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
package de.tud.stg.example.aosd2010.process.dspcl;

import java.util.regex.Pattern;

import de.tud.stg.example.aosd2010.process.dsjpm.TaskExecutionJoinPoint;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;
import de.tud.stg.popart.pointcuts.PrimitivePCD;

public class TaskExecutionPCD extends PrimitivePCD {
	
	private Pattern compiledTaskNameRegExpr;
	
	public TaskExecutionPCD(String taskNameRegExpr) {
		super("task_execution("+taskNameRegExpr+")");
		compiledTaskNameRegExpr = Pattern.compile(taskNameRegExpr);
	}
	
	@Override
	public Pointcut partialEvalUncached(StaticJoinPoint staticJoinPoint) {
		// TODO implement
		return this;
	}
	
	public boolean matchUncached(JoinPoint jp) {
		if (jp instanceof TaskExecutionJoinPoint){
			return compiledTaskNameRegExpr.matcher(((TaskExecutionJoinPoint)jp).task.getId()).matches();
		}else{
			return false;
		}
	}
}