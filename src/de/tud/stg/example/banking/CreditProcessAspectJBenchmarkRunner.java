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
package de.tud.stg.example.banking;

import de.tud.stg.example.interpreter.metamodel.Process;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkAspectMode;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkRunner;

public class CreditProcessAspectJBenchmarkRunner extends BenchmarkRunner {
	private int repeats;
	private Process process;
	
	public CreditProcessAspectJBenchmarkRunner(int repeats, int nBanks, BenchmarkAspectMode aspectMode, boolean enablePartialEvaluation, boolean enableInterTypeDeclarationCache, boolean debug) {
		super(aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, debug);
		this.repeats = repeats;
		BankingServiceProxy.init(nBanks);
		process = CreditProcess.createCreditProcess();
	}

	@Override
	protected StructureDesignator getBenchmarkSpecificClassDesignator() {
		return CreditProcess.getBenchmarkSpecificClassDesignator();
	}

	@Override
	protected void runBenchmark() {
		for(int i = 0; i < repeats; i++){
			process.execute();
		}
	}
}
