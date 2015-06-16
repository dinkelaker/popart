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

import java.util.List;

import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation;
/**
 * @author Jan Stolzenburg
 */
public class InstrumentationServiceProxy extends JoinPointInstrumentation {
	
	protected void prolog() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t before service call "+instrumentationContext.args[0]);
		joinPointContext = new java.util.HashMap<String,Object>();
		joinPointContext.put("service", instrumentationContext.getReceiver());
		joinPointContext.put("operation", instrumentationContext.args[0]);
		joinPointContext.put("args", instrumentationContext.args[1]);
		joinPointContext.put("external", true);
		joinPoint = new ServiceCallJoinPoint(joinPointContext.get("service").toString(), "MyServiceProxy.call():23", ((List<?>)joinPointContext.get("args")).toArray(), joinPointContext);
		joinPointContext.put("thisJoinPoint", joinPoint);
	}
	
	protected void prologForAround() {
  	    if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t around service call "+joinPointContext.get("operation"));
		joinPoint.location = "MyServiceProxy.call():34";
	}
	
	@Override
	protected void writeBackJoinPointContextToApplicationContext() {
		//re-assemble the real args from the operation and the joinpoints args
		Object[] args = new Object[] {joinPointContext.get("operation"), joinPointContext.get("args")};
		instrumentationContext.setArgs(args);
	}
	
	protected void epilogForAround() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t after service call "+joinPointContext.get("operation"));
		joinPoint.location = "MyServiceProxy.call():44";
	}
}
