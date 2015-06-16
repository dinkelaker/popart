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

import java.util.Vector;
import junit.framework.*;
import junit.textui.TestRunner;
import net.sourceforge.groboutils.junit.v1.*;

public class GuardedCache extends TestCase {
  
  private Vector<String> trace = new Vector<String>();

  private class TestWithGrobo extends TestRunnable {
    
    private GuardedCacheVictim mVictim;
    private int mId;
    
    private TestWithGrobo(GuardedCacheVictim v, int id) {
      mVictim = v;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      if (mId == 10) {
        long res1, res2, res3;
        res1 = mVictim.doingSth(mId);
        delay(1000);
        res2 = mVictim.doingSth(mId);
        delay(1000);
        res3 = mVictim.doingSth(mId);
        assertTrue(res1 != res2);
        assertTrue(res2 == res3);
      }
      else {
        delay(1000);
        mVictim.doingSth(mId);
      }
    }
    
  }
  
  public void testSth() throws Throwable {
    System.out.println("====== Running  CoCa:GuardedCache Test (JUnit+Grobo) ======");
    GuardedCacheVictim victim = new GuardedCacheVictim(trace);
    TestRunnable t1 = new TestWithGrobo(victim, 10);
    TestRunnable t2 = new TestWithGrobo(victim, 20);
    TestRunnable[] ts = { t1, t2 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
    
    System.out.println(trace);
    assertTrue(trace.toString().equals("[internal:10:0, internal:10:-1]"));
    
    System.out.println("====== Finished CoCa:GuardedCache Test (JUnit+Grobo) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { GuardedCacheVictim.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(GuardedCacheVictim.class);
  }
  
}
