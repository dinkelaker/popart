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
package de.tud.stg.popart.aspect.extensions.cool;

import java.util.Formatter;
import java.lang.StringBuilder;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.cool.domainmodel.*;

/**
 * Interpreter layer (I) class that handles interpretation of COOL EDSL
 * programs utilizing Groovy's Meta Object Protocol.
 * @author Oliver Rehor
 */
public class CoolDSL implements ICoolDSL {
  
  // indicates whether or not to print debugging info
  public final static boolean mDebug = true;
  
  // shorthand notation for the definition of an aspect
  private static def aspect = { map, definition ->
    return new CCCombiner().eval(map,definition)
  }
  
  // these members hold the relevant information extracted from the COOL code
  private List<String> mSelfexMethods = [];
  private List<List<String>> mMutexMethods = [];
  private List<MethodManager> mMethodManagers = [];
  private StateManager mInitialStateManager = new StateManager();
  
  // holds the currently interpreted method manager
  private MethodManager mCurrentMethodManager = null;
  private HashMap mMethodManagerValidity = [:];

  // some members to ensure correct order and interpretation of statements
  private boolean mIsPerClass = false;
  private boolean mSelfexPresent = false;
  private boolean mOnEntryFound = false;
  private boolean mOnExitFound = false;
  private boolean mMethodManagerStatementFound = false;
  
  /*
   * Default constructor (with per object instantiation policy)
   */
  public CoolDSL() {
  }
  
  /**
   * Create a new COOL interpreter, whose instantiation policy is determined by the boolean flag
   * @param isPerClass indicates, whether this is a per class or per object interpreter
   */
  public CoolDSL(boolean isPerClass) {
    this();
    mIsPerClass = isPerClass;
  }
  
  /**
   * Return a closure that manages the actual call to a COOL interpreter.
   * This closure can be bound to a bootstrap keyword, for instance:
   * def coordinator = CoolDSL.getInterpreter();
   * @return a closure with an interpreter call
   */
  public static Closure getInterpreter() {
    return { params, body ->
             ICoolDSL coolInterpreter = new CoolDSL();
             return coolInterpreter.eval(params, body) };
  }
  
  /**
   * Return a closure that manages the actual call to a per_class
   * COOL interpreter.
   * This closure can be bound to a bootstrap keyword, for instance:
   * def coordinator = CoolDSL.getInterpreter();
   * @return a closure with an interpreter call
   */
  public static Closure getPerClassInterpreter() {
    return { classNames, body ->
             ICoolDSL coolInterpreter = new CoolDSL(true);
             return coolInterpreter.eval(classNames, body);};
  }
  
  /**
   * Main entry point of the COOL interpreter. This method creates a
   * {@link Coordinator} object representing the coordinator this method
   * is called for. Keywords found in this coordinator's body are mapped
   * to method names of {@link CoolDSL}.
   * @param classNames       class names the current coordinator is
   *                         responsible for
   * @param coordinatorBody  the body of the coordinator containing COOL
   *                         statements/language constructs
   */
  public void eval(List<Class> classNames, Closure coordinatorBody) {
    preEvalActions(classNames);
	
    coordinatorBody.delegate = this;
    coordinatorBody.resolveStrategy = Closure.DELEGATE_FIRST;
    coordinatorBody.call();
    
    postEvalActions(classNames);
  }
  
  /**
   * Performs actions, that must be done before evaluating the bootstrap
   * closure. Must be a public, separate method, such that external,
   * combining interpreters can call it before evaluation their bootstrap
   * closure.
   * @param the list of current coordinator's coordinated classes
   */
  public void preEvalActions(List<Class> classNames) {
    if (!mIsPerClass && classNames.size() > 1)
      throw new MalformedStatementException("coordinator",
        "per_object coordination not allowed for multiple classes.");
  }
   
  /**
   * Performs actions, that must be done after evaluating the bootstrap
   * closure. Must be a public, separate method, such that external,
   * combining interpreters can call it after evaluation their bootstrap
   * closure.
   * @param the list of current coordinator's coordinated classes
   */
  public void postEvalActions(List<Class> classNames) {
    if (!mIsPerClass) { // per object instantiation policy
      AspectManager.getInstance().register(
        aspect(name:"coolAfterObjectConstruction") {
          // only take the first item from the list since we are in per object
	        // case here (per object multi class coordination is not allowed and
          // already caught above):
          String classSimpleName = classNames.first().getSimpleName();
          String className = classNames.first().getName();
          
          // intercept each instantiation of the coordinated class and create
	        // a seperate coordinator responsible for this instance.
          after(initialization(className)) {
            
            final Object referenceTargetObject = targetObject;
            //assert referenceTargetObject == targetObject;
            assert referenceTargetObject.is(targetObject);
            
            if (CoolDSL.mDebug)
              debugMsg("CoolDSL",
                "intercepted constructor call of '" + classSimpleName +\
                "', new object: '" + targetObject.hashCode() + "'");
            
            //assert referenceTargetObject == targetObject;
            assert referenceTargetObject.is(targetObject);
            
            Coordinator coordinator = createCoordinator(classNames);
            coordinator.addObjectResponsibleFor(targetObject);
            
            coordinator.registerAspects();
            
            //assert referenceTargetObject == targetObject;
            assert referenceTargetObject.is(targetObject);
          }});
    }
    else { // per class instantiation policy
      Coordinator coordinator = createCoordinator(classNames);
      coordinator.registerAspects();
    }
  }
  
  /**
   * Helper method to simplify EDSL syntax for a coordinator only referring
   * to a single class. Creates a list containing only this class and calls
   * the true eval method.
   * @param className        the single class name the current coordinator is
   *                         responsible for
   * @param coordinatorBody  the body of the coordinator containing COOL
   *                         statements/language constructs
   */
  public void eval(Class className, Closure coordinatorBody) {
    eval([className], coordinatorBody);
  }
  
  /** In per_object case, all method names must be cloned
   * to ensure correct behaviour, because they serve as thread locks.
   * This helper method performs that.
   * @param orgList  the original list of string locks
   * @return  a new list with cloned string locks
   */
  private List<String> cloneLocks(List<String> orgList) {
    List<String> newList = [];
    orgList.each { listEntry ->
      newList.add(AuxiliaryState.deepCopy(listEntry));
    }
    return newList;
  }
   
  /**
   * Create and return a new coordinator. 
   * @param classNames  the class names the coordinator should coordinate
   * @return  the new coordinator
   */
  private Coordinator createCoordinator(List<Class> classNames) {
    Coordinator coordinator = new Coordinator(classNames, mIsPerClass);
    
    if (!mIsPerClass)
      coordinator.addSelfex(cloneLocks(mSelfexMethods));
    else
      coordinator.addSelfex(mSelfexMethods);
    
    if (!mIsPerClass)
      mMutexMethods.each {
        coordinator.addMutex(cloneLocks(it));
      }
    else
      mMutexMethods.each {
        coordinator.addMutex(it);
      }
    
    if (!mIsPerClass)
      coordinator.setStateManager(mInitialStateManager.clone());
    else
      coordinator.setStateManager(mInitialStateManager);
    
    mMethodManagers.each {
      if (!mIsPerClass) {
        MethodManager mm = new MethodManager(cloneLocks(it.methods()));
        mm.required(it.getRequiredClone());
        mm.onEntry(it.getOnEntryClone());
        mm.onExit(it.getOnExitClone());
        coordinator.addMethodManager(mm);
      }
      else
        coordinator.addMethodManager(it);
    }
    return coordinator;
  }

  /**
   * Keyword method to manage the synchronization state (condition variables).
   * Forwards variable declaration to the coordinator object.
   * @param vars  the hashmap containing the variable names and its initial
   *              value
   */
  public void condition(HashMap<String, Boolean> vars) {
    mInitialStateManager.addCondVars(vars);
  }
  
  /**
   * Keyword method to manage the synchronization state (condition variables).
   * Forwards variable declaration to the coordinator object.
   * (Alternative EDSL syntax)
   * @param initializers  a closure containing the variable names and their
   *                      initial assignment
   */
  public void condition(Closure initializers) {
    VarDefinitionHandler vdh =
      new VarDefinitionHandler(Boolean, mInitialStateManager);
    vdh.setIsCondVar();
    initializers.setResolveStrategy(Closure.DELEGATE_ONLY);
    initializers.setDelegate(vdh);
    initializers.call();
    condition(vdh.getVariables());
  }
  
  /**
   * Keyword method to manage the auxiliary state (ordinary variables)
   * Forwards variable declaration to the coordinator object.
   * (type inference style syntax)
   * @param vars  the hashmap containing the variable names and their initial
   *              value
   */
  public void var(HashMap<String, Object> vars) {
    mInitialStateManager.addVars(vars);
  }
  
  /**
   * Keyword method to manage the auxiliary state (ordinary variables)
   * Forwards variable declaration to the coordinator object.
   * @param vars  the hashmap containing the variable names and their initial
   *              value
   * @param type  the class of all variables defined by this statement
   */
  public void var(HashMap<String, Object> vars, Class type) {
    vars.each {
      def value = vars[it.key];
      if (!value.class.equals(type))
        throw new VariableAssignmentException(it.key, null, value,
            "Variable is declared to have the type '" + type + "' but " +\
            "assigned a value of type '" + value.class + "'");
    }
    mInitialStateManager.addVars(vars);
  }
  
  /**
   * Keyword method to manage the auxiliary state (ordinary variables)
   * Forwards variable declaration to the coordinator object.
   * (Alternative EDSL syntax)
   * @param type  the class for all variables defined by this statement
   * @param initializers  Closure defining variable names and their initial value
   */
  public void var(Class type, Closure initializers) {
    VarDefinitionHandler vdh =
      new VarDefinitionHandler(type, mInitialStateManager);
    initializers.setResolveStrategy(Closure.DELEGATE_ONLY);
    initializers.setDelegate(vdh);
    initializers.call();
    var(vdh.getVariables());
  }
  
  /**
   * Keyword method for handling self-exclusive methods (string list style):
   * "selfex ([ "name1", "name2" ]);"
   * @param methods list of self-exclusive methods for the current coordinator
   */
  @Deprecated
  public void selfex(List<String> methods) {
    if (mSelfexPresent)
      throw new ConflictingStatementException("selfex", "selfex",
        "'selfex' statement can only occur once per coordinator.");
    mSelfexMethods.addAll(methods)
    mSelfexPresent = true;
  }
  
  /**
   * Keyword method for handling self-exclusive methods (closure style):
   * "selfex { name1; name2 }"
   * @param methodNames  Closure containing semincolon separated method names
   */
  public void selfex(Closure methodNames) {
    if (mSelfexPresent)
      throw new ConflictingStatementException("selfex", "selfex",
        "'selfex' statement can only occur once per coordinator.");
    ExclusionStatementResolveHandler esrh =
      new ExclusionStatementResolveHandler();
    methodNames.setResolveStrategy(Closure.DELEGATE_ONLY);
    methodNames.setDelegate(esrh);
    methodNames.call();
    List<String> methods = esrh.getFoundMethodNames();
    if (CoolDSL.mDebug)
      debugMsg("CoolDSL", "selfex:: found " + methods);
    mSelfexMethods.addAll(methods);
    mSelfexPresent = true;
  }
  
  /**
   * Keyword method for handling mutual-exclusive methods (string list style):
   * "mutex ([ "name1", "name2" ]);"
   * @param methods   list of mutual-exclusive methods for the current
   *                  coordinator
   */
  @Deprecated
  public void mutex(List<String> methods) {
    if (methods.size() < 2)
      throw new MalformedStatementException("mutex",
        "'mutex' statement set must contain at least 2 method names.");
    mMutexMethods.add(methods);
  }
  
  /**
   * Keyword method for handling mutual-exclusive methods (closure style):
   * "mutex { name1; name2 }"
   * @param methodNames  Closure containing semincolon separated method names
   */
  public void mutex(Closure methodNames) {
    ExclusionStatementResolveHandler esrh =
      new ExclusionStatementResolveHandler();
    methodNames.setResolveStrategy(Closure.DELEGATE_ONLY);
    methodNames.setDelegate(esrh);
    methodNames.call();
    List<String> methods = esrh.getFoundMethodNames();
    if (methods.size() < 2)
      throw new MalformedStatementException("mutex",
        "'mutex' statement set must contain at least 2 method names.");
    if (CoolDSL.mDebug)
      debugMsg("CoolDSL", "mutex:: found " + methods);
    mMutexMethods.add(methods);
  }
   
   /**
    * Keyword method to handle guarded suspension and notification of threads.
    * Ensures correct order of requires/on_entry/on_exit statements in the
    * method manager definition block. Also checks whether the currently
    * interpreted method manager can coexist with all method managers already
    * read for this coordinator.
    * (Alternative EDSL Syntax)
    * @param methods          Closure containing the method names this manager
    *                         is responsible for
    * @param definitionBlock  body of the method manager, e.g. containing
    *                         'requires' statements
    */
   public void guard(Closure methods, Closure definitionBlock) {
     GuardDefinitionHandler gdh = new GuardDefinitionHandler();
     methods.setResolveStrategy(Closure.DELEGATE_ONLY);
     methods.setDelegate(gdh);
     methods.call();
     guard(gdh.getGuardedMethods(), definitionBlock);
   }
  
  /**
   * Keyword method to handle guarded suspension and notification of threads.
   * Ensures correct order of requires/on_entry/on_exit statements in the
   * method manager definition block. Also checks whether the currently
   * interpreted method manager can coexist with all method managers already
   * read for this coordinator.
   * @param methods          list of methods this manager is responsible for
   * @param definitionBlock  body of the method manager, e.g. containing
   *                         'requires' statements
   */
  public void guard(List<String> methods, Closure definitionBlock) {
    mCurrentMethodManager = new MethodManager(methods);
    mOnEntryFound = false;
    mOnExitFound = false;
    mMethodManagerStatementFound = true;
    
    definitionBlock.setResolveStrategy(Closure.DELEGATE_FIRST);
    definitionBlock.setDelegate(this);
    definitionBlock.call();
    
    HashMap m = mCurrentMethodManager.getValidityMap();
    HashMap validity = mMethodManagerValidity;
    m.each {
      if (!validity.containsKey(it.key) || !validity[it.key] && m[it.key])
        validity[it.key] = m[it.key];
      else if (validity[it.key] && m[it.key])
        throw new ConflictingStatementException("methodManager", "methodManager",
          "If a method name occurs in several methodManagers, there can be" +\
          "at most one requires statement.");
    };
    mMethodManagers.add(mCurrentMethodManager);
    mMethodManagerStatementFound = false;
  }
  
  /**
   * Keyword method to handle boolean expressions required to be true for a
   * method manager to run. The expression is NOT evaluated here, since
   * this must be done when the actual method is run the current manager is
   * responsible for. Instead, it is saved in the method manager.
   * @param requiredCondition  the condition that must be true in order to
   *                           let this method manager run
   */
  public void requires(Closure requiredCondition) {
    if (!mMethodManagerStatementFound)
      throw new BadStatementLocationException("requires",
        "found outside of a methodManager block!");
    if (mOnEntryFound || mOnExitFound)
      throw new BadStatementLocationException("requires",
        "'requires' must preceed 'on_entry' and 'on_exit' statements!");
    mCurrentMethodManager.required(requiredCondition);
  }
  
  /**
   * Keyword method to handle on_entry actions for a managed method.
   * The statements are NOT evaluated here, since
   * this must be done when the actual method is run the current manager is
   * responsible for. Instead, it is saved in the method manager.
   * @param onEntryActions  the code block that is run each time the managed
   *                        methods are entered
   */
  public void on_entry(Closure onEntryActions) {
    if (!mMethodManagerStatementFound)
      throw new BadStatementLocationException("on_entry",
        "found outside of a methodManager block!");
    if (mOnExitFound)
      throw new BadStatementLocationException("on_entry",
        "'on_entry' must preceed 'on_exit' statement!");
    mCurrentMethodManager.onEntry(onEntryActions);
    mOnEntryFound = true;
  }
  
  /**
   * Keyword method to handle on_exit actions for a managed method.
   * The statements are NOT evaluated here, since
   * this must be done when the actual method is left the current manager is
   * responsible for. Instead, it is saved in the method manager.
   * @param onEntryActions  the code block that is run each time the managed
   *                        methods are left
   */
  public void on_exit(Closure onExitActions) {
    if (!mMethodManagerStatementFound)
      throw new BadStatementLocationException("on_exit",
        "found outside of a methodManager block!");
    mCurrentMethodManager.onExit(onExitActions);
    mOnExitFound = true;
  }
  
  /**
   * Print debugging info
   * @param module a string of max 12 chars representing the current submodule
   * @param message the message to print
   */
  public static void debugMsg(String module, String message) {
    StringBuilder sb = new StringBuilder();
    Formatter fm = new Formatter(sb, Locale.US);
    fm.format("COOL    [%02d] %-12s | %s",
      Thread.currentThread().getId(), module, message);
    println sb;
  }

  /*
  void setProperty(String name, Object value) {
    throw new BadStatementLocationException(
      "variable assignment [$name := $value]",
      "found outside of {requires, on_entry, on_exit}");
  }
  
  Object getProperty(String name) {
    throw new BadStatementLocationException("variable resolution [$name]",
      "found outside of {requires, on_entry, on_exit}");
  }*/
}
