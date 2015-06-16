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

import de.tud.stg.popart.aspect.extensions.cool.CoolDSL;

/**
 * Domain model layer (M) class responsible for guarded suspension
 * and notification of threads.
 * @author Oliver Rehor
 */
public class MethodManager {
  
  public final static boolean mDebug = true;

  private List<String> mMethods = [];

  private Closure mRequired = null;
  private Closure mOnEntry = null;
  private Closure mOnExit = null;
  
  private HashMap<Long,Boolean> mCanExecute = [:];
  private boolean mOneOfManagedMethodsIsRunning = false;
  
  /**
   * Take a list of managed methods and create a method manager for them
   */
  public MethodManager(List<String> managedMethods) {
    mMethods = managedMethods;
  }
  
  /**
   * Here each method name
   * in this manager gets a boolean flag indicating whether this manager
   * has a requires statement. This hashmap can be used to ensure that there
   * is at most one occurence of 'requires' for the same method name across
   * different method managers.
   * @return a hashmap [method name] -> ['requires' boolean flag]
   */
  public HashMap getValidityMap() {
    HashMap m = [:];
    mMethods.each {
      m[it] = (mRequired != null);
    }
    return m;
  }
  
  /**
   * Saves the 'requires' code block for evaluation at runtime
   * @param defBlock   the code definition block
   */
  public void required(Closure defBlock) {
    mRequired = defBlock;
  }
  
  /**
   * Saves the 'onEntry' code block for evaluation at runtime
   * @param defBlock   the code definition block
   */
  public void onEntry(Closure defBlock) {
    mOnEntry = defBlock;
  }
  
  /**
   * Saves the 'onExit' code block for evaluation at runtime
   * @param defBlock   the code definition block
   */
  public void onExit(Closure defBlock) {
    mOnExit = defBlock;
  }
  
  public Closure getRequiredClone() {
    if (mRequired==null)
      return null;
    return mRequired.clone();
  }
  
  public Closure getOnEntryClone() {
    if (mOnEntry==null)
      return null;
    return mOnEntry.clone();
  }
  
  public Closure getOnExitClone() {
    if (mOnExit==null)
      return null;
    return mOnExit.clone();
  }
  
  /**
   * Evaluates the 'requires' closure of this method manager.
   * @param resolveHandler  variables are passed to and resolved by this handler
   * @return evaluation result of the closure, must be boolean
   */
  public synchronized boolean evalRequired(ResolveHandler resolveHandler, boolean checkOnly) {
    assert resolveHandler != null;
    if (mRequired == null && !checkOnly) {
      mCanExecute[Thread.currentThread().getId()] = true;
      return true;
    }
    else if (mRequired == null)
      return true;
    // only condition variables are allowed to appear in this closure:
    resolveHandler.setResolveOnlyCondVars(true);
    mRequired.setDelegate(resolveHandler);
    mRequired.setResolveStrategy(Closure.DELEGATE_ONLY);
    if (mDebug)
      CoolDSL.debugMsg("MethM    req", "'requires' call for " + mMethods);
    
    final boolean canExecute = mRequired.call();
    resolveHandler.setResolveOnlyCondVars(false);
    
    if (!checkOnly)
      mCanExecute[Thread.currentThread().getId()] = canExecute;
    
    return canExecute;
  }

  /**
   * Evaluates the 'on_entry' closure of this method manager.
   * @param resolveHandler  variables are passed to and resolved by this handler
   * @return evaluation result of the closure, must be boolean
   */
  public synchronized boolean evalOnEntry(ResolveHandler resolveHandler) {
    assert resolveHandler != null;
    if (!mCanExecute[Thread.currentThread().getId()]) {
      if (mDebug)
        CoolDSL.debugMsg("MethM  onEnt",
          "Blocked 'on_entry' call for " + mMethods);
      return false;
    }
    if (mOnEntry != null) {
      if (mDebug)
        CoolDSL.debugMsg("MethM  onEnt", "'on_entry' call for " + mMethods);
      mOnEntry.setDelegate(resolveHandler);
      mOnEntry.setResolveStrategy(Closure.DELEGATE_ONLY);
      mOneOfManagedMethodsIsRunning = true;
      return mOnEntry.call();
    }
    else
      return false;
  }

  public boolean requiresSatisfied(ResolveHandler resolveHandler) {
    if (mRequired == null)
      return true;
    // only condition variables are allowed to appear in this closure:
    resolveHandler.setResolveOnlyCondVars(true);
    mRequired.setDelegate(resolveHandler);
    mRequired.setResolveStrategy(Closure.DELEGATE_ONLY);
    final boolean canExecute = mRequired.call();
    resolveHandler.setResolveOnlyCondVars(false);
    return canExecute;
  }

  public Object callOnEntry(ResolveHandler resolveHandler) {
    if (mOnEntry == null)
      return null;
    mOnEntry.setDelegate(resolveHandler);
    mOnEntry.setResolveStrategy(Closure.DELEGATE_ONLY);
    return mOnEntry.call();
  }

  public Object callOnExit(ResolveHandler resolveHandler) {
    assert resolveHandler != null;
    if (mOnExit == null)
      return null;
    mOnExit.setDelegate(resolveHandler);
    mOnExit.setResolveStrategy(Closure.DELEGATE_ONLY);
    return mOnExit.call();
  }

  /**
   * Evaluates the 'on_exit' closure of this method manager.
   * @param resolveHandler  variables are passed to and resolved by this handler
   * @return evaluation result of the closure, must be boolean
   */
  public synchronized boolean evalOnExit(ResolveHandler resolveHandler) {
    assert resolveHandler != null;
    if (!mCanExecute[Thread.currentThread().getId()]) {
      if (mDebug)
        CoolDSL.debugMsg("MethM  onExi",
          "Blocked 'on_exit' call for " + mMethods);
      return false;
    }
    if (mOnExit != null) {
      if (mDebug)
        CoolDSL.debugMsg("MethM  onExi", "'on_exit' call for " + mMethods);
      mOnExit.setDelegate(resolveHandler);
      mOnExit.setResolveStrategy(Closure.DELEGATE_ONLY);
      mOneOfManagedMethodsIsRunning = false;
      return mOnExit.call();
    }
    else
      return false;
  }

  /**
   * Return a list of methods this method manager is responsible for
   * @return the list of methods this manager manages
   */
  public List<String> methods() {
    return mMethods;
  }
  
  public void setMethods(List<String> m) {
    mMethods = m;
  }
  
  public String toString() {
	String str = "MethodManager:"+mMethods;
	return str;
  }
}
