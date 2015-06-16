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
package de.tud.stg.example.aosd2010.process;

import de.tud.stg.example.aosd2010.process.domainmodel.Process;
import de.tud.stg.popart.dslsupport.DSL;

public class SafeProcessDSL extends DynamicProcessDSL implements DynamicIProcessDSL {
	
	private void checkInvariants() {
		//println "checkInvariants $currentProcess.sequence"
     	LinkedList names = new LinkedList(); 
		currentProcess.sequence.each{ task ->
		  assert !task.id.equals("");
		  assert !names.contains(task.id);
   		  names.add(task.id);
		}
	}

	public Process eval(Map params, Closure definitionClosure) {
		Process process = new Process(params.name);
		currentProcess = process;
		this.log("Process.define: this=${this}, this.class=${process.getClass()} ");
		definitionClosure.delegate = this;
		definitionClosure.call();
		return process;
	}

	public void log(String str) {
		super.log(str);
		checkInvariants();
	}
	
	public void notify(String str) {
		super.notify(str);
		checkInvariants();
	}
		
	public void task(Map params, Closure definitionClosure) {
		super.task(params,definitionClosure);
		checkInvariants();
	}
	
	public void sequence(Closure definitionClosure) {		
		super.sequence(definitionClosure);
		checkInvariants();
	}
	

}
