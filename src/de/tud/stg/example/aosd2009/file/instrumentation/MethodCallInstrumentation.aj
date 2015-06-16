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
package de.tud.stg.example.aosd2009.file.instrumentation;

import java.util.List;
import java.util.Map;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.Proceed;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.MethodCallJoinPoint;

public aspect MethodCallInstrumentation {
	
	private final boolean DEBUG = false;
	private static int proceedCnt = 0;
	
	declare precedence: InitMain, MethodCallInstrumentation; 
		
	pointcut inExamples() : (within(de.tud.stg.example.aosd2009.*) ||
        within(de.tud.stg.example.aosd2009.metaadvice.*) ||
        within(de.tud.stg.example.aosd2009.file.*) ||
        within(de.tud.stg.example.aosd2009.phone.*) ||
        within(de.tud.stg.example.aosd2009.x.*)) &&
        !within(de.tud.stg.example.aosd2009.phone.PhoneManagementSystemComparator) &&
        !within(de.tud.stg.example.aosd2009.phone.HolidayDB) &&
        !within(de.tud.stg.example.aosd2009.phone.Period);
		
	Object around () : call(* *.*(..)) && inExamples() {
		if (DEBUG) System.out.println("INSTRUMENTATION: \t before method call "+thisJoinPoint.getSignature().getName());
		Map<String,Object> context = new java.util.HashMap<String,Object>();
		context.put("methodName", thisJoinPoint.getSignature().getName());
		context.put("signature", thisJoinPoint.getSignature());
		context.put("thisObject", thisJoinPoint.getThis());
		Object target = thisJoinPoint.getTarget();
		if(target == null) target = thisJoinPoint.getStaticPart().getSignature().getDeclaringType();
		context.put("targetObject", target);
		context.put("args", java.util.Arrays.asList(thisJoinPoint.getArgs()));
		JoinPoint jp = new MethodCallJoinPoint(
		  thisJoinPoint.getSignature().getName(), 
		  thisJoinPointStaticPart.toLongString()+":"+thisJoinPointStaticPart.getSourceLocation(), 
		  thisJoinPoint.getArgs(), 
		  context); 
		context.put("thisJoinPoint", jp);
		AspectManager.getInstance().fireJoinPointBeforeToAspects(jp);
		
		final Object[] _args = ((List<Object>)context.get("args")).toArray();
	    context.put("proceed",new Proceed() {
	            public Object call(List<Object> _args) {
	               int id; 
	               if (DEBUG) id = proceedCnt++;
	               if (DEBUG) System.err.print(" >> proceed"+id+"("+_args+") called ... ");
	               Object result = proceed();
	               if (DEBUG) System.err.print(" ... proceed"+id+"("+_args+")="+result+". ");
	               return result;
	            }
        });
		AspectManager.getInstance().fireJoinPointAroundToAspects(jp);

		if (DEBUG) System.out.println("INSTRUMENTATION: \t after method call "+thisJoinPoint.getSignature().getName());
		AspectManager.getInstance().fireJoinPointAfterToAspects(jp);
		Object result = context.get("result");
		return result;
	}

}
