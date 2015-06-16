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

import junit.framework.*;
import junit.textui.TestRunner;
import net.sourceforge.groboutils.junit.v1.*;

public class StateInspectorTestCase extends TestCase {


  private class GuardTestWithGrobo extends TestRunnable {
    
    private StateInspectorVictim mVictim;
    private int mId;
    
    private GuardTestWithGrobo(StateInspectorVictim v, int id) {
      mVictim = v;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      if (mId == 10) {
        mVictim.doSth(Integer.toString(mId));
      }
      else {
        Thread.currentThread().sleep(5000);
        mVictim.initState();
        mVictim.doSth(Integer.toString(mId));
      }
    }
    
  }
  
  public void testSth() throws Throwable {
    System.out.println("====== Running  COOL Guard Test (JUnit+Grobo) ======");
    StateInspectorVictim victim = new StateInspectorVictim();
    TestRunnable t1 = new GuardTestWithGrobo(victim, 10);
    TestRunnable t2 = new GuardTestWithGrobo(victim, 20);
    TestRunnable[] ts = { t1, t2 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
    
    assertTrue(victim.toString().equals("[init, run 20, run 10]"));
   
    System.out.println("====== Finished COOL Guard Test (JUnit+Grobo) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { StateInspectorTestCase.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(StateInspectorTestCase.class);
  }
  
}
