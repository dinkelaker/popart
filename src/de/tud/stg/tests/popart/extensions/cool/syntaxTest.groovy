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
package de.tud.stg.tests.popart.extensions.cool;

import de.tud.stg.popart.aspect.extensions.cool.*;

def coordinator = CoolDSL.getInterpreter();
def per_class_coordinator = CoolDSL.getPerClassInterpreter();

/**
 * @author Oliver Rehor
 **/
// Testing the COOL EDSL Syntax
per_class_coordinator String, {
  
  // 1) SELF EXCLUSION ======================================================
  // selfex testMethod1, testMethod2;                         (original COOL)
  // ------------------------------------------------------------------------
  // selfex (["testMethod1", "testMethod2"]);
  selfex { testMethod1; testMethod2 }

  
  // 2) MUTUAL EXCLUSION ====================================================
  // mutex { testMethod3, testMethod4, testMethod5 }          (original COOL)
  // ------------------------------------------------------------------------
  // mutex (["testMethod3", "testMethod4", "testMethod5"]);
  mutex  { testMethod3; testMethod6 }

  
  // 3) CONDITION VARIABLE DEFINITION =======================================
  // condition varName1 = false, varName2 = true;             (original COOL)
  // condition varName4 = ...
  // ------------------------------------------------------------------------
  // condition (varName1:false, varName2:true);
  // condition (varName4:[false, false, true, false], varName5:true);
  condition varName1:false, varName2:true;
  condition varName4:[false, false, true, false], varName5:true;
  condition { cvarName1 = true }
  
  
  // 4) AUXILIARY VARIABLE DEFINITION =======================================
  // String varName7 = "foo", varName12 = "bar";              (original COOL)
  // ------------------------------------------------------------------------
  // a) unwieldy first attempt
  //var (varName7:[String, "blah"], varName8:[int, 5]);
  //var (varName9:[int, 3 + 5 * (8 - 3 + 5)]);
  //var (varName10:[int[], [1,2,3,4]], varName11:[int, 3]);
  
  // b) type definition by initial value
  var varName7:"foo", varName8:5;
  var varName9:3 + 5 * (8 - 3 + 5);
  var varName10:[1,2,3,4];
  
  // c) function-style-definition
  var (String) { aVarName = "init_aVarName"; bVarName = "init_bVarName" }
  var (Integer) { cVarName = 0 }
  var String, { dVarName = "init_dVarName"; eVarName = "init_eVarName" }
  var String, { fVarName = "init_fVarName-" + eVarName }
  
  // d) another interesting case exploiting Groovys command expressions
  var String, myVarName1:"init_myVarName1", myVarName2:"init_myVarName2";
  var Integer, myVarName3:0, myVarName4:1;

  
  // 5) METHOD MANAGER DEFINITION ===========================================
  // hashCode: requires !varName1 && varName2;                (original COOL)
  //           on_entry { ... }
  //           on_exit { ... }
  // ------------------------------------------------------------------------
  // Note: requires, on_entry and on_exit closures are never evaluated
  // in this test (not woven on java.lang.String, thus the guard is never used).
  //guard (["hashCode"]) { 
  guard { hashCode } {
    requires { !varName1 && varName2 }
    on_entry {
      if (!varName1)
        varName9 = 200;
    }
    on_exit {
     if (varName2)
       varName9 = 300;
     else
       varName9 = 400;
    }
  }
}
