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
package de.tud.stg.example.aosd2010.process.dsjpm;

import de.tud.stg.example.aosd2010.process.dsjpm.ServiceSelectionJoinPoint;
import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation;

/**
 * @author Jan Stolzenburg
 */
public class InstrumentationRegistry extends JoinPointInstrumentation {
	
	private String category;
	
	protected void prolog() {
		category = (String) instrumentationContext.args[0];
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t before service selection "+category);
		joinPointContext = new java.util.HashMap<String,Object>();
		joinPointContext.put("category", category);
		joinPoint = new ServiceSelectionJoinPoint(category, "Registry.find():43", joinPointContext);
		joinPointContext.put("thisJoinPoint", joinPoint);
	}
	
	protected void prologForAround() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t around service selection "+category);
		joinPoint.location = "Registry.find():43";
	}
	
	protected void epilogForAround() {
		joinPointContext.put("selectedServices", joinPointContext.get("result"));
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t after service selection "+category);
		joinPoint.location = "Registry.find():55";
	}
}
