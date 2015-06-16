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

import java.util.Vector;

import junit.framework.*;
import junit.textui.TestRunner;
import net.sourceforge.groboutils.junit.v1.*;

public class PerObjectTestCase extends TestCase {


  private class GuardTestWithGrobo extends TestRunnable {
    
    private PerObjectVictim mVictim;
    private int mId;
    
    private GuardTestWithGrobo(PerObjectVictim v, int id) {
      mVictim = v;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      if (mId == 10) {
        Thread.currentThread().sleep(10000);
        mVictim.get_Title(Integer.toString(mId));
      }
      else if (mId == 20) {
        mVictim.change_Title(Integer.toString(mId));
      } else if (mId == 30) {
        Thread.currentThread().sleep(3000);
        mVictim.get_Title(Integer.toString(mId));
      } else {
        mVictim.change_Title(Integer.toString(mId));
      }
    }
    
  }
  
  public void testSth() throws Throwable {
    System.out.println("====== Running  COOL Guard Test (JUnit+Grobo) ======");
    Vector<String> trace = new Vector<String>();
    PerObjectVictim victim1 = new PerObjectVictim();
    PerObjectVictim victim2 = new PerObjectVictim();
    victim1.useTrace(trace);
    victim2.useTrace(trace);
    TestRunnable t1 = new GuardTestWithGrobo(victim1, 10);
    TestRunnable t2 = new GuardTestWithGrobo(victim1, 20);
    TestRunnable t3 = new GuardTestWithGrobo(victim2, 30);
    TestRunnable t4 = new GuardTestWithGrobo(victim2, 40);
    TestRunnable[] ts = { t1, t2, t3, t4 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
    
    assertTrue(trace.toString().equals(
      "[getTitle 30, changeTitle 40, getTitle 10, changeTitle 20]"));
   
    System.out.println("====== Finished COOL Guard Test (JUnit+Grobo) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { PerObjectTestCase.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(PerObjectTestCase.class);
  }
  
}
