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

import java.util.Map;

public class BenchmarkConfiguration {
	// benchmark settings
	public final int warmups;
	public final String benchmark;
	public final String args;
	// framework settings
	public final int passes;
	public final BenchmarkMOPMode mopMode;
	public final BenchmarkAspectMode aspectMode;
	public final boolean enablePartialEvaluation;
	public final boolean enableInterTypeDeclarationCache;
	// vm settings
	public final String heapSize;
	public final int maxWait;
	
	public BenchmarkConfiguration(int warmups, String benchmark, String args, int passes, BenchmarkMOPMode mopMode, BenchmarkAspectMode aspectMode, boolean enablePartialEvaluation, boolean enableInterTypeDeclarationCache, String heapSize, int maxWait){
		this.warmups = warmups;
		this.benchmark = benchmark;
		this.args = args;
		this.passes = passes;
		this.mopMode = mopMode;
		this.aspectMode = aspectMode;
		this.enablePartialEvaluation = enablePartialEvaluation;
		this.enableInterTypeDeclarationCache = enableInterTypeDeclarationCache;
		this.heapSize = heapSize;
		this.maxWait = maxWait;
	}
	
	public Map<String,Object> toMap(){
		Map<String,Object> result = new java.util.LinkedHashMap<String,Object>();
		result.put("warmups", warmups);
		result.put("benchmark", benchmark);
		result.put("args", args);
		result.put("aspectMode", aspectMode);
		result.put("mopMode", mopMode);
		result.put("enablePartialEvaluation", enablePartialEvaluation);
		result.put("enableInterTypeDeclarationCache", enableInterTypeDeclarationCache);
		result.put("passes", passes);
		result.put("heapSize", heapSize);
		result.put("maxWait", maxWait);
		return result;
	}
	
	@Override
	public String toString() {
		return toMap().toString();
	}
}
