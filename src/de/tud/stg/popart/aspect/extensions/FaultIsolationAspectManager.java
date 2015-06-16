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
package de.tud.stg.popart.aspect.extensions;

import groovy.lang.Closure;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.joinpoints.JoinPoint;

/**
 * An example application-specific <tt>AspectManager</tt> that isolates failures in advice.
 * For every advice exceptions are catched and ignored.
 */
class FaultIsolationAspectManager extends OrderedAspectManager {

	protected static boolean INTERNAL_DEBUG = false;

    Set<Aspect> failed = null;
    
	public void fireJoinPointAfterToAspects(JoinPoint jp) {
        super.fireJoinPointAfterToAspects(jp);
        failed = null;
	}
    
	 
	/**
	 * Overrides the default semantics of advice invokation, such that exceptions are caught.
	 */
	public void invokeAllApplicablePointcutAndAdvice(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG || INTERNAL_DEBUG) System.out.println("AspectManager.fireJoinPoint*ToAspects: \t\t applicablePAs="+applicablePAs);
	    Iterator<PointcutAndAdvice> applicables = applicablePAs.iterator();
	    while (applicables.hasNext()) {
	    	PointcutAndAdvice pa = (PointcutAndAdvice)applicables.next();
	    	Aspect aspect = pa.getAspect();
			jp.context.put("thisAspect",aspect);
			jp.context.put("thisPointcut",pa.getPointcut());
			
		    if ((failed == null) || !(failed.contains(aspect))) {
			    if (DEBUG || INTERNAL_DEBUG || true) System.out.println("AspectManager.fireJoinPoint*ToAspects: \t\t executing advice "+pa);
			    aspect.beforeCallingAdvice(jp,pa);
			    try {
				    Object result = invokeAdvice(jp,pa); 
				    //jp.context.put("result",result); //TODO Check whether result is set correctly
			    } catch(AspectFaultIsolationException e) {
				    if (DEBUG || INTERNAL_DEBUG || true) System.out.println("AspectManager.fireJoinPoint*ToAspects: \t\t exception while executing advice "+pa);
				    if (DEBUG || INTERNAL_DEBUG || true) System.out.println("AspectManager.fireJoinPoint*ToAspects: \t\t   will suppress aspect "+pa.getAspect()+" for this join point.");
				    if (failed == null) failed = new java.util.HashSet<Aspect>();
			    	failed.add(aspect);
			    }
		    	aspect.afterCallingAdvice(jp,pa);
		    } else {
			    if (DEBUG || INTERNAL_DEBUG || true) System.out.println("AspectManager.fireJoinPoint*ToAspects: \t\t suppressed executing advice "+pa);
		    }
	    }		
	}
	
	/**
	 * Overrides the default semantics of advice invokation, such that exceptions are caught.
	 */
	public Object invokeAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		try {
	  	    Closure advice = pa.getAdvice();
		    return advice.call(); //TODO check if we could use super.invokeAdvice() instead.
		} catch(Exception ex) {
			System.out.println("FaultIsolationAspectManager.invokeAdvice: caught exception in advice");
			if (DEBUG) ex.printStackTrace();
		    throw new AspectFaultIsolationException(pa.getAspect(),pa,jp,ex);
		}
	}
   	 
}