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
package de.tud.stg.popart.aspect;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.aspect.PointcutAndAdvice;

/**
 * @author Tom Dinkelaker
 */
public class WrappingProceed extends Proceed {

	final protected static boolean DEBUG = false;  

	private IProceed proceedToShadow; 
	private Iterator<? extends PointcutAndAdvice> remainingPAs;
	private AspectManager aspectManager;
	private JoinPoint thisJoinPoint;

	PointcutAndAdvice lastPointcutAndAdvice = null;	

	public WrappingProceed(IProceed proceedToShadow, AspectManager am, JoinPoint jp, List<? extends PointcutAndAdvice> applicablePAs) {
		this.proceedToShadow = proceedToShadow;
		this.aspectManager = am;
		this.thisJoinPoint = jp;
		this.remainingPAs = applicablePAs.iterator();

		if (DEBUG) {
			System.out.println("WrappingProceed.<init>: \t\t applicablePAs: "+applicablePAs);
			System.out.print("WrappingProceed.<init>: \t\t wrapped advice: ");
			for(PointcutAndAdvice pa : applicablePAs){
				Aspect aspect = pa.getAspect();
				int index = aspect.getPointcutAndAdviceIndex(pa);
				System.out.print(aspect.getName()+"["+index+"] {aspect.priority="+aspect.getPriority()+"} <-- ");    		
			}
			System.out.println(" JP Shadow.");
		}
	}

	public Object call(List<Object> args) {
		Object result = null;
		if (remainingPAs.hasNext()) {
			//safe the current context in order to allow nested proceeds 
			PointcutAndAdvice safePA = this.lastPointcutAndAdvice;
			IProceed safeProceed = (IProceed)thisJoinPoint.context.get("proceed");
			Object safeThisObject = thisJoinPoint.context.get("thisObject");
			Object safeTargetObject = thisJoinPoint.context.get("targetObject");
			List<Object> safeArgs = (List<Object>)thisJoinPoint.context.get("args");

			AroundPointcutAndAdvice next = (AroundPointcutAndAdvice)remainingPAs.next();
			Aspect aspect = next.getAspect();
			if (DEBUG) System.out.println("WrappingProceed.call: \t\t setting thisAspect "+aspect);
			thisJoinPoint.context.put("thisAspect",aspect);
			thisJoinPoint.context.put("thisPointcut",next.getPointcut());

			if (DEBUG) System.out.println("WrappingProceed.call: \t\t executing next advice "+next+" ...");
			if (DEBUG) System.out.println("WrappingProceed.call: \t\t   passing the args="+args+" size="+args.size());
			if (DEBUG) System.out.println("WrappingProceed.call: \t\t   context="+thisJoinPoint.context);

			//saving this aspect before proceeding which may invoke another advice
			this.lastPointcutAndAdvice = next;

			// :::: OR-2009-07-25: Thread-Safety Modification :::::::::::::::::::
			// all the following actions must be atomic, yet reentrant for the same thread!
			// otherwise, parallel running around/proceed may cause errors
			aspectManager.acquireAdviceInterpretationLock();
			try {
				Map<String,Object> savedCtxt = aspect.getInterpreter().getContext();
				aspect.beforeCallingAdvice(thisJoinPoint,next);

				result = aspectManager.invokeAdvice(thisJoinPoint,next);

				aspect.afterCallingAdvice(thisJoinPoint,next);
				aspect.getInterpreter().setContext(savedCtxt);
			} finally { // needed to free the advice interpretation lock in AspectManager in case of an exception thrown above
				aspectManager.releaseAdviceInterpretationLock();
			}
			// :::: OR-2009-07-25: Thread-Safety Modification :::::::::::::::::::

			if (DEBUG) System.out.println("WrappingProceed.call: \t\t next advice "+next+" has been executed.");
			if (DEBUG) System.out.println("WrappingProceed.call: \t\t   working with return value ="+result);   	
			if (DEBUG) System.out.println("WrappingProceed.call: \t\t   context="+thisJoinPoint.context);

			//restore values that have may have been set to other proceeding advice in the meantime
			if (DEBUG) System.out.println("WrappingProceed.call: \t\t restoring thisAspect "+aspect);
			thisJoinPoint.context.put("thisAspect", (safePA == null)? null : safePA.aspect);
			thisJoinPoint.context.put("thisPointcut", (safePA == null)? null : safePA.pc);
			thisJoinPoint.context.put("proceed",safeProceed);
			thisJoinPoint.context.put("thisObject",safeThisObject);
			thisJoinPoint.context.put("targetObject",safeTargetObject);
			thisJoinPoint.context.put("args",safeArgs);
		} else {
			if (DEBUG) System.out.println("WrappingProceed.call: \t\t executing JP shadow");   	
			result = proceedToShadow.call(args);
			if (DEBUG) System.out.println("WrappingProceed.call: \t\t   working with return value ="+result);   	
		}
		thisJoinPoint.context.put("result",result);
		return result;
	}

}
