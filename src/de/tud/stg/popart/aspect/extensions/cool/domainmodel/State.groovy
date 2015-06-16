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
 * Abstract domain model layer (M) class responsible for
 * handling of all types of COOL variables
 * @author Oliver Rehor
 */
public abstract class State {
	
  // the variables are managed by a HashMap (varName -> initialValue)
  // where initialValue is either a boolean or a list of booleans
  protected HashMap mVars = null;
  
  public abstract State clone();
  
  public abstract void setOtherState(State other);

  /**
   * This method adds a bunch of variables declared in the parameter
   * to this state. Before that it is checked whether the names and values
   * are correct or already present by calling the methods
   * {@link checkNames} and {@link checkValues}.
   * @param vars  the Map containing the variable names as key and their
   *              initial assignment as value.
   */
  public void add(HashMap<String, Object> vars) {
    try {
      checkNames(vars.keySet());
      checkValues(vars.values());
    } catch (Exception E) {
      println E.message;
      return;
    }
    if (mVars == null)
      mVars = vars;
    else
      vars.keySet().each {
        mVars[it] = vars[it];
      };
  }
  
  /**
   * Determine whether a variable with a given name is already present
   * in this state.
   * @param varName   the name of the variable being checked
   * @return true, if the variable name is already present
   */
  public boolean hasVarName(String varName) {
	if (mVars != null)
      return mVars.containsKey(varName);
  }
  
  /**
   * This method checks whether given variable names can be added to
   * this state. If not, an exception is thrown.
   * @param varNames  the collection of variable names that should be added
   */
  protected void checkNames(Collection varNames) {
    varNames.each {
      if ((mVars != null && mVars.containsKey(it))
          || containedInOtherState(it))
        throw new Exception(">> Error: a variable with name " +\
                            it + " was already defined.");
    }
  }
  
  /**
   * This method checks whether given variable initial values can be added
   * to this state. If not, an exception is thrown.
   * @param initializers  the collection of variable initializers that
   *        should be added
   */
  protected void checkValues(Collection initializers) {
    initializers.each {
      if (!isValidExpr(it) && !(it instanceof List))
        throw new Exception(">> Error: variable initializer has wrong type." +\
                            " Found: " + it);
      else if (it instanceof List)
        it.each {
          if (!isValidExpr(it))
            throw new Exception(">> Error: variable array initializer has" +\
                                "wrong type. Found: " + it);
        };
    }
  }
  
  /**
   * Resolve a variable with given name.
   * @param varName  the variable name
   * @return the variable value
   */
  public Object resolve(String varName) {
	return mVars[varName];
  }
  
  /**
   * Assign a variable a given value.
   * @param varName  the variable name
   * @param value    the new variable value
   */
  public void assign(String varName, Object value) {
    mVars[varName] = value;
  }

  /**
   * Assign an array variable a given value.
   * @param varName  the variable name
   * @param index    the array index
   * @param value    the new variable value
   */
  public void assign(String varName, Integer index, Object value) {
    mVars[varName][index] = value;
  }
  
  /**
   * Template method deciding whether a given expression is a valid
   * variable value.
   * Subclasses define, what exactly a valid variable value is.
   * @param expression   the expression to check
   * @return             true, only if the expression is valid.
   */
  protected abstract boolean isValidExpr(Object expression);
  
  /**
   * Template method allowing to check whether a given variable name
   * is contained in another state. Subclasses implement details.
   * @param varName  the variable name
   * @return         true, only if the variable is contained in the other
   *                 state.
   */
  protected abstract boolean containedInOtherState(String varName);
  
  public String toString() {
	String str = "State:"+mVars;
	return str;
  }
}
