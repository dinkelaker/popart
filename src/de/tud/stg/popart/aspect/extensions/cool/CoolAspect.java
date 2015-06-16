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
package de.tud.stg.popart.aspect.extensions.cool;

import groovy.lang.Closure;

import java.util.Map;

import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.aspect.extensions.OrderedAspect;
import de.tud.stg.popart.aspect.extensions.cool.domainmodel.Coordinator;

/**
 * This class extends the <tt>OrderedAspect</tt> meta object class with the semantics of COOL aspects.
 * @author Tom Dinkelaker
 */
public class CoolAspect extends OrderedAspect { 
	
	protected static boolean INTERNAL_DEBUG = false;

	public CoolAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
        super(params,interpreter,definition);
        if (DEBUG || INTERNAL_DEBUG) System.out.print("CoolAspect.<init>: \t\t New Aspect name:"+params.get("name"));
	}
	
	//METHODS USED BY ASPECT MANAGER
		
    public Object clone() {
    	Map<String,Object> params = new java.util.HashMap<String,Object>();
    	params.put("name",this.name);
    	params.put("deployed",this.isDeployed());
    	params.put("perInstance",this.perInstanceScope);
    	params.put("perClass",this.perClassScope);
    	params.put("priority",this.priority);
    	return new CoolAspect(params, getInterpreter(), getDefinitionClosure());
    }
    
	//METHODS FOR META-ASPECT PROTOCOL 
    
	/*
	 * BEGIN-JD-2010-02-17: disabled method. Reason:
	 * This methods only functional part was replacement of the
	 * metaAspect instance with a CoolDebugMetaAspect, which broke the
	 * tests since the DebugMetaAspects expect user input which JUnit
	 * is unable to provide. So this functionallity had to be removed
	 * and what was left was just a load of console output without any
	 * functionallity, so I removed this aswell.
	 */
//	public void afterCallingAdvice(JoinPoint jp, PointcutAndAdvice pa) {
//		if (DEBUG || true) System.out.println("CoolAspect.afterCallingAdvice: \t\t "+this.name+" \njp="+jp+" \npa="+pa.getPointcut());
//
//		AspectManager am = AspectManager.getInstance();
//		List<Aspect> aspects = am.getAspects();
//		
//	    System.out.println("====== refresh COOL Aspects ======");
//	    int i=0;
//	    for (Aspect aspect : aspects ) {
//	     	System.out.println(i+": "+aspect.getName());
//	    	if (aspect instanceof CoolAspect) {
//	          System.out.println(i+": "+aspect.getName()+" is a cool aspect");
//	          System.out.print(i+": "+aspect.getName());
//	    	  
//	            if (!(aspect.getMetaAspect() instanceof CoolDebugMetaAspect)) {
//	      	        System.out.println(" setting the debug meta-aspect");
//	                MetaAspect debugMetaAspect  = new CoolDebugMetaAspect(aspect.getClass());   	  
//	    	        //aspect.setMetaAspect(debugMetaAspect); //TODO Debugging has to be activated at runtime
//	            }
//	    	}
//	    	i++;
//	    }
//	}
    //END-JD-2010-02-17: Disabled method.
	
    //METHODS USED BY RUN-TIME API
    
    private Coordinator coordinator = null; //Is set by ..domainmodel.Coordinator.getAfterCoordinatedMethodAspect/getBeforeCoordinatedMethodAspect

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}
    
	
}

