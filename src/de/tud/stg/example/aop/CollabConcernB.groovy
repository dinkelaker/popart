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

aspect(name:"CollabConcernB",deployed:false) {
	
	FileWriter f = new FileWriter("./concern_b.log",true);
	
    before (task_execution("getRates.*")) {
        println "ASPECT(${thisAspect.name}): \t write  $thisJoinPoint.context"
        f.append("log something\n");
    }

	before (task_execution("getRates.*")) {
		"ASPECT(${thisAspect.name}): \t before hypernate  $thisJoinPoint.context"
		Closure finishTaskB = { 
			f.append("File closing: finishTaskB\n");
			f.close()
		};
		process.tasks.add(finishTaskB);
	}
	
}

