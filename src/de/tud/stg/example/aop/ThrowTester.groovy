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
package de.tud.stg.example.aop;

aspect(name:"ThrowTester",deployed:false) {
	
	int r;
	Random random = new Random();
	
	before (service_call(".*")) {
		r = random.nextInt().abs() % 2;
		println "ASPECT(${thisAspect.name}): \t r=$r"
		if (r==1) {
			println "ASPECT(${thisAspect.name}): \t will throw an exception"
			throw new Exception("Exception in advice");
		} else {
		    println "ASPECT(${thisAspect.name}): \t successful advice execution"
		}
	}

	after (service_call(".*")) {
		if (r==1) {
  		    println "ASPECT(${thisAspect.name}): \t should not be executed as before has thrown an exception"
		} else {
  		    println "ASPECT(${thisAspect.name}): \t executed because no exception"
		}
	}

}




