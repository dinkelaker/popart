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

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * Domain model layer (M) class responsible for a coordinators
 * auxiliary state.
 * @author Oliver Rehor
 */
public class AuxiliaryState extends State {
  private SynchronizationState mSynchronizationState = null;
  
  // ---- taken from
  // http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
  public static Object deepCopy(Object o) {
    Object obj = null;
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(o);
      out.flush();
      out.close();
      ObjectInputStream inp =
        new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
      obj = inp.readObject();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    return obj;
  }
  // ---- end taken from
	  
  public State clone() {
    HashMap newVars = new HashMap();
    mVars.each {
      newVars[it.getKey()] = deepCopy(it.getValue());
    }
    return new AuxiliaryState(newVars);
  }
  

  public void setOtherState(State syn) {
    mSynchronizationState = syn;
  }

  public AuxiliaryState(HashMap vars) {
    mVars = vars;
  }

    /**
   * The constructor needs a reference to the synchronization state, because
   * when defining a new variable it must be checked that it is
   * not present in the other state.
   * @param reference to the synchronization state
   */
  public AuxiliaryState(SynchronizationState synState) {
	  mSynchronizationState = synState;
  }
  
  /**
   * Check whether a given variable name is contained in the synchronization
   * state.
   * @param varName  the variable name
   * @return         true, only if the variable is contained in the 
   *                 synchronization state
   */
  protected boolean containedInOtherState(String varName) {
    return (mSynchronizationState != null ?
            mSynchronizationState.hasVarName(varName) :
            false);
  }

  /**
   * Decides whether a given expression is a valid variable value.
   * @param expression   the expression to check
   * @return             true, only if the expression is valid.
   */
  protected boolean isValidExpr(Object expression) {
    return true;
  } 
}
