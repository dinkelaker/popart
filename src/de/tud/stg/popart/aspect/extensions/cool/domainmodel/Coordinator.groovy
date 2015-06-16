///////////////////////////////////////////////////////////////////////////////
// Copyright 2008-2015, Technische Universitaet Darmstadt (TUD), Germany
//
// The TUD licenses this file to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
///////////////////////////////////////////////////////////////////////////////
package de.tud.stg.popart.aspect.extensions.cool.domainmodel;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.cool.CoolDSL;
import de.tud.stg.popart.aspect.extensions.cool.CoolAspect;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Domain model layer (M) class representing a coordination unit.
 * @author Oliver Rehor
 */
public class Coordinator {
  
  public final static boolean mDebug = true;

  private static def aspect = { map, definition ->
    return new CCCombiner().eval(map,definition)
  }

  // per_class or per_object coordination? default is per_object coordination
  private boolean mIsPerClass = false;
  private List<Class> mCoordinatedClassNames = null;
  private List mObjectsResponsibleFor = [];
  
  private StateManager mStateManager = new StateManager();
  private ResolveHandler mResolveHandler = new ResolveHandler();
  
  // list of self- or mutual-exclusive methods and method managers
  private HashMap<String,Queue> mSelfexMethods = [];
  private HashMap<List<String>,Queue> mMutexMethods = [];
  private List mMethodManagers = [];
  
  // method name |-> isActiveInOtherThread
  private HashMap<String,MethodState> mCoordinatedMethods = [:];
  private HashMap<String,List<MethodManager>> mGuardedMethods = [:];
  
  private ReentrantLock mCoordinatorLock = new ReentrantLock();

  /**
   * This Closure checks exclusion constraints for a given method f
   * and returns true, if any constraint unsatisfied, false otherwise
   */
  private Closure mAnyExclusionConstraintUnmetFor = { f ->
    boolean unmet = false;
    
    // always check the selfex methods (even if no mutex methods exist)
    if (mSelfexMethods.size() > 0 && mSelfexMethods.containsKey(f))
      unmet |= mCoordinatedMethods[f].isActiveInOtherThread();
    
    if (mMutexMethods.keySet().empty)
      return unmet;
    
    mMutexMethods.keySet().each { mutexSet ->
      // if f is in the current mutex set but NOT in the selfex set:
      //   => f may run in another thread but not together with the rest
      //      of each mutex set it appears in
      if (mutexSet.contains(f) && !mSelfexMethods.containsKey(f))
        mutexSet.each { m ->
          if (m != f)
            unmet |= mCoordinatedMethods[m].isActiveInOtherThread();
        }
      // if f is NOT in the current mutex set but in the selfex set:
      //   => f must not run in another thread
      else if (!mutexSet.contains(f) &&
               mSelfexMethods.containsKey(f))
        unmet |= mCoordinatedMethods[f].isActiveInOtherThread();
      // if f is in both the selfex and the current mutex set:
      //   => f must not run in another thread and not together with the rest
      //      of each mutex set it appears in
      else if (mutexSet.contains(f) &&
               mSelfexMethods.containsKey(f))
        mutexSet.each { m ->
          unmet |= mCoordinatedMethods[m].isActiveInOtherThread();
        }
    } 
    return unmet;
  }
  
  /**
   * This constructor takes a list of class names and creates
   * a per_object coordinator by that
   * @param coordinatedClassNames  the class names this coordinator handles
   */
  public Coordinator(List<Class> coordinatedClassNames) {
    mCoordinatedClassNames = coordinatedClassNames;
    mIsPerClass = false;
  }
  
  /**
   * This constructor takes a list of class names, a boolean flag and creates
   * a per_class coordinator by that.
   * @param coordinatedClassNames  the class names this coordinator handles
   * @param isPerClass  if true, a per_class coordinator is created
   */
  public Coordinator(List<Class> coordinatedClassNames, boolean isPerClass) {
    this(coordinatedClassNames);
    mIsPerClass = isPerClass;
  }
  
  /**
   * If a method is guarded by method managers this method evaluates possibly
   * defined requires statements. If any requires statement evaluates to false,
   * this method returns true, otherwise false. Note that there is only allowed
   * one requires statement for all method managers. But any method manager
   * having none returns true when questioned whether its requires statement is
   * true, such that all results can be traited with logical and.
   * @param guardedMethod    the currently intercepted method
   * @param theTargetObject  the object which interceptedMethod belongs to.
   *                         the ResolveHandler takes it such that it can
   *                         correctly resolve access to any defined members
   *                         in the coordinated class.
   * @return   true, if any requires statement unsatisfied, false otherwise
   * 
   */
  private boolean requiresClosureUnmetFor(String guardedMethod, Object theTargetObject) {
    if (mGuardedMethods.containsKey(guardedMethod)) {
      boolean allRequiresFulfilled = true;
      getResolveHandler().setTargetObject(theTargetObject);
      mGuardedMethods[guardedMethod].each { mm ->
        allRequiresFulfilled &= mm.requiresSatisfied(getResolveHandler());
      }
      return !allRequiresFulfilled;
    }
    else
      return false;
  }
  
  /**
   * This method is invoked directly before a coordinated method would be
   * executed. It checks whether the intercepted method may run by checking
   * all defined exclusion constraints and requires statements. If it may not
   * run, this method here suspends the current thread and once it is notifed,
   * rechechs the running criteria. If the method may run, it evaluates all
   * referring on_entry statements, if any defined, and then updates the method
   * state used to determine whether exclusive methods may run or not. 
   * @param interceptedMethod   the Method pending to execute
   * @param theTargetObject     the object which interceptedMethod belongs to.
   *                            it is needed when evaluating the
   *                            requires and on_entry statements since they
   *                            might refer to fields of the coordinated object
   */
  private void enterCoordinatedMethod(String interceptedMethod, Object theTargetObject) {
    mCoordinatorLock.lock();
    try {
      if (mDebug) CoolDSL.debugMsg("Coord",
        "Checking constraints for '" + interceptedMethod + "'");
      // while exclusion criteria not met, suspend thread:
      while (mAnyExclusionConstraintUnmetFor(interceptedMethod) || requiresClosureUnmetFor(interceptedMethod, theTargetObject)) {
        try {
          if (mDebug) CoolDSL.debugMsg("Coord",
            "Unmet constraints for '" + interceptedMethod + "'");
          if (mDebug) CoolDSL.debugMsg("Coord",
            "Suspending thread " + Thread.currentThread().getId());
          //wait();
          AspectManager.getInstance().wait(mCoordinatorLock);
        } catch (InterruptedException ie) {}
        if (mDebug) CoolDSL.debugMsg("Coord",
          "Rechecking constraints for '" + interceptedMethod + "'");
      }
      // method may run now:
      if (mDebug) {
        CoolDSL.debugMsg("Coord",
          "Constraints (now) met for '" + interceptedMethod + "'");
        CoolDSL.debugMsg("Coord",
          "Continuing thread " + Thread.currentThread().getId());
      }
      if (mGuardedMethods.containsKey(interceptedMethod)) {
        if (mDebug) CoolDSL.debugMsg("Coord",
          "Calling on_entry for '" + interceptedMethod + "'");
        getResolveHandler().setTargetObject(theTargetObject);
        mGuardedMethods[interceptedMethod].each { aResponsibleMethodManager ->
          aResponsibleMethodManager.callOnEntry(getResolveHandler());
        }
      }
      
      if (mDebug) CoolDSL.debugMsg("Coord",
        "Entering '" + interceptedMethod + "' in " + Thread.currentThread().getId());
      mCoordinatedMethods[interceptedMethod].enter();
    } finally {
      mCoordinatorLock.unlock();
    }
  }

  /**
   * This method is invoked directly after a coordinated method finishes. It
   * updates the method state used to determine whether exclusive methods
   * may run or not. Then it evaluates on_exit statements, if any defined.
   * Finally it urges currently waiting threads to recheck their constraints
   * since ending this method might now allow a thread to be run.
   * @param interceptedMethod   the Method which was just executed
   * @param theTargetObject     the object which interceptedMethod belongs to.
   *                            it is needed when evaluating the on_exit
   *                            statement since it might refer to fields of the
   *                            coordinated object
   */
  private void leaveCoordinatedMethod(String interceptedMethod, Object theTargetObject) {
    mCoordinatorLock.lock();
    try {
      if (mDebug) CoolDSL.debugMsg("Coord",
        "Leaving '" + interceptedMethod + "' in " + Thread.currentThread().getId());
      mCoordinatedMethods[interceptedMethod].leave();
    
      if (mGuardedMethods.containsKey(interceptedMethod)) {
        if (mDebug) CoolDSL.debugMsg("Coord",
          "Calling on_exit for '" + interceptedMethod + "'");
        getResolveHandler().setTargetObject(theTargetObject);
        mGuardedMethods[interceptedMethod].each { aResponsibleMethodManager ->
          aResponsibleMethodManager.callOnExit(getResolveHandler());
        }
      }
      
      if (mCoordinatedMethods[interceptedMethod].getDepth() == 0) {
        if (mDebug) {
          CoolDSL.debugMsg("Coord",
            "Finished all actions for '" + interceptedMethod + "'.");
          CoolDSL.debugMsg("Coord",
            "Force all waiting threads to recheck constraints");
        }
        AspectManager.getInstance().notifyAll(mCoordinatorLock);
        CoolDSL.debugMsg("Coord", "Done notifyAll.");
      }
    } finally {
      mCoordinatorLock.unlock();
    }
  }

  /**
   * This method creates an aspect consisting of a before advice executed
   * before each method call of any method occurring in the coordinator.
   * In this advice, it triggers all neccessary coordinator actions when
   * entering a coordinated method.
   * @return  the aspect defined in this method
   */
  private Aspect getBeforeCoordinatedMethodAspect() {
	println "COOL: Define getBeforeCoordinatedMethodAspect";
    CoolAspect a = aspect(name:"coolBeforeCoordinatedMethod") {
      mCoordinatedMethods.keySet().each { coordinatedMethod -> 
        println "COOL: Define cool before pc for $coordinatedMethod";
        before(method_call(coordinatedMethod)) {
          enterCoordinatedMethod(coordinatedMethod, targetObject);
        }
      }
    }
    applyPossibleDeployPerInstance(a);
    a.setCoordinator(this);
    return a;
  }

  /**
   * This method creates an aspect consisting of an after advice executed
   * after each method call of any method occurring in the coordinator.
   * In this advice, it triggers all neccessary coordinator actions when
   * leaving a coordinated method.
   * @return  the aspect defined in this method
   */
  private Aspect getAfterCoordinatedMethodAspect() {
    println "COOL: Define getAfterCoordinatedMethodAspect";
    Aspect a = aspect(name:"coolAfterCoordinatedMethod") {
      mCoordinatedMethods.keySet().each { coordinatedMethod -> 
        println "COOL: Define cool after pc for $coordinatedMethod";
        after(method_call(coordinatedMethod)) {
          leaveCoordinatedMethod(coordinatedMethod, targetObject);
        }
      }
    }
    applyPossibleDeployPerInstance(a);
    a.setCoordinator(this);
    return a;
  }

  /**
   * Tells coordinator and resolve handler to use a specific StateManager.
   * @param the StateManager which should be used henceforth
   */
  public void setStateManager(StateManager sm) {
    mStateManager = sm;
    mResolveHandler.setStateManager(mStateManager);
  }
  
  /**
   * Returns the coordinator's resolve handler dealing with variable
   * assignment and resolution.
   * @return the resolve handler
   */
  public ResolveHandler getResolveHandler() {
    return mResolveHandler;
  }
  
  /**
   * Via this method, a per_object coordinator gets an object it should be
   * responsible for.
   * @param obj an object, the (per_object) coordinator manages
   */
  public void addObjectResponsibleFor(Object obj) {
    mObjectsResponsibleFor.add(obj);
  }
  
  /**
   * Triggers registration of the aspects neccessary for selfex, mutex and
   * method manager statement semantics.
   */
  public void registerAspects() {
    //registerAspectsWithCoordinationStrategyOne();
    registerAspectsWithCoordinationStrategyTwo();
  }

  /**
   * Tells the coordinator to use a design different to Lopez thesis.
   */
  private void registerAspectsWithCoordinationStrategyOne() {
    if (mSelfexMethods.size() > 0)
      registerAspectPair(
        createBeforeExclusiveMethodAspects("selfx", mSelfexMethods),
        createAfterExclusiveMethodAspects("selfx", mSelfexMethods));
    if (mMutexMethods.size() > 0)
      registerAspectPair(
        createBeforeExclusiveMethodAspects("mutex", mMutexMethods),
        createAfterExclusiveMethodAspects("mutex", mMutexMethods));
    if (mMethodManagers.size() > 0)
      registerAspectPair(
        createBeforeMethodManagerAspect(),
        createAfterMethodManagerAspect());
  }

  /**
   * Tells the coordinator to behave similar to Lopez thesis.
   */
  private void registerAspectsWithCoordinationStrategyTwo() {
    registerAspectPair(getBeforeCoordinatedMethodAspect(),
                       getAfterCoordinatedMethodAspect());
  }
  
  /**
   * This helper method takes two aspects and registers both.
   * @param aspect1  first aspect
   * @param aspect2  second aspect
   */
  private void registerAspectPair(Aspect aspect1, Aspect aspect2) {
    AspectManager.getInstance().register(aspect1);
    AspectManager.getInstance().register(aspect2);
  }

  /**
   * Adds a list of methods self exclusion should be guaranteed for.
   * @param selfexMethods  the list of self exclusive method names
   */
  public void addSelfex(List<String> selfexMethods) {
    selfexMethods.each {
      mSelfexMethods[it] = new ConcurrentLinkedQueue();
      mCoordinatedMethods[it] = new MethodState();
    }
  }
  
  /**
   * Adds a list of methods mutual exclusion should be guaranteed for.
   * @param mutexMethods  the list of mutual exclusive method names
   */
  public void addMutex(List<String> mutexMethods) {
    if (!mMutexMethods.containsKey(mutexMethods)) {
      mMutexMethods[mutexMethods] = new ConcurrentLinkedQueue();
      mutexMethods.each {
        mCoordinatedMethods[it] = new MethodState();
      }
    }
    else
      CoolDSL.debugMsg("Coord", "Warning: ignoring redefinition of mutex set.");
  }
  
  /**
   * Adds a method manager the coordinator should use.
   * @param mm  the method manager
   */
  public void addMethodManager(MethodManager mm) {
    mMethodManagers.add(mm);
    mm.methods().each {
      mCoordinatedMethods[it] = new MethodState();
      if (!mGuardedMethods.containsKey(it))
        mGuardedMethods[it] = [mm];
      else
        mGuardedMethods[it].add(mm);
    }
  }
  
  /**
   * Adds a HashMap of condition variables and its bindings which should be
   * used in the current StateManager.
   * @param vars  a HashMap of the form
   *              "condition variable name -> condition variable value"
   */
  public void addCondVars(HashMap vars) {
    mStateManager.addCondVars(vars);
  }
  
   /**
    * Adds a HashMap of variables and its bindings which should be
    * used in the current StateManager.
    * @param vars  a HashMap of the form
    *              "variable name -> variable value"
    */
  public void addVars(HashMap vars) {
    mStateManager.addVars(vars);
  }
  
  /**
   * Checks whether this is a per_object coordinator and if so, a given aspect
   * is deployed for each instance the coordinator is responsible for.
   * @param theAspect  the aspect which might be deployed per instance
   */
  private void applyPossibleDeployPerInstance(Aspect theAspect) {
    if (mIsPerClass)
      return;
    mObjectsResponsibleFor.each {
      if (mDebug) {
        CoolDSL.debugMsg("Coord perObj",
          "deploying '" + theAspect.name + "' per instance:");
        CoolDSL.debugMsg("Coord perObj",
          "  -> " + it.class.getSimpleName().toString() + ":" + it.hashCode());
      }
      if (it != null)
        theAspect.deployPerInstance(it);
    }
  }
    
  /**
   * Create and return an aspect handling mutual or self exclusion of methods.
   * Before any mutual or self exclusive method call, it is determined
   * whether or not the method is allowed to run. If not, it is blocked until
   * it may run.
   * @param exclusionType  "selfx", if self exclusion should be considered.
   *                       "mutex", otherwise
   * @param methodList     a list of method names the exclusion constraints
   *                       should be checked for
   */
  private Aspect createBeforeExclusiveMethodAspects(String exclusionType,
                                                    HashMap methodList) {
    assert (methodList == mSelfexMethods || methodList == mMutexMethods);
    Aspect beforeExclusiveMethodsAspect =
      aspect(name: "coolBefore_" + exclusionType + "Methods") {
        methodList.each { methodEntry -> 
          Closure aspectDefinition = { methodName ->
            before(method_call(methodName)) {
              def Thread callingThread = Thread.currentThread();
              synchronized (methodEntry) {
                CoolDSL.debugMsg("Coord  " + exclusionType, method + ":" + signature);
                if (mDebug)
                  CoolDSL.debugMsg("Coord  " + exclusionType, "method call '" +\
                    methodName + "'" + (mIsPerClass ? "" : "@object_" +\
                    targetObject.hashCode()) + " by thread_" + callingThread.getId());
                def waitingThreads = methodEntry.getValue();
                if (waitingThreads.peek().equals(callingThread)) {
                  assert exclusionType != "mutex";
                  if (mDebug)
                    CoolDSL.debugMsg("Coord  " + exclusionType,
                      "same thread legally calls '" +\
                      methodName + "' again (reentrant/recursive).");
                }
                else if (waitingThreads.peek() == null) {
                  if (mDebug)
                    CoolDSL.debugMsg("Coord  " + exclusionType,
                        "currently no thread locking '" + methodName + "'" +\
                        (mIsPerClass ? "" : "@object_" + targetObject.hashCode()) +\
                        ", running thread_" + callingThread.getId());
                  waitingThreads.add(callingThread);
                }
                else {
                  // now suspend callingThread:
                  if (mDebug)
                    CoolDSL.debugMsg("Coord  " + exclusionType,
                      "another thread locks '" + methodName + "'" +\
                      (mIsPerClass ? "" : "@object_" + targetObject.hashCode()) +\
                      ", suspending thread_" + callingThread.getId());
                  waitingThreads.add(callingThread);
                  //methodEntry.wait();
                  AspectManager.getInstance().wait(methodEntry);
                }
              }
            }
          }
          if (exclusionType == "mutex")
            // then methodEntry has a list of method names as key
            methodEntry.getKey().each aspectDefinition;
          else // otherwise we only have one method name as key
            aspectDefinition(methodEntry.getKey());
        }
      }
    applyPossibleDeployPerInstance(beforeExclusiveMethodsAspect);
    return beforeExclusiveMethodsAspect;
  }
  
  /**
   * Create and return an aspect handling mutual or self exclusion of methods.
   * After any mutual or self exclusive method call, possible other blocked
   * methods waiting on the current method to finish are notified and can
   * then run.
   * @param exclusionType  "selfx", if self exclusion should be considered.
   *                       "mutex", otherwise
   * @param methodList     a list of method names the exclusion constraints
   *                       should be checked for
   */
  private Aspect createAfterExclusiveMethodAspects(String exclusionType,
                                                   HashMap methodList) {
    assert (methodList == mSelfexMethods || methodList == mMutexMethods);
    Aspect afterExclusiveMethodsAspect =
      aspect(name: "coolAfter_" + exclusionType + "Methods") {
        methodList.each { methodEntry -> 
          Closure aspectDefinition = { methodName ->
            after(method_call(methodName)) {
              if (mDebug)
                CoolDSL.debugMsg("Coord  " + exclusionType,
                  exclusionType + ": method call finished for '" + methodName +\
                  "'" + (mIsPerClass ? "" : "@object_" + targetObject.hashCode()) +\
                  " from thread_" + Thread.currentThread().getId());
              
              synchronized(methodEntry) {
                def waitingThreads = methodEntry.getValue();
                waitingThreads.poll();
                Thread nextThread = waitingThreads.peek();
                if (nextThread == null) {
                  if (mDebug)
                    CoolDSL.debugMsg("Coord  " + exclusionType,
                      exclusionType + ": no other thread waiting on '" +\
                      methodName + "'" +\
                      (mIsPerClass ? "" : "@object_" + targetObject.hashCode()));
                }
                else
                  if (mDebug)
                    CoolDSL.debugMsg("Coord  " + exclusionType,
                      exclusionType + ": resuming thread_" + nextThread.getId() +\
                      " waiting on '" + methodName + "'" +\
                      (mIsPerClass ? "" : "@object_" + targetObject.hashCode()));
                methodEntry.notify();
              }
            }
          }
          if (exclusionType == "mutex")
            // then methodEntry has a list of method names as key
            methodEntry.getKey().each aspectDefinition;
          else // otherwise we only have one method name as key
            aspectDefinition(methodEntry.getKey());
        }
      }
    applyPossibleDeployPerInstance(afterExclusiveMethodsAspect);
    return afterExclusiveMethodsAspect;
  }
  
  /**
   * Assignment of condition variables in a method manager's onEntry
   * or onExit block can cause a suspended thread to run: his precondition
   * may now be satisfied. This method rechecks all preconditions and
   * notifies each thread whose precondition is now satisfied.
   * @param advisedMethodName  the method name of the currently running method
   *                           which must not be rechecked
   */
  private void recheckPreconditions(String advisedMethodName) {
    assert mMethodManagers != null;
    assert advisedMethodName != null && !advisedMethodName.equals("");
    mMethodManagers.each { manager ->
      manager.methods().each { otherManagedMethod ->
        if (!otherManagedMethod.equals(advisedMethodName)) {
          if (mDebug)
            CoolDSL.debugMsg("Coord  guard",
              "rechecking preconditions for '" + otherManagedMethod + "'.");

          if (manager.evalRequired(getResolveHandler(), true)) {
            if (mDebug)
              CoolDSL.debugMsg("Coord  guard",
                "rechecking allows '" + otherManagedMethod +\
                "' to be run, notifying next thread waiting on.");
            
            synchronized (otherManagedMethod) {
              otherManagedMethod.notify();
            }
          }
        }
      }
    }
  }
  
  /**
   * Create and return an aspect handling method manager constraints.
   * Before any method call associated to a method manager,
   * the condition closure defined in the manager is evaluated with the current
   * state. If it yields true, the method may run, otherwise it is blocked.
   * But before it is run, its on_entry closure is called and, once finished,
   * all preconditions of all managed methods are rechecked since another one
   * may run now.
   */
  private Aspect createBeforeMethodManagerAspect() {
    def methodManagers = mMethodManagers;
    Aspect beforeMethodManagerAspect = aspect(name:"coolBeforeGuardedMethod") {
      assert mMethodManagers != null;
      mMethodManagers.each { currentManager ->
        List<String> managedMethods = currentManager.methods();
        managedMethods.each { aManagedMethod ->
          before(method_call(aManagedMethod)) {
            synchronized (aManagedMethod) {
              getResolveHandler().setTargetObject(targetObject);
              boolean canRun =
                currentManager.evalRequired(getResolveHandler(), false);
              if (canRun) {
                if (mDebug)
                 CoolDSL.debugMsg("Coord  guard",
                    "precondition satisfied for '" + aManagedMethod +\
                    "', running it.");
                getResolveHandler().setTargetObject(targetObject);
                currentManager.evalOnEntry(getResolveHandler());
                recheckPreconditions(aManagedMethod);
              }
              else {
                if (mDebug)
                  CoolDSL.debugMsg("Coord  guard",
                    "unsatisfied precondition for '" + aManagedMethod +\
                    "', suspending its thread.");
                
                // tell mutex and selfex methods they can run since thread is
                // suspended (formerly exclusive method actually not running)
                def methodMutexLock = null;
                def methodSelfexLock = null;
                def Thread callingThread = Thread.currentThread();
                boolean canRunNow = false;
                
                while (!canRunNow) {

                  mSelfexMethods.each { x ->
                    if (x.getKey() == aManagedMethod)
                      methodSelfexLock = x;
                  }
              
                  if (methodSelfexLock != null) {
                    synchronized (methodSelfexLock) {
                      def waitingThreads = methodSelfexLock.getValue();
                      waitingThreads.poll();
                      Thread nextThread = waitingThreads.peek();
                      methodSelfexLock.notify();
                    }
                  }
              
                  mMutexMethods.each { x ->
                    x.getKey().each { y ->
                      if (y == aManagedMethod)
                        methodMutexLock = x;
                    }
                  }
                  if (methodMutexLock != null)
                    synchronized(methodMutexLock) {
                      def waitingThreads = methodMutexLock.getValue();
                      waitingThreads.poll();
                      Thread nextThread = waitingThreads.peek();
                      methodMutexLock.notify();
                    }
                
                  AspectManager.getInstance().wait(aManagedMethod);
                
                  // recheck exclusion criteria and suspend thread if not met
                  if (methodMutexLock != null)
                    synchronized (methodMutexLock) {
                      def waitingThreads = methodMutexLock.getValue();
                      if (waitingThreads.peek() == null) {
                        waitingThreads.add(callingThread);
                      }
                      else {
                        waitingThreads.add(callingThread);
                        AspectManager.getInstance().wait(methodMutexLock);
                      }
                    }
                
                  if (methodSelfexLock != null)
                    synchronized (methodSelfexLock) {
                      def waitingThreads = methodSelfexLock.getValue();
                      if (waitingThreads.peek().equals(callingThread)) {
                        // reentrance, allowed and nothing to do.
                      }
                      else if (waitingThreads.peek() == null) {
                        waitingThreads.add(callingThread);
                      }
                      else {
                        waitingThreads.add(callingThread);
                        AspectManager.getInstance().wait(methodSelfexLock);
                      }
                    }
                  canRunNow = currentManager.evalRequired(getResolveHandler(),
                                                          false);
                }
                
                if (mDebug)
                  CoolDSL.debugMsg("Coord  guard",
                    "resuming formerly blocked '" + aManagedMethod + "'");
                getResolveHandler().setTargetObject(targetObject);
                currentManager.evalOnEntry(getResolveHandler());
                recheckPreconditions(aManagedMethod);
              }
            }
          }
        }
      }
    }
    applyPossibleDeployPerInstance(beforeMethodManagerAspect);
    return beforeMethodManagerAspect;
  }
  
  /**
   * Create and return an aspect handling method manager constraints.
   * After any method call associated to a method manager,
   * the on_exit closure is called and, once finished, all preconditions
   * of all managed methods are rechecked since another one may run now.
   */
  private Aspect createAfterMethodManagerAspect() {
    def methodManagers = mMethodManagers;
    Aspect afterMethodManagerAspect = aspect(name:"coolAfterGuardedMethod") {
      assert mMethodManagers != null;
      mMethodManagers.each { currentManager -> 
        List<String> managedMethods = currentManager.methods();
        managedMethods.each { aManagedMethod ->
          after(method_call(aManagedMethod)) {
            synchronized (aManagedMethod) {
              getResolveHandler().setTargetObject(targetObject);
              currentManager.evalOnExit(getResolveHandler());
              recheckPreconditions(aManagedMethod);
              if (mDebug)
                CoolDSL.debugMsg("Coord  guard",
                  "notifying other threads possibly waiting on '" +\
                  aManagedMethod);
              aManagedMethod.notify();
            }
          }
        }
      }
    }
    applyPossibleDeployPerInstance(afterMethodManagerAspect);
    return afterMethodManagerAspect;
  }
  
  public String toString(Object targetObject) {
	  String str = "Coordinator "+(mIsPerClass? "per class" : "per object") + "\n";
	  str += "  coordinated classes: " + mCoordinatedClassNames + "\n";
	  str += "  coordinated objects: " + mObjectsResponsibleFor + "\n";
	  str += "  state manager: " + mStateManager + "\n";
	  str += "  selfex: " + mSelfexMethods + "\n";
	  str += "  mutex: " + mMutexMethods + "\n";
	  str += "  guard: ";
	  for (String methodName : mGuardedMethods.keySet()) {
		  str += "\n    "+methodName+":[";
		  List<MethodManager> mml = mGuardedMethods.get(methodName)

		  mResoveHandler.targetObject = targetObject; 
		  
		  for (MethodManager mm : mml) {
  		      try {
  		          Closure required = mm.getRequiredClone();
  		          required.delegate = mResolveHandler;
  		          required.resolveStrategy = Closure.DELEGATE_ONLY;
  		          boolean requiredResult = required.call()
		          str += "required guard="+requiredResult;
		      } catch (Exception e) {
			      str += "required guard=N/A";
		      }
		  
		      str += ", "
		    
		      try {
		          Closure onEntry = mm.getRequiredClone();
		          onEntry.delegate = mResolveHandler;
		          onEntry.resolveStrategy = Closure.DELEGATE_ONLY;
		          boolean onEntryResult = onEntry.call()
		          str += "on entry guard="+onEntryResult;
		      } catch (Exception e) {
			      str += "on entry guard=N/A";
		      }

		      str += ", "

		      try {
		          Closure onExit = mm.getRequiredClone();
		          onExit.delegate = mResolveHandler;
		          onExit.resolveStrategy = Closure.DELEGATE_ONLY;
		          boolean onExitResult = onExit.call()
		          str += "on exit guard="+onExitResult;
		      } catch (Exception e) {
			      str += "on exit guard=N/A";
		      }
		  }
	  }
	  str += "]\n";
	  return str;
  }
  
  public String toString() {
	  String str = "Coordinator "+(mIsPerClass? "per class" : "per object") + "\n";
	  str += "  coordinated classes: " + mCoordinatedClassNames + "\n";
	  str += "  coordinated objects: " + mObjectsResponsibleFor + "\n";
	  str += "  state manager: " + mStateManager + "\n";
	  str += "  selfex: " + mSelfexMethods + "\n";
	  str += "  mutex: " + mMutexMethods + "\n";
	  str += "  guard: (dynamic result not available)" + mGuardedMethods + "\n";
	  return str;
  }
}
