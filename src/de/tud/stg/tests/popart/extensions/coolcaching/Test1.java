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
package de.tud.stg.tests.popart.extensions.coolcaching;

import junit.framework.*;
import junit.textui.TestRunner;
import net.sourceforge.groboutils.junit.v1.*;

public class Test1 extends TestCase {


  private class TestWithGrobo extends TestRunnable {
    
    private Test1Victim mVictim;
    private int mId;
    
    private TestWithGrobo(Test1Victim v, int id) {
      mVictim = v;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      if (mId == 10) {
        mVictim.wordCount(Integer.toString(mId));
      }
      else {
        Thread.currentThread().sleep(10);
        mVictim.addLine(Integer.toString(mId));
      }
    }
    
  }
  
  public void testSth() throws Throwable {
    System.out.println("====== Running  CoCa Test (JUnit+Grobo) ======");
    Test1Victim victim = new Test1Victim();
    TestRunnable t1 = new TestWithGrobo(victim, 10);
    TestRunnable t2 = new TestWithGrobo(victim, 20);
    TestRunnable[] ts = { t1, t2 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
    
    System.out.println("====== Finished CoCa Test (JUnit+Grobo) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { Test1Victim.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(Test1Victim.class);
  }
  
}
