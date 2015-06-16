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
package de.tud.stg.tests.instrumentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.Proceed;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.InitializationJoinPoint;

public aspect InitializationInstrumentation {
  
  private final boolean DEBUG = false;
  private static int proceedCnt = 0;
  
  //declare precedence: InitMain, MethodCallInstrumentation, MethodExecutionInstrumentation; 
    
  pointcut inExamples() : (
			within(de.tud.stg.tests..*) && 
			!within(de.tud.stg.tests.instrumentation..*) &&
		    !within(de.tud.stg.tests.dslsupport..*) &&
		    !within(de.tud.stg.tests.interactions.aspectj..*) &&
		    !within(de.tud.stg.tests.interactions.popart.itd..*));

  Object around () : execution(*.new(..)) && inExamples() {
    if (DEBUG) System.out.println("INSTRUMENTATION: \t before initialization "+thisJoinPoint.getSignature().getName());
    Map<String, Object> context = new java.util.HashMap<String, Object>();
    context.put("method", thisJoinPoint.getSignature().getName());
    context.put("signature", thisJoinPoint.getSignature());
    context.put("thisObject", thisJoinPoint.getThis());
    context.put("targetObject", thisJoinPoint.getTarget());
    context.put("args", java.util.Arrays.asList(thisJoinPoint.getArgs()));
    JoinPoint jp = new InitializationJoinPoint(
      thisJoinPoint.getSignature().getDeclaringType().getName(), 
      thisJoinPointStaticPart.toLongString()+":"+thisJoinPointStaticPart.getSourceLocation(), 
      thisJoinPoint.getArgs(), 
      context); 
    context.put("thisJoinPoint", jp);
    AspectManager.getInstance().fireJoinPointBeforeToAspects(jp);
    
    final Object[] _args = ((List<Object>)(context.get("args"))).toArray();
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
    // this output is valid, so the "thisJoinPoint" errors must be caused
    // by POPART
    //System.out.println("aroundExecNew: " + thisJoinPoint.getTarget() + ":" + context.get("targetObject"));

    if (DEBUG) System.out.println("INSTRUMENTATION: \t after initialization "+thisJoinPoint.getSignature().getName());
    AspectManager.getInstance().fireJoinPointAfterToAspects(jp);
    Object result = context.get("result");
    return result;
  }

}
