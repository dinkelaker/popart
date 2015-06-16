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
package de.tud.stg.tests.popart.extensions.zip;

import java.io.File;
import junit.framework.TestSuite;
import de.tud.stg.popart.aspect.extensions.zip.ZipLauncher;

/**
 * @author Oliver Rehor
 **/
public class AllZipTests extends TestSuite {
  
  public static TestSuite suite() throws Exception {
    
    TestSuite suite = new TestSuite();
    
    org.apache.log4j.PropertyConfigurator.configure(
      "src/de/tud/stg/tests/popart/extensions/cool/log4j.properties");

    addZipTest(ArgAndResult.class, suite);

    return suite;
  }
  
  private static void addZipTest(Class testClass, TestSuite suite) {
    final String path = "src/de/tud/stg/tests/popart/extensions/zip/"
      .replace("/", File.separator);
    new ZipLauncher().loadAndInterpretSource(
      new File(path + testClass.getSimpleName() + ".z"), testClass);
    suite.addTestSuite(testClass);
  }
}
