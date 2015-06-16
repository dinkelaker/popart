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

public class BenchmarkConfigurationBuilder {
	// benchmark settings
	public static int warmups;
	public static String benchmark;
	public static String args;
	// framework settings
	public static int passes;
	public static BenchmarkMOPMode mopMode;
	public static BenchmarkAspectMode aspectMode;
	public static boolean enablePartialEvaluation;
	public static boolean enableInterTypeDeclarationCache;
	// vm settings
	public static String heapSize;
	public static int maxWait;

	public static BenchmarkConfiguration createConfiguration(){
		BenchmarkConfiguration result = new BenchmarkConfiguration(warmups, benchmark, args, passes, mopMode, aspectMode, enablePartialEvaluation, enableInterTypeDeclarationCache, heapSize, maxWait);
//		System.out.println("Built config: "+result);
		return result;
	}
}
