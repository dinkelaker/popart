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
package de.tud.stg.tests.popart.extensions.cool.guard;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.tud.stg.popart.aspect.extensions.cool.CoolLauncher;

/**
 * @author Oliver Rehor
 **/
public class AllGuardTests extends TestSuite {
  
  public static TestSuite suite() throws Exception {
    
    TestSuite suite = new TestSuite();
    
    org.apache.log4j.PropertyConfigurator.configure(
      "src/de/tud/stg/tests/popart/extensions/cool/log4j.properties");

    // COOL can inspect fields of the coordinated class, which is tested here
    addGuardTest(StateInspectorTestCase.class, suite);

    // Test synchronization state and auxiliary state
    addGuardTest(CoordinatorStateTestCase.class, suite);
    
    // Testing the implementation of per object coordination
    addGuardTest(PerObjectTestCase.class, suite);
    
    return suite;
  }
  
  private static void addGuardTest(Class<? extends TestCase> testClass, TestSuite suite) {
    final String path = "src/de/tud/stg/tests/popart/extensions/cool/guard/".replace("/", File.separator);
    File file = new File(path + testClass.getSimpleName() + ".cool");
    new CoolLauncher().loadAndInterpretSource(file, testClass);
    suite.addTestSuite(testClass);
  }
}
