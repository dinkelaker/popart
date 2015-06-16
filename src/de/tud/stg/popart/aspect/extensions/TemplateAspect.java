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

import java.util.List;
import java.util.Map;

import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.joinpoints.JoinPoint;


/**
 * This class extends the <tt>Aspect</tt> meta object class with ##not yet defined## semantics.
 * @author Tom Dinkelaker
 */
public class TemplateAspect extends DynamicAspect { 
	
	public TemplateAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
        super(params,interpreter,definition);
		
        if (DEBUG) System.out.print("TemplateAspect.<init>: \t\t New Aspect name:"+params.get("name"));
	}
	
	//METHODS USED BY ASPECT MANAGER
		
    public Object clone() {
    	Map<String,Object> params = new java.util.HashMap<String,Object>();
    	params.put("name",this.name);
    	params.put("deployed",this.isDeployed());
    	params.put("perInstance",this.perInstanceScope);
    	params.put("perClass",this.perClassScope);
    	params.put("priority",this.priority);
    	return new TemplateAspect(params, getInterpreter(), getDefinitionClosure());
    }
    
	public void receiveBefore(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("TemplateAspect.receivingBefore: \t\t Receiving before JP="+jp+" CTXT="+jp.context); 
		super.receiveBefore(jp,applicablePAs);
	}
	
	public void receiveAround(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("TemplateAspect.receivingAround: \t\t Receiving before JP="+jp+" CTXT="+jp.context); 
		super.receiveAround(jp,applicablePAs); 
	}

	public void receiveAfter(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("TemplateAspect.receivingAfter: \t\t Receiving after JP="+jp+" CTXT="+jp.context); 
		super.receiveAfter(jp,applicablePAs);
	}
	
	//METHODS FOR META-ASPECT PROTOCOL 
	public void matchingPointcut(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG) System.out.println("TemplateAspect.matchingPointcut: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut()); 
	}
	
	public void notMatchingPointcut(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG) System.out.println("TemplateAspect.notMatchingPointcut: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut()); 
	}
	
	public void beforeCallingAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG) System.out.println("TemplateAspect.beforeCallingAdvice: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut()); 
	}
	
	public void afterCallingAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG) System.out.println("TemplateAspect.afterCallingAdvice: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut()); 
	}
	
    //METHODS USED BY RUN-TIME API
    
	
}

