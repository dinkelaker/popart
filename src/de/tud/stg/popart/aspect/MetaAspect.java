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

import groovy.lang.GroovySystem;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistry;

import java.util.List;
import java.util.Set;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * @author Tom Dinkelaker
 */
public class MetaAspect extends MetaClassImpl implements MetaAspectProtocol {
	
	protected final boolean DEBUG = false;
	
	/**
	 * Convenience constructor, using the current
	 * {@link GroovySystem#getMetaClassRegistry()} as registry
	 * (see {@link #MetaAspect(MetaClassRegistry, Class)})
	 * @param theClass the asoect class which this meta aspect is
	 * instantiated for.
	 */
	public MetaAspect(final Class<?> theClass){
		this(GroovySystem.getMetaClassRegistry(), theClass);
	}
	
	/**
	 * Constructor.
	 * @param registry the meta registry used to lookup missing meta classes,
	 * 	for example used in a scenario, where an object specific meta class
	 * 	tries to call a static method on the object, in which case the normal
	 * 	meta class for the objects class will be retrieved from there to
	 * 	look up the static meta method.
	 * @param theClass the aspect class which this meta aspect is instantiated
	 * 	for.
	 */
    public MetaAspect(final MetaClassRegistry registry, final Class<?> theClass) {
        super(registry, theClass);
        initialize();
    }
	
    private <T extends PointcutAndAdvice> void collectPAs(Aspect aspect, List<T> allPAs, JoinPoint jp, List<? super T> applicablePAs){
		for(T pa : allPAs){
			Pointcut pcexpr = pa.getPointcut();
			if (matchPointcut(aspect,jp,pcexpr)) {
				if (DEBUG) System.out.println("MetaAspect.collectPAs: \t\t Current join point "+jp+" matched "+pcexpr);
				matchedPointcut(aspect,jp,pa); 
				applicablePAs.add(pa);
				if (DEBUG) System.out.println("In Metaaspect: PA that matched: " + pa);
			} else {
				if (DEBUG) System.out.println("MetaAspect.collectPAs: \t\t Current join point "+jp+" does not match "+pcexpr);
				notMatchedPointcut(aspect,jp,pa); 				
				if (DEBUG) System.out.println("In Metaaspect: PA that matched not: " + pa);
			}
		}
    }
    
	public void receiveBefore(Aspect aspect, JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("MetaAspect.receivingBefore: \t\t Receiving before JP="+jp+" CTXT="+jp.context); 
		collectPAs(aspect, aspect.getBeforePAs(), jp, applicablePAs);
	}

	/**
	 * Recieves a join point and decides whether a defined advice should be executed.
     * The <tt>adviced</tt> return parameter signals to the instrumentation whether the join point was advised of not. In case the join point was not adviced (by default) the original join actions are executed .
     * Add pointcut-and-advice that pointcut match at cuttent join point to the <tt>applicablePAs</tt> list.
     * @param  aspect           The current aspect.
     * @param  jp				The current join point intercepted by aspect manager
     * @param  applicablePAs	List of pointcut-and-advice bindings to which apply at the current join point.
	 * @return True if join point was advised; False, if no pointcut has matched and no advice was executed.
	 */
	public void receiveAround(Aspect aspect, JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("MetaAspect.receivingAround: \t\t Receiving before JP="+jp+" CTXT="+jp.context); 
		collectPAs(aspect, aspect.getAroundPAs(), jp, applicablePAs);
	}

	public void receiveAfter(Aspect aspect, JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("Aspect.receivingAfter: \t\t Receiving after JP="+jp+" CTXT="+jp.context); 
		collectPAs(aspect, aspect.getAfterPAs(), jp, applicablePAs);
	}
	
	public boolean matchPointcut(Aspect aspect, JoinPoint jp, Pointcut pc){
		AspectManager.getInstance().disableJoinPointSpawningForCurrentThread();
		boolean result = pc.match(jp);
		AspectManager.getInstance().enableJoinPointSpawningForCurrentThread();
		return result;
	}
	
	public void matchedPointcut(Aspect aspect, JoinPoint jp, PointcutAndAdvice pa) {
		
	}
	
	public void notMatchedPointcut(Aspect aspect, JoinPoint jp, PointcutAndAdvice pa) {
		
	}

	public void beforeCallingAdvice(Aspect aspect, JoinPoint jp, PointcutAndAdvice pa) {
		
	}
	
	public void interactionAtJoinPoint(Aspect aspect, JoinPoint jp, Set<Aspect> aspectInterferenceSet, List<PointcutAndAdvice> applicablePAs) {
		
	}

	public void afterCallingAdvice(Aspect aspect, JoinPoint jp, PointcutAndAdvice pa) {
		
	}
}
