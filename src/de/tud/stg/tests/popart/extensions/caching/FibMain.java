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
package de.tud.stg.tests.popart.extensions.caching;

public class FibMain {
	
  static final class Fibonacci {
    public long fibCached(final int n) {
      System.out.println("call of fib(" + n + ")");
      if (n == 1)
    	  return 1;
      else if (n == 0)
     	  return 0;
      else
    	  return fibCached(n-2) + fibCached(n-1);
    }
    public long fibUncached(final int n) {
      if (n == 1)
        return 1;
      else if (n == 0)
        return 0;
      else
        return fibUncached(n-2) + fibUncached(n-1);
    }
  };
	
  public static void init() {}
   
  public static void main(String args[]) {
    Fibonacci fib = new Fibonacci();
    final int n = 20;
    // 0   1   2   3   4   5   6   7   8
    // ---------------------------------
    // 0   1   1   2   3   5   8  13  21
    long startTime = System.currentTimeMillis();
    long cachedResult = fib.fibCached(n);
    long endTime = System.currentTimeMillis();
    System.out.println("cached fib(" + n + ") = " + cachedResult +
                       " in " + (endTime-startTime) + "ms.");
    startTime = System.currentTimeMillis();
    long uncachedResult = fib.fibUncached(n);
    endTime = System.currentTimeMillis();
    System.out.println("uncached fib(" + n + ") = " + uncachedResult +
                       " in " + (endTime-startTime) + "ms.");
  }
}
