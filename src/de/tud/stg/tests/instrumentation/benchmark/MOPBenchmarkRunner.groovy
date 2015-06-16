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
package de.tud.stg.tests.instrumentation.benchmark;

import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClassCreationHandle;
import de.tud.stg.popart.aspect.extensions.itd.InstrumentationInterTypeDeclarationMetaClassCreationHandle;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaClassCreationHandle;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;

abstract class MOPBenchmarkRunner extends BenchmarkRunner {
	
	public MOPBenchmarkRunner(BenchmarkMOPMode mopMode, BenchmarkAspectMode aspectMode, boolean enablePartialEvaluation, boolean enableInterTypeDeclarationCache, boolean debug) {
		super(aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, debug);
		
		switch(mopMode){
			case BenchmarkMOPMode.ENABLE_BOTH:
				InstrumentationInterTypeDeclarationMetaClassCreationHandle.replaceMetaClassCreationHandleForInstrumentationAndInterTypeDeclarations();
				Booter.initializeJoinPointInstrumentation();
				break;
			case BenchmarkMOPMode.ENABLE_INSTRUMENTATION_ONLY:
				InstrumentationMetaClassCreationHandle.replaceMetaClassCreationHandleForInstrumentation();
				Booter.initializeJoinPointInstrumentation();
				break;
			case BenchmarkMOPMode.ENABLE_INTER_TYPE_DECLARATIONS_ONLY:
				InterTypeDeclarationMetaClassCreationHandle.replaceMetaClassCreationHandleForInterTypeDeclarations();
				break;
			default:
				//default groovy mop setup.
				break;
		}
	}
	
	@Override
	protected abstract StructureDesignator getBenchmarkSpecificClassDesignator();
	@Override
	protected abstract void runBenchmark();
}
