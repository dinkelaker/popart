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
package de.tud.stg.popart.dslsupport.trivalent;

import de.tud.stg.popart.dslsupport.*;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * This class defines a DSL environment for working with List as mathematical sets policies.
 * @author Tom Dinkelaker
 */
public class TrivalentDSL implements DSL {

	def DEBUG = false; 
	 
	public static Interpreter getInterpreter(HashMap context) {
		DSLCreator.getInterpreter(new TrivalentDSL(),context)
	}
	
	/* Literals */
	
	public Trivalent T = new T();
	
	public Trivalent F = new F();
	
	public Trivalent U = new U();
	
	/* Operations */
    
	public void puts(String str, Trivalent tri) {
		println "$str $tri";
	}
	
	Trivalent not(Trivalent tri) {
		return tri.not();
	}
	
}

