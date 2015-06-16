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

/**
 * Domain model layer (M) class managing everything that
 * has to do with the two types of coordinator state: synchronization
 * and auxiliary state.
 * @author Oliver Rehor
 */
public class StateManager {
  
  private static final boolean mDebug = false;
 
  private State mSynState = null;
  private State mAuxState = null;
  private State mLastResolveState = null;

  /**
   * Deep-copy the StateManager: both synchronization state and auxiliary state
   * @return the cloned StateManager
   */
  public StateManager clone() {
    StateManager theClone = new StateManager();
    State clonedSynState = null;
    State clonedAuxState = null;
    
    if (mSynState != null)
      clonedSynState = mSynState.clone();
    if (mAuxState != null)
      clonedAuxState = mAuxState.clone();
    
    if (clonedSynState != null) {
      clonedSynState.setOtherState(clonedAuxState);
      theClone.setSynState(clonedSynState);
    }
    if (clonedAuxState != null) {
      clonedAuxState.setOtherState(clonedSynState);
      theClone.setAuxState(clonedAuxState);
    }
    return theClone;
  }
  
  /**
   * Sets the synchronization state for this StateManager.
   * @param s the state to be used as synchronization state.
   */
  private void setSynState(State s) {
    mSynState = s;
  }
  
  /**
   * Sets the auxiliary state for this StateManager.
   * @param s the state to be used as auxiliary state.
   */
  private void setAuxState(State s) {
    mAuxState = s;
  }
  
  /**
   * Add a condition variable to the synchronization state.
   * @param vars  map of variable names and its corresponding initial values
   */
  public void addCondVars(HashMap vars) {
	if (mSynState == null)
	  mSynState = new SynchronizationState(mAuxState);
    mSynState.add(vars);
  }
  
  /**
   * Add an ordinary variable to the auxiliary state.
   * @param vars  map of variable names and its corresponding initial values
   */
  public void addVars(HashMap vars) {
    if (mAuxState == null)
      mAuxState = new AuxiliaryState(mSynState);
    mAuxState.add(vars);
  }
  
  /**
   * Forward variable resolution requests to the two states. The
   * synchronization state has precedence: Variables are first attempted
   * to be resolved therein.
   * @param varName  the variable name that should be resolved
   * @param resolveOnlyCondVars  if true, only condition variables are resolved
   * @return the resolved value of the variable
   */
  public Object resolve(String varName, boolean resolveOnlyCondVars) {
    if (StateManager.mDebug) println\
      "State: resolve [$varName]"
    Object v = null;
    if (mSynState != null) {
      v = mSynState.resolve(varName);
      mLastResolveState = mSynState;
    }
	if (v == null && !resolveOnlyCondVars) {
	  if (mAuxState != null) {
	    v = mAuxState.resolve(varName);
	    mLastResolveState = mAuxState;
	  }
	  else
        throw new VariableResolutionException(varName);
	}
	if (v == null) {
	  mLastResolveState = null;
      throw new VariableResolutionException(varName);
	}
	return v;
  }
  
  /**
   * Forward array variable resolution requests to the two states. The
   * synchronization state has precedence: Variables are first attempted
   * to be resolved therein.
   * @param varName  the variable name that should be resolved
   * @param index    the index of the array
   * @param resolveOnlyCondVars  if true, only condition variables are resolved
   * @return the resolved value of the variable
   */
  public Object resolve(String varName, Integer index,
                        boolean resolveOnlyCondVars) {
	Object v = resolve(varName, resolveOnlyCondVars);
	if (v instanceof List)
      return v[index];
	else
      throw new VariableResolutionException(varName);
  }
  
  /**
   * Receive variable assignment requests.
   * @param varName  the variable name
   * @param value    the new variable value
   */
  public void assign(String varName, Object value) {
    doAssign(varName, null, value, false);
  }
  
  /**
   * Receive array variable assignment requests.
   * @param varName  the variable name
   * @param index    the array index
   * @param value    the new variable value
   */
  public void assign(String varName, Integer index, Object value) {
    doAssign(varName, index, value, true);
  }
  
  /**
   * Forward variable assignment requests to the correct state and check
   * type compatibility.
   * @param varName  the variable name
   * @param index    the index, if an array participates; null otherwise
   * @param value    the new variable value
   * @param isArray  true, if an array at given index should be assigned a
   *                 new value; false otherwise
   * @throws VariableAssignmentException
   */
  private void doAssign(String varName, Integer index, Object value,
                        boolean isArray) {
    try {
      // variable which will be assigned a new value must already be defined
      // by "var" or "condition", so resolve it and check its type
      Object valueTillNow;
      if (isArray) {
        if (StateManager.mDebug) println\
          "State: assign  $varName[$index] := $value";
        valueTillNow = resolve(varName, index, false);
      }
      else {
        if (StateManager.mDebug) println\
          "State: assign  $varName := $value";
        valueTillNow = resolve(varName, false);
      }
      if (value.class == valueTillNow.class) {
        assert mLastResolveState != null;
        if (isArray)
          mLastResolveState.assign(varName, index, value);
        else
          mLastResolveState.assign(varName, value);
      }
      else
        throw new VariableAssignmentException(varName, index, value, \
          "incompatible types!");
    }
    catch (VariableResolutionException e) {
      throw new VariableAssignmentException(varName, index, value, \
        "variable undefined!");
    }
  }
  
  public String toString() {
	String str = "StateManager:";
	str += "\n    synState="+mSynState;
	str += "\n    auxState="+mAuxState;
	str += "\n    lastResolvedState="+mLastResolveState;
	return str;
  }
}
