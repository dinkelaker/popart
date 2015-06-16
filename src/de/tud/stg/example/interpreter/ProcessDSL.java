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
package de.tud.stg.example.interpreter;

import groovy.lang.Closure;

import java.util.Date;
import java.util.Map;

import de.tud.stg.example.interpreter.metamodel.Process;
import de.tud.stg.example.interpreter.metamodel.Task;
import de.tud.stg.example.interpreter.metamodel.Registry;
import de.tud.stg.popart.dslsupport.DSL;

public class ProcessDSL implements DSL {
	
	private Process currentProcess = null;
	
	private static boolean DEBUG = false;
	
	public static Registry getRegistry() {
		return Registry.getInstance();
	}
	
	public Process eval(Map<String,Object> params, Closure definitionClosure) {
		Process process = new Process((String)params.get("name"));
		currentProcess = process;
		log("Process.define: this="+this+", this.class="+process.getClass());
		definitionClosure.setDelegate(this);
		definitionClosure.call();
		return process;
	}

	public void log(String str) {
		if (DEBUG) System.out.println("log: "+new Date()+", "+currentProcess.getId()+", "+str);
	}
	
	public void notify(String str) {
		log("sending Email: "+str);
	}
	
	public void task(Map<String,Object> params, Closure definitionClosure) {
		Task task = new Task((String)params.get("name"), definitionClosure);
		definitionClosure.setDelegate(this);
		currentProcess.getSequence().add(task);
	}
	
	public void sequence(Closure definitionClosure) {		
		definitionClosure.setDelegate(this);
		definitionClosure.call();
	}

}
