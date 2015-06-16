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

public class BinomialVictim {
  
  public long calc(int n, int k) throws Exception {
    System.out.println(">> Inside BinomialVictim.calc(" + n  + "," + k+ ") before fac(..)");
    long x = fac(k) * fac(n-k);
    long result = fac(n) / x;
    System.out.println(">> Inside BinomialVictim.calc(" + n  + "," + k+ ") after fac(..)");
    return result;
  }
  
  public long fac(long n) throws Exception {
    if (n == 1)
      return 1;
    else
      return n * fac(n-1);
  }
  
  public void clear() {
    System.out.println(">> Inside BinomialVictim.clear()");
  }
  
  public String toString() {
    return "BinomialVictim";
  }
}
