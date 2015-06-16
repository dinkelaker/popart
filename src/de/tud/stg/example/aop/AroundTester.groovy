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

aspect(name:"AroundTester",deployed:false) {
	
	around (process_execution("Credit.*")) {
		println ">ASPECT(${thisAspect.name}): \t around $process.id"
		proceed();
	}

    around (task_execution(".*")) {
		println ">>ASPECT(${thisAspect.name}): \t around $task.id"
		proceed();
	}

	around (service_selection(".*")) {
		println ">>>ASPECT(${thisAspect.name}): \t around service selection category=$category"
		proceed();
	}
	
	around (service_call("getRate.*") & if_pcd { external }) {
		println ">>>ASPECT(${thisAspect.name}): \t around call service=$service"
		println ">>>ASPECT(${thisAspect.name}): \t proceed=$proceed"
		println ">>>ASPECT(${thisAspect.name}): \t calling proceed..."
		proceed();
		println ">>>ASPECT(${thisAspect.name}): \t ...proceed finished."
	}

}




