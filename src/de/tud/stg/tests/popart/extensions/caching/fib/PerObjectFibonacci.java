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

public class PerObjectFibonacci {
  public long fibCachedPO(final int n) {
    System.out.println("call of fib(" + n + ")");
    if (n == 1)
      return 1;
    else if (n == 0)
      return 0;
    else
      return fibCachedPO(n-2) + fibCachedPO(n-1);
  }
  public long fibUncached(final int n) {
    if (n == 1)
      return 1;
    else if (n == 0)
      return 0;
    else
      return fibUncached(n-2) + fibUncached(n-1);
  }
}
