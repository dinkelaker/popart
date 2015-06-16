///////////////////////////////////////////////////////////////////////////////
// Copyright 2009-2015, Technische Universitaet Darmstadt (TUD), Germany
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
package de.tud.stg.example.aop;

aspect(name:"BoolAdviceDSL",deployed:false) {
	
	before (service_selection("Category1")) {
		println "ASPECT(${thisAspect.name}): \t before service selection advice in $application"

		def boolDSL = de.tud.stg.popart.dslsupport.bool.BoolDSL.getInterpreter(thisJoinPoint.context);
		
		boolDSL.eval {
			puts "T is", T
			puts "F is", F
			def X = T
			puts "X is", X
			puts "T and F is", T & F
			puts "T or F is", T | F
			puts "not(T) is", not(T)
		}

	}
	
}