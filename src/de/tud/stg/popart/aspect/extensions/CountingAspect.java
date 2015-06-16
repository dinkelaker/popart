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
import java.util.List;
import java.util.Map;

import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.joinpoints.JoinPoint;

/**
 * This class extends the <tt>Aspect</tt> meta object class with the semantics of dynamic deployed aspects.
 * @author Tom Dinkelaker
 */
public class CountingAspect extends DynamicAspect { 
	
	protected boolean COUNTING_DEBUG = false; 
	 
	protected int instanceCounter = 0; 
	protected int receivedJoinPointCounter = 0; 
	protected int matchingPointcutCounter = 0; 
	
	protected HashMap<PointcutAndAdvice,Integer> perPointcutMatchCount = new HashMap<PointcutAndAdvice, Integer>();
	protected HashMap<PointcutAndAdvice,Integer> perPointcutNotMatchCount = new HashMap<PointcutAndAdvice, Integer>();
	 
	public CountingAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
        super(params,interpreter,definition);
        if (DEBUG) System.out.print("CountingAspect.<init>: \t\t New Aspect name:"+params.get("name"));
        //metaAspect = (MetaAspect)this.metaClass;
        //if (DEBUG) System.out.print("CountingAspect.<init>: \t\t metaClass="+metaAspect);
	}
	
	//METHODS USED BY ASPECT MANAGER
		
    public Object clone() {
    	instanceCounter++;
    	Map<String,Object> params = new java.util.HashMap<String,Object>();
    	params.put("name",this.name);
    	params.put("deployed",this.isDeployed());
    	params.put("perInstance",this.perInstanceScope);
    	params.put("perClass",this.perClassScope);
    	params.put("priority",this.priority);
    	return new CountingAspect(params, getInterpreter(), getDefinitionClosure());
    }
    
	public void receiveBefore(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.receivingBefore: \t\t Receiving before JP="+jp+" CTXT="+jp.context); 
		receivedJoinPointCounter++;
		super.receiveBefore(jp,applicablePAs);
	}
	
	public void receiveAround(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.receivingAround: \t\t Receiving around JP="+jp+" CTXT="+jp.context); 
		super.receiveAround(jp,applicablePAs); 
	}

	public void receiveAfter(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.receivingAfter: \t\t Receiving after JP="+jp+" CTXT="+jp.context); 
		super.receiveAfter(jp,applicablePAs);
	}
	
	//METHODS FOR META-ASPECT PROTOCOL 
	public void matchingPointcut(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.matchingPointcut: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut()); 
		matchingPointcutCounter++;	
		Integer matchCount = perPointcutMatchCount.get(pa); 
		if (matchCount != null) {
			perPointcutMatchCount.put(pa,++matchCount);
			if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.matchingPointcut: \t\t inc "+pa+" matchCount"); 
		} else {
			perPointcutMatchCount.put(pa,1);
			if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.matchingPointcut: \t\t new "+pa); 
	    }  
		if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.matchingPointcut: \t\t "+perPointcutMatchCount); 
	}
	
	public void notMatchingPointcut(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.notMatchingPointcut: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut()); 
		Integer notMatchCount = perPointcutNotMatchCount.get(pa); 
		if (notMatchCount != null) {
			perPointcutNotMatchCount.put(pa,++notMatchCount);
			if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.notMatchingPointcut: \t\t inc not "+pa+" "+notMatchCount); 
		} else {
			perPointcutNotMatchCount.put(pa,1);
			if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.notMatchingPointcut: \t\t new not "+pa);
	    }  
		if (DEBUG || COUNTING_DEBUG) System.out.println("CountingAspect.notMatchingPointcut: \t\t "+perPointcutNotMatchCount); 
	}
	
    //METHODS USED BY RUN-TIME API
    
    public int getInstanceCounter() { return this.instanceCounter; }
	
    public int getReceivedJoinPointsCounter() { return this.receivedJoinPointCounter; }
	
    public int getMatchingPointcutsCounter() { return this.matchingPointcutCounter; }
	
    public int getMatchingPointcutsCounterPerPA(PointcutAndAdvice pa) {
    	Integer cnt = perPointcutMatchCount.get(pa);
    	return (cnt==null)? 0 : cnt;
    }
	
}

