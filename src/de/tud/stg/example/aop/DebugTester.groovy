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

import de.tud.stg.popart.pointcuts.DebugPCD;

aspect(name:"DebugTester",deployed:false) {

	before ( new DebugPCD ({ task_execution("getRates") }) ) {
		println "ASPECT(${thisAspect.name}): \t BEFORE TASK EXEC task=$task"	
	}

	/*
	before ( input_debug { task_execution("getRates") } ) {
		println "ASPECT(${thisAspect.name}): \t BEFORE TASK EXEC task=$task"	
	}
	*/

	/*
	before ( deep_debug { 
		task_execution("selectOffer") | 
		not (task_execution("get.*")) | 
		(service_call("getRate(.)*") & if_pcd { external }) } 
	) {
		println "ASPECT(${thisAspect.name}): \t BEFORE TASK EXEC task=$task"
	}

	before ( 
	  debug { 
		  println "$thisJoinPoint"
  		  //println "ASPECT(${thisAspect.name}): \t THIS LINE WAS ADDED TO POINTCUT AT RUNTIME"
		  task_execution("getRates") 
	  } 
	) {
		println "ASPECT(${thisAspect.name}): \t BEFORE TASK EXEC task=$task"
		//println "ASPECT(${thisAspect.name}): \t THIS LINE WAS ADDED TO ADVICE AT RUNTIME"
	}
    */

}




