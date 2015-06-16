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

public class Binomial extends TestCase {

  private Vector<String> rs = new Vector<String>();

  private class TestWithGrobo extends TestRunnable {
    
    private BinomialVictim mVictim;
    private int mId;
    
    private TestWithGrobo(BinomialVictim v, int id) {
      mVictim = v;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      if (mId == 10) {
        final int n = 20;
        final int k = 5;
        long result = mVictim.calc(n, k);
        rs.add("1::" + result);
        System.out.println("::::: cached binom(" + n + ", " + k + ") = " + result + ".");
        assertTrue(result == 15504);
        
        result = mVictim.calc(n, k);
        rs.add("3::" + result);
        System.out.println("::::: recalled cached binom(" + n + ", " + k + ") = " + result + ".");
        assertTrue(result == 15504);
        
        mVictim.clear();
        rs.add("4::clear");
        System.out.println("::::: invalidated cache");
        
        result = mVictim.calc(n, k);
        rs.add("5::" + result);
        System.out.println("::::: recalled cached binom(" + n + ", " + k + ") = " + result + ".");
        assertTrue(result == 15504);
      }
      else {
        Thread.currentThread().sleep(10);
        final int n = 18;
        final int k = 2;
        long result = mVictim.calc(n, k);
        rs.add("2::" + result);
        System.out.println("::::: cached binom(" + n + ", " + k + ") = " + result + ".");
        assertTrue(result == 153);
      }
    }
    
  }
  
  public void testSth() throws Throwable {
    System.out.println("====== Running  CoCa Test (JUnit+Grobo) ======");
    BinomialVictim victim = new BinomialVictim();
    TestRunnable t1 = new TestWithGrobo(victim, 10);
    TestRunnable t2 = new TestWithGrobo(victim, 20);
    TestRunnable[] ts = { t1, t2 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
    
    System.out.println(rs);
    //assertTrue(rs.toString().equals("[1::15504, 2::153, 3::15504, 4::clear, 5::15504]"));
    
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
