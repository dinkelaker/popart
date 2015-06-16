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
package de.tud.stg.popart.aspect;

import groovy.lang.Closure;

import java.util.Map;

import de.tud.stg.popart.dslsupport.*;

/**
 * This class defines an AdviceDSL implementing the default.
 * @author Tom Dinkelaker
 */
public class AdviceDSL extends Interpreter implements DSL {

	private static final boolean DEBUG = false; 
	 
	public static Interpreter getInterpreter(Map<String,Object> context) {
		return DSLCreator.getInterpreter(new AdviceDSL(),context);
	}
	
	/* Literals */
	
	
	/* Operations */
	public Object policy(Closure policyClosure) {
		if (DEBUG) System.out.println("DSAL: \t\t Evaluating a Policy DSL block");
		Interpreter gdsl = DSLCreator.getInterpreter("policy",new java.util.HashMap<String,Object>());
		Object result = gdsl.eval(policyClosure);
		return result;
	}
	
	public Object listsets(Closure listSetsClosure) {
		if (DEBUG) System.out.println("DSAL: \t\t Evaluating a ListSets DSL block");
		Interpreter gdsl = DSLCreator.getInterpreter("listsets",new java.util.HashMap<String,Object>());
		Object result = gdsl.eval(listSetsClosure);
		return result;
	}
	
	public Object booleans(Closure boolClosure) {
		if (DEBUG) System.out.println("DSAL: \t\t Evaluating a Bool DSL block");
		Interpreter gdsl = DSLCreator.getInterpreter("bool",new java.util.HashMap<String,Object>());
		Object result = gdsl.eval(boolClosure);
		return result;
	}

	public Object proceed() {
		//TODO what the heck is this?!
		System.out.println();
		return null;
	}
}



