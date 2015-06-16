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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier

import de.tud.stg.popart.aspect.extensions.cool.CoolDSL;

/**
 * Domain model layer (M) class intercepting variable resolution and assignment
 * requests in COOL closures using Groovy's MOP.
 * @author Oliver Rehor
 */
public class ResolveHandler {
  
  private final static boolean mDebug = true;
  
  private StateManager mStateManager = null;
  private boolean mResolveOnlyCondVars = false;
  
  private Object mTargetObject = null;
  
  public ResolveHandler() {}
  
  public ResolveHandler(StateManager sm, boolean onlyCondVarsFlag) {
    mStateManager = sm;
    mResolveOnlyCondVars = onlyCondVarsFlag;
  }
  
  public void setStateManager(StateManager sm) {
    mStateManager = sm;
  }
  
  public StateManager getStateManager() {
    return mStateManager;
  }
  
  public void setResolveOnlyCondVars(boolean flag) {
    mResolveOnlyCondVars = flag;
  }
  
  public boolean getResolveOnlyCondVars() {
    return mResolveOnlyCondVars;
  }
  
  public void setTargetObject(Object targetObject) {
    mTargetObject = targetObject;
  }
  
  /**
   * Catch variable assignment statements in COOL programs.
   * Whenever an unknown variable assignment is encountered during
   * interpretation, Groovy asumes that it is a property and calls
   * this method. This circumstance is exploited to simplify the COOL EDSL
   * syntax concerning variable assignment.
   * @param name   the variable name
   * @param value  the new variable value
   */
  void setProperty(String name, Object value) {
    mStateManager.assign(name, value);
    if (mDebug)
      CoolDSL.debugMsg("ResHd   asgn",
        "assigned [$name := $value]");
  }
  
  /**
   * Catch variable resolution statements in COOL programs.
   * Whenever an unknown variable resolution is encountered during
   * interpretation, Groovy asumes that it is a property and calls
   * this method. This circumstance is exploited to simplify the COOL EDSL
   * syntax concerning variable resolution.
   * @param name   the variable name
   * @return       the current variable value
   */
  Object getProperty(String name) {
    assert mTargetObject != null;
    try {
      Field field = mTargetObject.class.getDeclaredField(name);
      Object fieldValue = field.get(mTargetObject);
      // TODO: concern field visibility!?
      //if (Modifier.isPublic(field.getModifiers())) {
        if (mDebug)
          CoolDSL.debugMsg("ResHd  reslv",
            "resolved [$name == " + fieldValue + "] as field in coordinated class.");
        return fieldValue;
      //}
    } catch (NoSuchFieldException) {
      // ignore
    }
  
    assert mStateManager != null;
    Object result = mStateManager.resolve(name, mResolveOnlyCondVars);
    if (mDebug)
      CoolDSL.debugMsg("ResHd  reslv",
        "resolved [$name == " + result + "] as coordinator variable.");
    return result;
  } 
}
