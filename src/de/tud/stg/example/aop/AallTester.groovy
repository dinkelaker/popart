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

aspect(name:"Aalltester",deployed:false,priority:2) {
	
	before (service_selection("Category1") | service_selection("Banking")) {
		println "ASPECT(${thisAspect.name}): \t before service selection advice in $application"
		println "ASPECT(${thisAspect.name}): \t jp=$thisJoinPoint"
		println "ASPECT(${thisAspect.name}): \t category=$category"
	}

	before (process_execution("Credit2.*")) {
		println "ASPECT(${thisAspect.name}): \t BEFORE PROCESS EXEC process=$process"
	}

	after (process_execution("Credit.*")) {
		println "ASPECT(${thisAspect.name}): \t AFTER PROCESS EXEC process=$process"
	}

	/*
	before ( deep_debug { 
		task_execution("selectOffer") | 
		not (task_execution("get.*")) | 
		(service_call("getRate(.)*") & if_pcd { external }) } 
	) {
		println "ASPECT(${thisAspect.name}): \t BEFORE TASK EXEC task=$task"
	}
	*/

	after (task_execution("select.*")) {
		println "ASPECT(${thisAspect.name}): \t AFTER TASK EXEC task=$task"
	}

	//*
	before (process_execution(".*") | 
			task_execution(".*") | 
			service_selection(".*") |
			service_call(".*")) {
		println "ASPECT(${thisAspect.name}): \t match all jps"
	}
	//*/

}




