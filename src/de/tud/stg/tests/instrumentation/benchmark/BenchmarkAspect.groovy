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
package de.tud.stg.tests.instrumentation.benchmark;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.itd.ITDCCCombiner;
import de.tud.stg.popart.aspect.extensions.itd.StructuralPointcutDSL;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;
import de.tud.stg.popart.pointcuts.BooleanPCD;
import de.tud.stg.popart.pointcuts.Pointcut;

public abstract class BenchmarkAspect {
	/**
	 * logs stuff like:
	 * before calc=299
	 * around pre-proceed calc=299
	 * around post-proceed calc=299
	 * after calc=299
	 */
	public static final Map<String,Integer> logger = [:]
	
	private static void aspect(Map<String,Object> params, Closure definition) {
		Aspect aspect = new ITDCCCombiner().eval(params,definition)
		AspectManager.getInstance().register(aspect)
	}

	public static void registerNonMatchingAspect(){
		String pattern = "no (class|package) will ever* match this regular expression.."
		/*  *) this pattern serves the following purpose:
		 *      - it is medium-expensive to evaluate, so if partial evaluation
		 *        is disabled, it will take some time to re-evaluate it all
		 *        the time.
		 *      - it will be evaluated completely by partial evaluation, so if
		 *        partial evaluation is enabled, it will be evaluated only
		 *        once per method (advice) or class (introduction) and it's
		 *        result will be cached and thus on re-evaluations not take
		 *        any time anymore. 
		 */
		registerAspect(StructuralPointcutDSL.eval{
			within_package_hierarchy(~pattern) | 
			is_type(~pattern) 
		});
	}
	
	public static void registerAspect(StructureDesignator clientClasses){
		aspect(name:'BenchmarkAspect') {
			introduce_field(clientClasses, "someFieldThatIsNeverUsed", "defaultValue");
			introduce_method(clientClasses, "someMethodThatIsNeverUsed", { ->
				assert false;
			});
			
			before(clientClasses & method_call(".*")) {
				String key = "before "+methodName;
				if(!logger[key]) logger[key] = 1;
				else logger[key]++
			}
			around(clientClasses & method_execution(".*")) {
				String key = "around "+methodName+" pre-proceed";
				if(!logger[key]) logger[key] = 1;
				else logger[key]++
				def result = proceed()
				key = "around "+methodName+" post-proceed";
				if(!logger[key]) logger[key] = 1;
				else logger[key]++
				return result
			}
			after(clientClasses & method_call(".*")) {
				String key = "after "+methodName;
				if(!logger[key]) logger[key] = 1;
				else logger[key]++
			}
		}
	}
}
