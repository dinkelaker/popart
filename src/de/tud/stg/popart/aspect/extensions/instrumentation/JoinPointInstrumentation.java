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
package de.tud.stg.popart.aspect.extensions.instrumentation;

import java.util.List;
import java.util.Map;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.IProceed;
import de.tud.stg.popart.joinpoints.JoinPoint;

/**
 * The base class for all user defined JoinPoints. They should inherit and override the four empty methods:
 * prolog, prologForAround, EpilogForAround and epilog.
 * The class object for the children of this class should be passed to
 * <code>InstrumentationActivator.declareJoinPoint</code>.
 * The InstrumentationActivator creates a new instance of the given class for every JoinPoint
 * and calls the firePopartJoinPoints method on it.
 * Every subclass needs a public constructor with no arguments, as reflection is used to instantiate them.
 * @author Jan Stolzenburg
 */
public abstract class JoinPointInstrumentation {
	
	public static final boolean DEBUG = false;
	
	protected JoinPoint joinPoint;
	protected Map<String,Object> joinPointContext;
	protected InstrumentationContextParameter instrumentationContext;
	
	public Object firePopartJoinPoints(InstrumentationContextParameter instrumentationContext) {
		this.instrumentationContext = instrumentationContext;
		prolog();
		if (AspectManager.getInstance().isJoinPointSpawningEnabledForCurrentThread() && (joinPoint != null) && (joinPointContext != null)) {
			copyApplicationContextToJoinPointContext();
			fireJoinPoints();
			writeBackJoinPointContextToApplicationContext();
		} else {
			return callMethodWithoutFiringJoinPoints();
		}
		epilog();
		return joinPointContext.get("result");
	}
	
	/**
	 * This method will be called before firing the join points. By
	 * default, all the instrumentation contexts values are transferred
	 * to the join point context.
	 */
	private void copyApplicationContextToJoinPointContext() {
		joinPointContext.put("staticJoinPointInformationSet", true);
		joinPointContext.put("instrumentationContext", instrumentationContext);
		joinPointContext.put("receiver", instrumentationContext.getReceiver());
		joinPointContext.put("methodName", instrumentationContext.getMethodName());

		if(!joinPointContext.containsKey("args")){
			//Convert args to an ArrayList since the AspectManager makes an according cast.
			List<Object> args = new java.util.ArrayList<Object>(instrumentationContext.getArgs().length);
			for(Object arg : instrumentationContext.getArgs()) args.add(arg);
			joinPointContext.put("args", args);
		}
		joinPointContext.put("targetObject", instrumentationContext.getReceiver());
	}

	/**
	 * This method will be called before invoking the final proceed,
	 * to write through changes to the arguments to the instrumentation
	 * context. The default implementation just transfers the arguments
	 * back, like they were transferred into the context unchanged in
	 * {@link #copyApplicationContextToJoinPointContext()}. If you changed
	 * the way arguments were transferred to the joinPointContext by
	 * overriding that method, you should override this aswell to reverse
	 * the changes.
	 */
	protected void writeBackJoinPointContextToApplicationContext() {
		//Convert args list back to array, since groovy uses arrays for args.
		List<Object> args = (List<Object>)joinPointContext.get("args");
		instrumentationContext.setArgs(args.toArray());
	}

	private void fireJoinPoints() {
		/*
		 * all join point firings must be run through the same
		 * aspect manager because before and after ohterwise modify
		 * different join point stacks which could lead to ugly bugs.
		 */
		AspectManager runner = AspectManager.getInstance();
		runner.fireJoinPointBeforeToAspects(joinPoint);
		prologForAround();
		
		joinPointContext.put("proceed", new IProceed() {
			public Object call(List<Object> args) {
				writeBackJoinPointContextToApplicationContext();
				Object result = instrumentationContext.proceed();
				joinPointContext.put("result", result);
				return result;
			}
			@Override
			public String toString() {
				return "InstrumentationMethodExecution_finalProceed";
			}
		});
		runner.fireJoinPointAroundToAspects(joinPoint);
		epilogForAround();
		runner.fireJoinPointAfterToAspects(joinPoint);
	}
	
	private Object callMethodWithoutFiringJoinPoints() {
		return instrumentationContext.proceed();
	}
	
	//Stubs and not just abstract methods, so children don't have to implement them if they don't need them.
	/**
	 * This method will be called on invocation of the instrumentation.
	 * it should put a new join point instance into the joinPoint field
	 * and a new map into the context field. If you do not put any "args"
	 * into the context, the methods args will be copied to that key.
	 * However, if you do put something other than the original argument
	 * array in the context using the "args" key, you will have to override
	 * {@link #writeBackJoinPointContextToApplicationContext()} to write
	 * back the original method args to the instrumentation context, since
	 * the base implementation probably won't fit your version of "args".
	 */
	protected void prolog() {}
	
	/**
	 * This method will be called before any around-advices are invoked.
	 */
	protected void prologForAround() {}
	/**
	 * This method will be called before any after-advices are invoked
	 */
	protected void epilogForAround() {}
	/**
	 * This method will be called before the invocation of this
	 * instrumentation ends.
	 */
	protected void epilog() {}
}