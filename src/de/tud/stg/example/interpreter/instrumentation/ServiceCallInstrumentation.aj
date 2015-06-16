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
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.Proceed;
import de.tud.stg.popart.aspect.extensions.instrumentation.PopartInterestCache;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.example.interpreter.metamodel.ServiceProxy;

public aspect ServiceCallInstrumentation {
	
	private final boolean DEBUG = false;
	
	Object around (String operation, List<Object> args, ServiceProxy service) : execution(Object ServiceProxy+.call(String, ArrayList)) && args(operation,args) && target(service) {
		if (! PopartInterestCache.isPopartInterested(service.getClass(), "call"))
			return proceed(operation, args, service);
		if (DEBUG) System.out.println("INSTRUMENTATION: \t before service call "+operation);
        
		Map<String, Object> context = new java.util.HashMap<String, Object>();
		context.put("targetObject", service);
		context.put("service", service);
		context.put("operation", operation);
		context.put("args", args);
		context.put("external", new Boolean(true));
		JoinPoint jp = new ServiceCallJoinPoint(operation, "MyServiceProxy.call():23", args.toArray(), context); 
		context.put("thisJoinPoint", jp);
		AspectManager.getInstance().fireJoinPointBeforeToAspects(jp);
		
		final String _operation = (String)context.get("operation");
		final List<Object> _args = (List<Object>)context.get("args");
		final ServiceProxy _service = (ServiceProxy)context.get("service");
  	    if (DEBUG) System.out.println("INSTRUMENTATION: \t around service call "+operation);
		//Extracting Original Join Point Actions and Storing them Callable Proceed Closure
		context.put("proceed",new Proceed() {
	            public Object call(List<Object> _args) {
	               return proceed(_operation,_args, _service);
	            }
        });
		jp.location = "MyServiceProxy.call():34";
		AspectManager.getInstance().fireJoinPointAroundToAspects(jp);
		Object result = context.get("result");
		
		if (DEBUG) System.out.println("INSTRUMENTATION: \t after service call "+operation);
		jp.location = "MyServiceProxy.call():44";
		AspectManager.getInstance().fireJoinPointAfterToAspects(jp);
		result = (Integer)context.get("result");
		
		return result;
	}

}
