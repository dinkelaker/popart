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

import de.tud.stg.example.aosd2010.process.domainmodel.Task;
import de.tud.stg.example.aosd2010.process.dsjpm.TaskExecutionJoinPoint;
import de.tud.stg.popart.aspect.IProceed;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation;

/**
 * @author Jan Stolzenburg
 */
public class InstrumentationTask extends JoinPointInstrumentation {
	
	private Task task;
	
	protected void prolog() {
		task = (Task) instrumentationContext.getReceiver();
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t before task execution "+task);
		joinPointContext = new java.util.HashMap<String,Object>();
		joinPointContext.put("task", task);
		joinPoint = new TaskExecutionJoinPoint(task, "Source location feature not implemented", joinPointContext);
		joinPointContext.put("thisJoinPoint", joinPoint);
	}
	
	protected void prologForAround() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t around task execution "+task);
		joinPoint.location = "Task.execute():45";
	}
	
	protected void epilogForAround() {
		if (DEBUG) System.out.println("INSTRUMENTATION (AOP): \t after task execution "+task);
	}
}
