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

aspect(name:"AdviceDynamicTriDSL",deployed:false) {
	
	before (service_selection("Category1")) {
		println "ASPECT(${thisAspect.name}): \t before service selection advice in $application"

		def triDSL = de.tud.stg.popart.dslsupport.trivalent.TrivalentDSL.getInterpreter(thisJoinPoint.context);
		
		triDSL.eval {
			puts "T is", T
			puts "F is", F
			def x = U
			puts "x is", x
			puts "T and F is", T & F
			puts "T or F is", T | F
            puts "not(F) is", not(F)
            puts "not(T) is", not(T)
			puts "T and x is", T & x
			puts "T or x is", T | x
			puts "not(x) is", not(x)
		}

	}
	
}



