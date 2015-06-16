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

import java.util.List;

import de.tud.stg.popart.aspect.IProceed;
import de.tud.stg.popart.joinpoints.MethodCallJoinPoint;

/**
 * @author Jan Stolzenburg
 */
public class InstrumentationMethodCall extends JoinPointInstrumentation {
	
	protected void prolog() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t before method call ${instrumentationContext.receiver.class.canonicalName}.${instrumentationContext.methodName}");
		joinPointContext = new java.util.HashMap<String,Object>();
		joinPoint = new MethodCallJoinPoint(instrumentationContext.getMethodName(), "Source location feature not implemented", instrumentationContext.args, joinPointContext);
		joinPointContext.put("thisJoinPoint",joinPoint);
	}
	
	protected void prologForAround() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t around method call ${instrumentationContext.receiver.class.canonicalName}.${instrumentationContext.methodName}");
		joinPointContext.put("proceed", new IProceed() {
			public Object call(List<Object> args) {
				writeBackJoinPointContextToApplicationContext();
				Object result = instrumentationContext.proceed();
				joinPointContext.put("result", result);
				return result;
			}
			@Override
			public String toString() {
				return "InstrumentationMethodExecution_finalProceed";
			}
		});
	}
	
	protected void epilogForAround() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t after method call ${instrumentationContext.receiver.class.canonicalName}.${instrumentationContext.methodName}");
	}
}