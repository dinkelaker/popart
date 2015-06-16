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

import java.util.HashMap;
import java.util.Map;

import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.joinpoints.JoinPoint;


/**
 * This class extends the <tt>Aspect</tt> meta object class with the profiling semantics for aspects.
 * The overall execution times for all advice executions as well as the minimal and the maximal execution times are measured.
 * All durations are measure as nano seconds.
 * @author Tom Dinkelaker
 */
public class ProfilingAspect extends CountingAspect { 
	
	protected HashMap<Integer,Long> perActivationTimes = new HashMap<Integer,Long>(); 
	protected HashMap<PointcutAndAdvice,Long> perPointcutAndAdviceExecutionTimes = new HashMap<PointcutAndAdvice,Long>(); 
	protected HashMap<PointcutAndAdvice,Long> perPointcutAndAdviceMinExecutionTimes = new HashMap<PointcutAndAdvice,Long>(); 
	protected HashMap<PointcutAndAdvice,Long> perPointcutAndAdviceMaxExecutionTimes = new HashMap<PointcutAndAdvice,Long>(); 
	 
	public ProfilingAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
        super(params,interpreter,definition);
        if (DEBUG) System.out.print("ProfilingAspect.<init>: \t\t New Aspect name:"+params.get("name")); 
	}
	
	//METHODS USED BY ASPECT MANAGER
		
    public Object clone() {
    	Map<String,Object> params = new java.util.HashMap<String,Object>();
    	params.put("name",this.name);
    	params.put("deployed",this.isDeployed());
    	params.put("perInstance",this.perInstanceScope);
    	params.put("perClass",this.perClassScope);
    	params.put("priority",this.priority);
    	return new ProfilingAspect(params, getInterpreter(), getDefinitionClosure());
    }
    
	//METHODS FOR META-ASPECT PROTOCOL 
	public void beforeCallingAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG) System.out.println("ProfilingAspect.beforeCallingAdvice: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut());
		int key = jp.hashCode() ^ pa.hashCode();
		this.perActivationTimes.put(key,System.nanoTime());
	}
	
	public void afterCallingAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG) System.out.println("ProfilingAspect.afterCallingAdvice: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut()); 
		long end = System.nanoTime();
		long key = jp.hashCode() ^ pa.hashCode();
		long start = perActivationTimes.get(key);
		this.perActivationTimes.remove(key);
		long duration = end - start;
		
		Long total_duration = perPointcutAndAdviceExecutionTimes.get(pa);
		if (total_duration == null) {
		  perPointcutAndAdviceExecutionTimes.put(pa,duration);			
		} else {
		  perPointcutAndAdviceExecutionTimes.put(pa,total_duration+duration);
		}
		
		Long oldMin = perPointcutAndAdviceMinExecutionTimes.get(pa);
		if (oldMin == null) oldMin = Long.MAX_VALUE;
		if (duration < oldMin) perPointcutAndAdviceMinExecutionTimes.put(pa,duration);

		Long oldMax = perPointcutAndAdviceMaxExecutionTimes.get(pa);
		if (duration > oldMax) perPointcutAndAdviceMaxExecutionTimes.put(pa,duration);
	}
	
	public void notMatchingPointcut(JoinPoint jp, PointcutAndAdvice pa) {
        super.notMatchingPointcut(jp,pa);
		//if (jp.context.thisAspect.name=="CounterAroundCalls") println ">>>>>ProfilingAspect.notMatchingPointcut: \t\t \nname=${this.name} \njp=$jp \npa=$pa.pointcut"
	}

	//METHODS USED BY RUN-TIME API
    
    public long getDurationTime(PointcutAndAdvice pa) {
    	Long duration = perPointcutAndAdviceExecutionTimes.get(pa);
    	return (duration == null)? 0 : duration;
    }
    
    public long getMinTime(PointcutAndAdvice pa) {
    	Long duration = perPointcutAndAdviceMinExecutionTimes.get(pa);
    	return (duration == null)? Integer.MAX_VALUE : duration;
    }
    
    public long getMaxTime(PointcutAndAdvice pa) {
    	Long duration = perPointcutAndAdviceMaxExecutionTimes.get(pa);
    	return (duration == null)? Integer.MIN_VALUE : duration;
    }
    
    public long getAvgDurationTime(PointcutAndAdvice pa) {
//    	long duration = getDurationTime(pa);
//    	long cnt = getMatchingPointcutsCounterPerPA(pa);
//    	if (duration == 0) return 0;
//    	assert (cnt != 0);
//    	return Math.round(duration/cnt);
    	long total = getDurationTime(pa);
    	long cnt = getMatchingPointcutsCounterPerPA(pa);
        return (cnt == 0)? 0 : Math.round(total/cnt); 
    }
    
}

