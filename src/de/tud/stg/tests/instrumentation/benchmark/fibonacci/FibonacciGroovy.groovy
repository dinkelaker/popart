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
package de.tud.stg.tests.instrumentation.benchmark.fibonacci
/**
 * @author Jan Stolzenburg
 */
public class FibonacciGroovy {
	
	private static final boolean DEBUG = false
	
	public static void main(String[] args) {
		FibonacciGroovy fibonacci = new FibonacciGroovy()
		println "fib(7) should be 13 and is "+fibonacci.calc(7)
	}
	
	public long calc(int n) {
		if (DEBUG) println("fib($n)")
		if (n < 0)
			throw new IllegalArgumentException("'n' musst be positive or zero. But it was: $n")
		if (n == 0)
			return 0
		if (n == 1)
			return 1
		return calc(n-1) + calc(n-2)
	}
}
