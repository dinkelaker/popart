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
package de.tud.stg.tests.popart.extensions.coolcachingzip;

import de.tud.stg.popart.aspect.extensions.zip.Compressable;
import junit.framework.*;
import junit.textui.TestRunner;

/**
 * @author Oliver Rehor
 **/
public class Archive extends TestCase {

  public void testSth() throws Throwable {
    System.out.println("====== Running  CoCaZip Test (JUnit) ======");
    ArchiveVictim victim = new ArchiveVictim();
    Compressable c = new Compressable();
    
    c.data = "inputStr";
    victim.write(c);
    assertTrue(c.data instanceof Byte[]);
    
    c = victim.read();
    assertTrue(c.data instanceof String);
    assertTrue(((String)c.data).equals("inputStr"));
    
    c = victim.read();
    assertTrue(c.data instanceof String);
    assertTrue(((String)c.data).equals("inputStr"));

    c.data = "2ndInputStr";
    victim.write(c);
    assertTrue(c.data instanceof Byte[]);

    c = victim.read();
    assertTrue(c.data instanceof String);
    assertTrue(((String)c.data).equals("2ndInputStr"));

    System.out.println("====== Finished CoCaZip Test (JUnit) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { ArchiveVictim.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(ArchiveVictim.class);
  }
  
}
