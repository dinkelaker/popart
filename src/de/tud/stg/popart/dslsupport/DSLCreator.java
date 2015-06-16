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
package de.tud.stg.popart.dslsupport;

import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.dslsupport.policy.PolicyDSL;
import de.tud.stg.popart.dslsupport.listsets.ListSetsDSL;
import de.tud.stg.popart.dslsupport.bool.BoolDSL;

/**
 * @author Tom Dinkelaker
 */
public class DSLCreator {

	public static Interpreter getInterpreter(String name, Map<String, Object> context) {
		if (name.equals("policy")) return getInterpreter(new PolicyDSL(),context);
		if (name.equals("listsets")) return getInterpreter(new ListSetsDSL(),context);
		if (name.equals("bool")) return getInterpreter(new BoolDSL(),context);
		throw new RuntimeException("DSL "+name+" is not defined.");
	}
	
	public static Interpreter getInterpreter(DSL dslDefinition, Map<String, Object> context) {
		return new InterpreterCombiner(dslDefinition,context);
	}
	
	public static Interpreter getCombinedInterpreter(DSL dslDefinition1, DSL dslDefinition2, Map<String, Object> context) {
		Set<DSL> dslDefinitions = new java.util.HashSet<DSL>();
		dslDefinitions.add(dslDefinition1);
		dslDefinitions.add(dslDefinition2);
		return new InterpreterCombiner(dslDefinitions,context);
	}
	
	public static Interpreter getCombinedInterpreter(Set<DSL> dslDefinitions, Map<String, Object> context) {
		return new InterpreterCombiner(dslDefinitions,context);
	}
	
	
	
}
