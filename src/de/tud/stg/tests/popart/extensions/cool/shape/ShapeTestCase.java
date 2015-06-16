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
package de.tud.stg.tests.popart.extensions.cool.shape;

import java.util.Vector;

import junit.framework.*;
import junit.textui.TestRunner;
import net.sourceforge.groboutils.junit.v1.*;

public class ShapeTestCase extends TestCase {

  private class GuardTestWithGrobo extends TestRunnable {
    
    private MyShape mShape;
    private int mId;
    
    private GuardTestWithGrobo(MyShape s, int id) {
      mShape = s;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      try {
        if (mId == 10) {
          mShape.adjustLocation();
          mShape.x();
        }
        else if (mId == 20) {
          delay(10);
          mShape.x();
          mShape.adjustDimensions();
        } else {
          delay(10);
          mShape.y();
          delay(15);
          mShape.adjustDimensions();
        }
      } catch (Exception e) {
        System.err.println(e);
      }
    }
    
  }
  
  public void testSth() throws Throwable {
    System.out.println("====== Running  COOL Shape Test (JUnit+Grobo) ======");
    MyShape s = new MyShape();
    TestRunnable t1 = new GuardTestWithGrobo(s, 10);
    TestRunnable t2 = new GuardTestWithGrobo(s, 20);
    TestRunnable t3 = new GuardTestWithGrobo(s, 30);
    TestRunnable[] ts = { t1, t2, t3 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
    
    System.out.println("====== Finished COOL Shape Test (JUnit+Grobo) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { ShapeTestCase.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(ShapeTestCase.class);
  }
  
}
