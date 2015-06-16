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
package de.tud.stg.tests.instrumentation.benchmark.fibonacci;

import de.tud.stg.popart.aspect.extensions.itd.StructuralPointcutDSL;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkAspectMode;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkRunner;

public class FibonacciJavaBenchmarkRunner extends BenchmarkRunner {
	private int size;
	private FibonacciJava fibonacci;
	
	public FibonacciJavaBenchmarkRunner(int size, BenchmarkAspectMode aspectMode, boolean enablePartialEvaluation, boolean enableInterTypeDeclarationCache, boolean debug) {
		super(aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, debug);
		this.size = size;
		fibonacci = new FibonacciJava();
	}
	
	@Override
	protected StructureDesignator getBenchmarkSpecificClassDesignator() {
		return new StructuralPointcutDSL().is_type(FibonacciJava.class);
	}
	
	@Override
	protected void runBenchmark() {
		if(debug) System.out.print("FibonacciJava: Calculating fib("+size+") = ");
		long result = fibonacci.calc(size);
		if(debug) System.out.println(result);
	}
}
