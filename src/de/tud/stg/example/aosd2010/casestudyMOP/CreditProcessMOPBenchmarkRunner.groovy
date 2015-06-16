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
package de.tud.stg.example.aosd2010.casestudyMOP;

import de.tud.stg.example.aosd2010.process.dsjpm.ProcessJoinPointModel;
import de.tud.stg.example.aosd2010.process.domainmodel.Process;
import de.tud.stg.example.aosd2010.process.domainmodel.ServiceProxy;
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.aspect.extensions.itd.StructuralPointcutDSL;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkAspectMode;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkMOPMode;
import de.tud.stg.tests.instrumentation.benchmark.MOPBenchmarkRunner;

public class CreditProcessMOPBenchmarkRunner extends MOPBenchmarkRunner {
	private int repeats;
	private Process process;
	
	public CreditProcessMOPBenchmarkRunner(int repeats, int nBanks, BenchmarkMOPMode mopMode, BenchmarkAspectMode aspectMode, boolean enablePartialEvaluation, boolean enableInterTypeDeclarationCache, boolean debug) {
		super(mopMode, aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, debug)
		this.repeats = repeats;
		BankingServiceProxy.init(nBanks)
		process = CreditProcess.createCreditProcess()
	}
	
	@Override
	protected StructureDesignator getBenchmarkSpecificClassDesignator() {
		/*
		 * all classes within the domain model package and
		 * all classes implementing the service proxy interface
		 * are to be adviced for this benchmark.
		 */
		return StructuralPointcutDSL.eval{
			is_type(ServiceProxy.class) |
			//within_package("de.tud.stg.example.aosd2010.process.domainmodel")
			within_package(Process.class.getPackage())
		}
	}

	@Override
	protected void runBenchmark() {
		repeats.times {
			process.execute()
		}
	}

}
