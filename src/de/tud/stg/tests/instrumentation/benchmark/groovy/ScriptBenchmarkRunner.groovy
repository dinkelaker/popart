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
package de.tud.stg.tests.instrumentation.benchmark.groovy;

import java.lang.reflect.Method;

import de.tud.stg.popart.aspect.extensions.itd.StructuralPointcutDSL;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkAspectMode;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkMOPMode;
import de.tud.stg.tests.instrumentation.benchmark.MOPBenchmarkRunner;

public class ScriptBenchmarkRunner extends MOPBenchmarkRunner {
	Class<?> script;
	String[] args;
	
	public ScriptBenchmarkRunner(String scriptName, String[] args, BenchmarkMOPMode mopMode,
			BenchmarkAspectMode aspectMode, boolean enablePartialEvaluation,
			boolean enableInterTypeDeclarationCache, boolean debug) {
		super(mopMode, aspectMode, enablePartialEvaluation,
				enableInterTypeDeclarationCache, debug);
		this.args = args;
		try{
			script = Class.forName(getClass().getPackage().getName()+".scripts."+scriptName);
		}catch(ClassNotFoundException e){
			System.err.println("could not find script: "+scriptName);
			System.exit(0);
		}
	}

	@Override
	protected StructureDesignator getBenchmarkSpecificClassDesignator() {
		return StructuralPointcutDSL.eval{
			is_type(script);
		}
	}

	@Override
	protected void runBenchmark() {
		script.main(args);
	}

}
