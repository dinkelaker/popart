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
package de.tud.stg.popart.aspect.extensions.instrumentation;

import de.tud.stg.popart.joinpoints.MethodExecutionJoinPoint;

/**
 * @author Jan Stolzenburg
 */
public class InstrumentationMethodExecution extends JoinPointInstrumentation {
	
	protected void prolog() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t before method call ${instrumentationContext.receiver.class.canonicalName}.${instrumentationContext.methodName}");
		joinPointContext = new java.util.HashMap<String,Object>();
		joinPoint = new MethodExecutionJoinPoint(instrumentationContext.getMethodName(), "Source location feature not implemented", instrumentationContext.args, joinPointContext);
		joinPointContext.put("thisJoinPoint",joinPoint);
	}
	
	protected void prologForAround() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t around method call ${instrumentationContext.receiver.class.canonicalName}.${instrumentationContext.methodName}");
	}
	
	protected void epilogForAround() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t after method call ${instrumentationContext.receiver.class.canonicalName}.${instrumentationContext.methodName}");
	}
}