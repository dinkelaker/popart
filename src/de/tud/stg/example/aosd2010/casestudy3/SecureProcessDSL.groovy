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
package de.tud.stg.example.aosd2010.casestudy3;

import de.tud.stg.example.aosd2010.process.domainmodel.Process;
import de.tud.stg.example.aosd2010.process.*;
import de.tud.stg.popart.dslsupport.DSL;

import groovy.lang.Closure;

import java.util.Map;


public class SecureProcessDSL extends ProcessDSL implements ISecureProcessDSL {
	
	public Process process(Map params, Closure definitionClosure) {
		println "before SecureProcessDSL.process"
		SecureProcess process = new SecureProcess(params.name);
		currentProcess = process;
		this.log("SecureProcess.define: this=${this}, this.class=${process.getClass()} ");
		definitionClosure.delegate = this;
		definitionClosure.call();
		return process;
	}	
	
	public SecureProcess secure_process(HashMap<String,Object> params, Closure definitionClosure) {
		println "before secure_process"
		return process(params,definitionClosure);
	}	
	
	private static final RSA_ALGORITH_ID = 0;
	
	public int getRSA() { return RSA_ALGORITH_ID; } 
	
	public Identity getMe() { return (SecureProcess)currentProcess; }

	public int encrypt(int plain, int algorith, int key) {
        println "SecureProcessDSL encrypt for $currentProcess"
		println "SecureProcessDSL encrypting plain=$plain with key=$key to chiffre=${plain + key}"
		return plain + key;
	}

	public int decrypt(int chiffre, int algorith, int key) {
        println "SecureProcessDSL decrypt for $currentProcess"
        println "SecureProcessDSL decrypting chiffre=$chiffre with key=$key to plain=${chiffre - key}"
		return chiffre - key;
	}

}
