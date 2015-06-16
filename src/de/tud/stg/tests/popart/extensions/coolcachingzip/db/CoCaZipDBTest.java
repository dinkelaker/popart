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
package de.tud.stg.tests.popart.extensions.coolcachingzip.db;

import java.io.File;
import junit.framework.TestSuite;
import de.tud.stg.popart.aspect.extensions.coolcachingzip.CoCaZipLauncher;

/**
 * @author Oliver Rehor
 **/
public class CoCaZipDBTest extends TestSuite {
  
  public static TestSuite suite() throws Exception {
    
    TestSuite suite = new TestSuite();
    
    org.apache.log4j.PropertyConfigurator.configure(
      "src/de/tud/stg/tests/popart/extensions/cool/log4j.properties");

    addCoCaZipTest(DBApp.class, suite);

    return suite;
  }
  
  private static void addCoCaZipTest(Class testClass, TestSuite suite) {
    final String path = "src/de/tud/stg/tests/popart/extensions/coolcachingzip/db/"
      .replace("/", File.separator);
    new CoCaZipLauncher().loadAndInterpretSource(
      new File(path + testClass.getSimpleName() + ".ccz"), testClass);
    suite.addTestSuite(testClass);
  }
}
