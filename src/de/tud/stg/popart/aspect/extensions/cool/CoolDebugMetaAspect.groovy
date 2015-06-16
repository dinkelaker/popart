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

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.extensions.cool.CoolAspect;
import de.tud.stg.popart.pointcuts.*;
import de.tud.stg.popart.joinpoints.*;
import de.tud.stg.popart.dslsupport.*;

import groovy.lang.MetaClassRegistry;

/**
 * @author dinkelaker
 */
public class CoolDebugMetaAspect extends DebugMetaAspect {
	
	/* (non-Javadoc)
	 * @see de.tud.stg.popart.aspect.extensions.DebugMetaAspect#DebugMetaAspect(java.lang.Class)
	 */
	public CoolDebugMetaAspect(Class theClass) {
		super(theClass);
	}
		
	public CoolDebugMetaAspect(MetaClassRegistry registry, Class theClass) {
		super(registry, theClass);
	}

	/**
	 * Overrides the default semantics for pointcut evaluation.
	 * Pointcut evaluation is intercepted and information about the evaluation of subexpression is printed on the console, and user input is requested for step wise debugging of pointcuts.
	 */
	public boolean matchPointcut(Aspect aspect, JoinPoint jp, Pointcut pc){
			//println "COOL    ["+Thread.currentThread().getId()+"]: aspect.name: "+aspect.name;
			//println "COOL    ["+Thread.currentThread().getId()+"]: aspect.class: "+aspect.class;
			//println "COOL    ["+Thread.currentThread().getId()+"]: aspect.pc: "+pc;
			//println "COOL    ["+Thread.currentThread().getId()+"]: aspect.jp: "+jp;
		 
		if (aspect instanceof CoolAspect) {
			boolean matches = pc.match(jp);
			println "COOL    ["+Thread.currentThread().getId()+"]: matches: "+matches;
			if (matches) {
				println "\n\n================================="
				println "= COOL BREAKPOINT "
				println "================================="
				CoolAspect coolAspect = (CoolAspect)aspect;
				println "COOL    ["+Thread.currentThread().getId()+"]: Coordinator: "+coolAspect.getCoordinator();
				return super.matchPointcut(aspect, jp, pc);
			}
		} 
		return pc.match(jp);	
    }
}    
	
	

