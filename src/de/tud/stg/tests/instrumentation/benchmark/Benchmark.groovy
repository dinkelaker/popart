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
package de.tud.stg.tests.instrumentation.benchmark

import de.tud.stg.example.aosd2010.casestudyMOP.CreditProcessMOPBenchmarkRunner;
import de.tud.stg.example.banking.CreditProcessAspectJBenchmarkRunner;
import de.tud.stg.tests.instrumentation.benchmark.fibonacci.FibonacciGroovyBenchmarkRunner;
import de.tud.stg.tests.instrumentation.benchmark.fibonacci.FibonacciJavaBenchmarkRunner;
import de.tud.stg.tests.instrumentation.benchmark.groovy.BuilderPerfTestBenchmarkRunner;
import de.tud.stg.tests.instrumentation.benchmark.groovy.ScriptBenchmarkRunner;
/**
 * @author Jan Stolzenburg
 */
class Benchmark {
	private static boolean DEBUG = false
	
	public static void main(String[] args) {
		//def parameter = [*args]
		int i = 0;
		assert (args.length > i+5)
		int warmups = Integer.parseInt(args[i++]);
		String benchmark = args[i++]
		BenchmarkAspectMode aspectMode = BenchmarkAspectMode.valueOf(args[i++])
		boolean enablePartialEvaluation = Boolean.parseBoolean(args[i++])
		boolean enableInterTypeDeclarationCache = Boolean.parseBoolean(args[i++]);
		
		//Get benchmark specifics
		BenchmarkRunner benchmarkRunner;
		switch (benchmark) {
			case 'CreditProcessMOP':
				assert(args.length == i+3)
				BenchmarkMOPMode mopMode = BenchmarkMOPMode.valueOf(args[i++]);
				int repeats = Integer.parseInt(args[i++]);
				int nBanks = Integer.parseInt(args[i++]);
				benchmarkRunner = new CreditProcessMOPBenchmarkRunner(repeats, nBanks, mopMode, aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, DEBUG)
				break
			
			case 'CreditProcessAspectJ':
				assert(args.length == i+2)
				int repeats = Integer.parseInt(args[i++]);
				int nBanks = Integer.parseInt(args[i++]);
				benchmarkRunner = new CreditProcessAspectJBenchmarkRunner(repeats, nBanks, aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, DEBUG)
				break
			
			case 'FibonacciGroovyMOP':
				assert(args.length == i+2)
				BenchmarkMOPMode mopMode = BenchmarkMOPMode.valueOf(args[i++]);
				int number = Integer.parseInt(args[i++]);
				benchmarkRunner = new FibonacciGroovyBenchmarkRunner(number, mopMode, aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, DEBUG)
				break
			
			case 'FibonacciJavaAspectJ':
				assert(args.length == i+1)
				int number = Integer.parseInt(args[i++])
				benchmarkRunner = new FibonacciJavaBenchmarkRunner(number, aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, DEBUG);
				break
				
			case 'BuilderPerfTestMOP':
				assert(args.length == i+2)
				BenchmarkMOPMode mopMode = BenchmarkMOPMode.valueOf(args[i++]);
				int size = Integer.parseInt(args[i++])
				// Approx duration: 5000 -> 1s, 100000 -> 15s
				benchmarkRunner = new BuilderPerfTestBenchmarkRunner(size, mopMode, aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, DEBUG);
				break;
			
			default:
				assert (args.length >= i+1)
				BenchmarkMOPMode mopMode = BenchmarkMOPMode.valueOf(args[i++]);
				int n = i;
				String[] newArgs = new String[args.length-n];
				while (i < args.length) newArgs[i-n] = args[i++];
				/*
				 * Available script benchmarks are located in package
				 * de.tud.stg.tests.instrumentation.benchmark.groovy.scripts
					benchmark		#args	approx durations
					----------------------------------------
					spectralnorm	1		50 -> 3s, 100 -> 13s
					recursive		1		5 -> 4s, 8 -> 24s
					raytracer		2		3 10 -> 5s, 3 20 -> 19s, 10 10 -> 32s
					partialsums		1		10000 -> 1s, 100000 -> 11s
					fannkuch		1		5 -> 1s, 8 -> 12s
					binarytrees		1		10 -> 3s, 13 -> 26s
				 */
				benchmarkRunner = new ScriptBenchmarkRunner(benchmark, newArgs, mopMode, aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, DEBUG);
		}
		
		//run benchmark
		benchmarkRunner.measure(warmups);
	}
}