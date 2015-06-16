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

import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationActivator;import de.tud.stg.example.aosd2010.process.domainmodel.Process;
import de.tud.stg.example.aosd2010.process.domainmodel.Registry;
import de.tud.stg.example.aosd2010.process.domainmodel.ServiceProxy;
import de.tud.stg.example.aosd2010.process.domainmodel.Task;
/**
 * @author Jan Stolzenburg
 */
public abstract class ProcessJoinPointModel {
	
	static boolean DEBUG = false;
	
	public static void declare() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t Instrumentation for ${this.class.simpleName} started");
		InstrumentationActivator.declareJoinPoint(ServiceProxy.class, "call", InstrumentationServiceProxy.class);
		InstrumentationActivator.declareJoinPoint(Process.class, "execute", InstrumentationExecuteProcess.class);
		InstrumentationActivator.declareJoinPoint(Registry.class, "find", InstrumentationRegistry.class);
		InstrumentationActivator.declareJoinPoint(Task.class, "execute", InstrumentationTask.class);
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t Instrumentation for ${this.class.simpleName} finished");
	}
	
}