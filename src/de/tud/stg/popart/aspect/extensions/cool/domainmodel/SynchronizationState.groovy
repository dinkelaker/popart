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
 * Domain model layer (M) class responsible for a coordinators
 * synchronization state.
 * @author Oliver Rehor
 */
public class SynchronizationState extends State {
  private AuxiliaryState mAuxiliaryState = null;
  
  public State clone() {
    HashMap newVars = new HashMap();
    mVars.each {
      assert (it.getValue() instanceof Boolean);
      newVars[it.getKey()] = new Boolean(it.getValue());
    }
    return new SynchronizationState(newVars);
  }
  
  public void setOtherState(State aux) {
    mAuxiliaryState = aux;
  }
  
  public SynchronizationState(HashMap vars) {
    mVars = vars;
  }  
  
  /**
   * The constructor needs a reference to the auxiliary state, because
   * when defining a new condition variable it must be checked that it is
   * not present in the other state.
   * @param reference to the auxiliary state
   */
  public SynchronizationState(AuxiliaryState auxState) {
    mAuxiliaryState = auxState;
  }
  
  /**
   * Check whether a given variable name is contained in the auxiliary
   * state.
   * @param varName  the variable name
   * @return         true, only if the variable is contained in the 
   *                 auxiliary state
   */
  protected boolean containedInOtherState(String varName) {
	  return (mAuxiliaryState != null ?
	          mAuxiliaryState.hasVarName(varName) :
	          false);
  }
  
  /**
   * Decides whether a given expression is a valid condition variable value.
   * @param expression   the expression to check
   * @return             true, only if the expression is valid.
   */
  protected boolean isValidExpr(Object expression) {
	return expression == true || expression == false;
  }
}
