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
package de.tud.stg.example.interpreter.instrumentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.Proceed;
import de.tud.stg.popart.aspect.extensions.instrumentation.PopartInterestCache;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.example.interpreter.metamodel.Task;

public aspect TaskExecutionInstrumentation {
	
	private final boolean DEBUG = false;
	
	Object around (Task task) : execution(* Task+.execute()) && target(task) {
		if (! PopartInterestCache.isPopartInterested(task.getClass(), "execute"))
			return proceed(task);
		if (DEBUG) System.out.println("INSTRUMENTATION: \t before task execution "+task);
        
		Map<String, Object> context = new java.util.HashMap<String, Object>();
		context.put("targetObject", task);
		context.put("task", task);
		JoinPoint jp = new TaskExecutionJoinPoint(task, thisJoinPointStaticPart.getSourceLocation().toString(), context); 
		context.put("thisJoinPoint", jp);
		AspectManager.getInstance().fireJoinPointBeforeToAspects(jp);
		
		final Task _task = (Task)context.get("task");
		if (DEBUG) System.out.println("INSTRUMENTATION: \t around task execution "+_task.getId());
		//Extracting Original Join Point Actions and Storing them Callable Proceed Closure
	    context.put("proceed",new Proceed() {
	            public Object call(List<Object> _args) {
	               return proceed(_task);
	            }
        });
		jp.location = "Task.execute():45";
		AspectManager.getInstance().fireJoinPointAroundToAspects(jp);
		Object result = context.get("result");
				
		if (DEBUG) System.out.println("INSTRUMENTATION: \t after task execution "+task);
		AspectManager.getInstance().fireJoinPointAfterToAspects(jp);
		result = (Integer)context.get("result");
		
		return result;
	}

}
