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
package de.tud.stg.tests.popart.extensions.caching.fib;

import junit.framework.*;
import junit.textui.TestRunner;

public class FibTestCase extends TestCase {
	
  public void testFib() {
    System.out.println("====== Running  Caching Fib Per Class Test (JUnit) ======");
    // 0   1   2   3   4   5   6   7   8
    // ---------------------------------
    // 0   1   1   2   3   5   8  13  21
    Fibonacci fib = new Fibonacci();
    final int n = 20;
    final int fib_n = 6765;
    
    long startTime = System.currentTimeMillis();
    long cachedResult = fib.fibCachedPC(n);
    long endTime = System.currentTimeMillis();
    
    System.out.println("\n------ Results: ------------------------------------------");
    System.out.println(":: cached fib(" + n + ") = " + cachedResult +
                       " in " + (endTime-startTime) + "ms.");
    
    assertTrue(cachedResult == fib_n);
    
    startTime = System.currentTimeMillis();
    long uncachedResult = fib.fibUncached(n);
    endTime = System.currentTimeMillis();

    assertTrue(uncachedResult == fib_n);
    
    System.out.println(":: uncached fib(" + n + ") = " + uncachedResult +
                       " in " + (endTime-startTime) + "ms.");
    System.out.println("====== Finished Caching Fib Per Class Test (JUnit) ======");
  }
	
  public static void main(String[] args) {
    String[] name = { FibTestCase.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(FibTestCase.class);
  }
}
