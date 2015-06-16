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
package de.tud.stg.example.interpreter;

import de.tud.stg.popart.aspect.PointcutDSL;
import de.tud.stg.popart.pointcuts.Pointcut;

public class ProcessPointcutDSL extends PointcutDSL {
	public Pointcut service_call(String serviceNameRegEx) {
		return new ServiceCallPCD(serviceNameRegEx);
	}
	
	public Pointcut service_selection(String categoryRegEx) {
		return new ServiceSelectionPCD(categoryRegEx);
	}
	
	public Pointcut task_execution(String taskNamePattern) {
		return new TaskExecutionPCD(taskNamePattern);
	}

	public Pointcut process_execution(String processNamePattern) {
		return new ProcessExecutionPCD(processNamePattern);
	}
}
