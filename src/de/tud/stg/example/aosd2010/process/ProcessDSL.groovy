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

import de.tud.stg.example.aosd2010.process.domainmodel.*;
import de.tud.stg.example.aosd2010.process.domainmodel.Process;
import de.tud.stg.popart.dslsupport.DSL;

public class ProcessDSL implements IProcessDSL {
	
	protected Process currentProcess = null;
	
	private static DEBUG = true;
	
	public Registry getRegistry() {
		return Registry.getInstance();
	}
	
	public Process eval(Map params, Closure definitionClosure) {
		println "before ProcessDSL.eval"
		Process process = new Process(params.name);
		currentProcess = process;
		//log "Process.define: this=${this}, this.class=${this.getClass()} "
		this.log("Process.define: this=${this}, this.class=${process.getClass()} ");
		definitionClosure.delegate = this;
		definitionClosure.resolveStrategy = Closure.DELEGATE_FIRST;
		definitionClosure.call();
		return process;
	}

	public Process process(Map params, Closure definitionClosure) {
		println "before ProcessDSL.process"
		return eval(params,definitionClosure);
	}

	public void log(String str) {
		if (DEBUG) println "log: ${new Date()}, $currentProcess.id, $str";
	}
	
	public void notify(String str) {
		log " sending Email: $str";
	}
	
	public void task(Map params, Closure definitionClosure) {
		Task task = new Task(params.name, definitionClosure);
		definitionClosure.delegate = this;
		currentProcess.sequence.add(task);
	}
	
	public void sequence(Closure definitionClosure) {		
		definitionClosure.delegate = this;
		definitionClosure.call();
	}

}
