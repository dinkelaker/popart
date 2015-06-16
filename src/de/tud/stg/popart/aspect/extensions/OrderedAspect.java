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

import java.util.Map;

import de.tud.stg.popart.dslsupport.ContextDSL;

/**
 * This class extends the <tt>Aspect</tt> meta object class with the semantics of dynamic deployed aspects.
 * @author Tom Dinkelaker
 */
public class OrderedAspect extends DynamicAspect { 
	
	protected static boolean INTERNAL_DEBUG = false;

	public OrderedAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
        super(params,interpreter,definition);
        if (DEBUG || INTERNAL_DEBUG) System.out.print("OrderedAspect.<init>: \t\t New Aspect name:"+params.get("name"));
	}
	
	//METHODS USED BY ASPECT MANAGER
		
    public Object clone() {
    	Map<String,Object> params = new java.util.HashMap<String,Object>();
    	params.put("name",this.name);
    	params.put("deployed",this.isDeployed());
    	params.put("perInstance",this.perInstanceScope);
    	params.put("perClass",this.perClassScope);
    	params.put("priority",this.priority);
    	return new OrderedAspect(params, getInterpreter(), getDefinitionClosure());
    }
    
	//METHODS FOR META-ASPECT PROTOCOL 
	
    //METHODS USED BY RUN-TIME API
    
	
}

