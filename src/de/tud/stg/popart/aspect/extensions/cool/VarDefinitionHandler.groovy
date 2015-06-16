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

import de.tud.stg.popart.aspect.extensions.cool.domainmodel.VariableAssignmentException;
import de.tud.stg.popart.aspect.extensions.cool.domainmodel.StateManager;

/**
 * Interpreter layer (M) class to assist interpretation of variable definition
 * syntax in COOL EDSL programs.
 * @author Oliver Rehor
 */
public class VarDefinitionHandler {
  
  private final static boolean mDebug = false;
  
  private Class mType = null;
  
  private HashMap<String,Object> mFoundVariables = [:];
  private StateManager mStateMan;
  private mIsCondVar = false;
  
  public VarDefinitionHandler(Class type, StateManager stateMan) {
    mType = type;
    mStateMan = stateMan;
  }
  
  public void setIsCondVar() {
    mIsCondVar = true;
  }
  
  public HashMap<String,Object> getVariables() {
    return mFoundVariables;
  }
  
  /**
   * Within another variable definition context, this method resolves
   * already defined variables such that they can be used in the current
   * variable initialization expression.
   */
  Object getProperty(String name) {
    return mStateMan.resolve(name, mIsCondVar);
  } 
  
  void setProperty(String name, Object value) {
    if (!value.class.equals(mType))
      throw new VariableAssignmentException(name, null, value,
                "Variable is declared to have the type '" + mType + "' but " +\
                "assigned a value of type '" + value.class + "'");
    mFoundVariables.put(name, value);
  } 
}
