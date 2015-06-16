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
package de.tud.stg.example.aosd2010.process.domainmodel

public class Task extends ProcessElement {

	protected static int lastId = 0
	
	String id = "Task${lastId++}"
	
	Closure taskClosure = null
	
	public Task(String id, Closure taskClosure) {
		this.id = id
		this.taskClosure = taskClosure
	}
	
	public String toString() { return "task:$id" }
	
	public void execute() {
		if (DEBUG) println("INTERPRETER: \t Task.execute id=$id ... ")
		
		this.taskClosure.invokeMethod('call', new Object[0])
		if (DEBUG) println("INTERPRETER: \t Task.execute id=$id ... finished. ")
	}

}
