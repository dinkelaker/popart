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

aspect(name:"CollabConcernScheduler",deployed:false,priority:1) {
	
	before (task_execution("getRates.*")) {
		println "ASPECT(${thisAspect.name}): \t before hypernate $thisJoinPoint.context"
		process = new HashMap(); 
		process.tasks = new LinkedList(); 
	}

	after (task_execution("getRates.*")) {
		println "ASPECT(${thisAspect.name}): \t closing files $thisJoinPoint.context"
		process.tasks.each { task -> 
  		  println "ASPECT(${thisAspect.name}): \t executing task closure "+task.toString()+".";
		  task.call(); 
		}; 
	}
}

