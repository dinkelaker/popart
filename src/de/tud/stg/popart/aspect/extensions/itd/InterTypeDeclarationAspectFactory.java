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
package de.tud.stg.popart.aspect.extensions.itd;

import groovy.lang.Closure;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClassRegistry;

import java.util.Map;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.dslsupport.ContextDSL;

/**
 * This class extends the OrderedAspectFactory Implementation and replaces
 * the OrderedAspects by Inter Type Declaration Aspects
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationAspectFactory extends AspectFactory {

	@Override
	public Aspect createAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
		return new InterTypeDeclarationAspect(params, interpreter, definition);
	}

	@Override
	public void initAspectMetaObjectClass() {
		MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
		synchronized (registry) {
			registry.setMetaClass(InterTypeDeclarationAspect.class, new InterTypeDeclarationMetaAspect(registry, InterTypeDeclarationAspect.class));
		}
	}
}
