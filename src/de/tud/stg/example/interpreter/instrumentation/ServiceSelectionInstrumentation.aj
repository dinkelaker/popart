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
package de.tud.stg.example.interpreter.instrumentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.Proceed;
import de.tud.stg.popart.aspect.extensions.instrumentation.PopartInterestCache;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.example.interpreter.metamodel.Registry;
import de.tud.stg.example.interpreter.metamodel.ServiceProxy;

public aspect ServiceSelectionInstrumentation {
	
	private final boolean DEBUG = false;
	
	Collection<ServiceProxy> around (String category, final Registry registry) : execution(Collection Registry.find(String)) && args(category) && target(registry) {
		if (! PopartInterestCache.isPopartInterested(Registry.class, "find"))
			return proceed(category, registry);
		if (DEBUG) System.out.println("INSTRUMENTATION: \t before service selection "+category);
		
		Map<String, Object> context = new java.util.HashMap<String, Object>();
		context.put("targetObject", registry);
		context.put("category", category);
		JoinPoint jp = new ServiceSelectionJoinPoint(category, "Registry.find():43", context); 
		context.put("thisJoinPoint", jp);
		AspectManager.getInstance().fireJoinPointBeforeToAspects(jp);
		
		final String _category = (String)context.get("category");
		if (DEBUG) System.out.println("INSTRUMENTATION: \t around service selection "+_category);
		//Extracting Original Join Point Actions and Storing them Callable Proceed Closure
	    context.put("proceed",new Proceed() {
	        public Object call(List<Object> args) {
	           return proceed(_category, registry);
	        }
        });
		jp.location = "Registry.find():43";
		AspectManager.getInstance().fireJoinPointAroundToAspects(jp);
		Collection selectedServices = (Collection)context.get("result");

		if (DEBUG) System.out.println("INSTRUMENTATION: \t after service selection "+category);
		context.put("selectedServices", selectedServices);
		jp.location = "Registry.find():55";
		AspectManager.getInstance().fireJoinPointAfterToAspects(jp);
		
		return (Collection)context.get("selectedServices");
	}

}
