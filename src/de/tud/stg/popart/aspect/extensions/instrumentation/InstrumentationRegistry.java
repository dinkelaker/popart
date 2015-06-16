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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import groovy.lang.Closure;

/**
 * This is the InstrumentationRegistry. It offers simple access to all
 * instrumentation features:<ul>
 * <li>Instrumenting Closures:<ul>
 * 		<li>{@link #instrumentGlobally(Closure)}</li>
 * 		<li>{@link #instrumentClass(Class, Closure)}</li>
 * 		<li>{@link #instrumentMethod(Class, String, Closure)}</li>
 * </ul></li>
 * <li>Reset registered Instrumentations:<ul>
 * 		<li>{@link #resetGlobalInstrumentations()}</li>
 * 		<li>{@link #resetClassInstrumentationsFor(Class)}</li>
 * 		<li>{@link #resetMethodInstrumentationsFor(Class, String)}</li>
 * 		<li>{@link #resetAllInstrumentations()}</li>
 * 		<li>{@link #resetAllInstrumentationsFor(Class)}</li>
 * </ul></li>
 * <li>Invoke Instrumetation proceed chains<ul>
 * 		<li>{@link #invokeInstrumentations(Object, String, Object[], Closure)}</li>
 * </ul></li>
 * </ul>
 * @author Joscha Drechsler
 */
public abstract class InstrumentationRegistry {
	// ============ INSTRUMENTATION COLLECTIONS =============

	/**
	 * A map of maps of lists of closures, storing instrumentations for
	 * specific methods of specific classes.
	 */
	private static final Map<Class<?>, Map<String, List<Proceed>>> methodInstrumentations = new java.util.HashMap<Class<?>, Map<String, List<Proceed>>>();
	/**
	 * A map of lists of closures, storing instrumentations for all methods
	 * of specific classes.
	 */
	private static final Map<Class<?>, List<Proceed>> classInstrumentations = new java.util.HashMap<Class<?>, List<Proceed>>();
	/**
	 * A list of closures, storing instrumentations for all methods of all
	 * classes.
	 */
	private static final List<Proceed> globalInstrumentations = new java.util.LinkedList<Proceed>();

	// ============ INSTRUMENTATION RESET METHODS =============

	/**
	 * This method will reset all instrumentations, which are registered
	 * to instrument all methods of all classes.
	 */
	public static void resetGlobalInstrumentations(){
		synchronized (InstrumentationRegistry.class) {
			proceedChainCache.clear();
			globalInstrumentations.clear();
		}
	}

	/**
	 * This method will reset all instrumentations, which are registered
	 * to instrument all methods of the given class
	 * @param theClass the class
	 */
	public static void resetClassInstrumentationsFor(Class<?> theClass){
		synchronized (InstrumentationRegistry.class) {
			proceedChainCache.remove(theClass);
			classInstrumentations.remove(theClass);
		}
	}
	
	/**
	 * This method will reset all instrumentations, which are registered
	 * to instrument all methods with the given name of the given class
	 * @param theClass the class
	 * @param methodName the method name
	 */
	public static void resetMethodInstrumentationsFor(Class<?> theClass, String methodName){
		synchronized (InstrumentationRegistry.class) {
			//clear cache first
			Map<String, List<Proceed>> methodCache = proceedChainCache.get(theClass);
			if(methodCache != null) methodCache.remove(methodName);
			//then reset instrumentations
			Map<String, List<Proceed>> methodMap = methodInstrumentations.get(theClass);
			if(methodMap == null) return;
			List<Proceed> list = methodMap.get(methodName);
			if(list == null) return;
			list.clear();
		}
	}

	/**
	 * This method will reset all instrumentations. This includes global
	 * instrumentations and all class-wide and method instrumentations
	 * for all classes.
	 */
	public static void resetAllInstrumentations(){
		synchronized (InstrumentationRegistry.class) {
			proceedChainCache.clear();
			globalInstrumentations.clear();
			classInstrumentations.clear();
			methodInstrumentations.clear();
		}
	}

	/**
	 * This method will reset all instrumentations associated with the
	 * given class. This includes it's class-wide instrumentations
	 * and all instrumentations of its methods.
	 * @param theClass the class
	 */
	public static void resetAllInstrumentationsFor(Class<?> theClass){
		synchronized (InstrumentationRegistry.class) {
			//clear cache first
			proceedChainCache.remove(theClass);
			//then reset instrumentations
			classInstrumentations.remove(theClass);
			methodInstrumentations.remove(theClass);
		}
	}

	// ============ INSTRUMENTATION METHODS =============

	/**
	 * This method instruments a Closure as a reifier globally,
	 * meaning it will be called for all methods of all classes.<br>
	 * The last instrumented reifier will be invoked first.<br>
	 * Global reifiers are called first.<br>
	 * @param closure the Closure
	 */
	public static void instrumentGlobally(Closure closure){
		instrumentGlobally(new ClosureProceed(closure));
	}

	/**
	 * This method instruments a reifier globally, meaning it will be
	 * called for all methods of all classes.<br>
	 * The last instrumented reifier will be invoked first.<br>
	 * Global reifiers are called first.<br>
	 * @param reifier the Proceed object
	 */
	public static void instrumentGlobally(Proceed reifier){
		synchronized (InstrumentationRegistry.class) {
			proceedChainCache.clear();
			globalInstrumentations.add(reifier);
		}
	}
	

	/**
	 * This method instruments a Closure as reifier class-wide, meaning
	 * it will be called for all methods of the given class.
	 * The last instrumented reifier will be invoked first.<br>
	 * Class reifiers are called after global reifiers
	 * and before method reifiers.<br>
	 * @param theClass the class
	 * @param closure the Closure
	 */
	public static void instrumentClass(Class<?> theClass, Closure closure){
		instrumentClass(theClass, new ClosureProceed(closure));
	}
	
	/**
	 * This method instruments a reifier class-wide, meaning
	 * it will be called for all methods of the given class.
	 * The last instrumented reifier will be invoked first.<br>
	 * Class reifiers are called after global reifiers
	 * and before method reifiers.<br>
	 * @param theClass the class
	 * @param reifier the Proceed object
	 */
	public static void instrumentClass(Class<?> theClass, Proceed reifier){
		synchronized (InstrumentationRegistry.class) {
			//clear cache first
			proceedChainCache.remove(theClass);
			//then store the closure
			List<Proceed> list = classInstrumentations.get(theClass);
			if(list == null){
				list = new java.util.LinkedList<Proceed>();
				classInstrumentations.put(theClass,list);
			}
			list.add(reifier);
		}
	}

	/**
	 * This method instruments a Closure as a reifier to all methods of
	 * the given name within the given class.
	 * The last instrumented reifier will be invoked first.<br>
	 * Method-specific reifiers are called last.<br>
	 * @param theClass the class
	 * @param methodName the method
	 * @param closure the Closure
	 */
	public static void instrumentMethod(Class<?> theClass, String methodName, Closure closure){
		instrumentMethod(theClass, methodName, new ClosureProceed(closure));
	}
	
	/**
	 * This method instruments a reifier to all methods of
	 * the given name within the given class.
	 * The last instrumented reifier will be invoked first.<br>
	 * Method-specific reifiers are called last.<br>
	 * @param theClass the class
	 * @param methodName the method
	 * @param reifier the Proceed object
	 */
	public static void instrumentMethod(Class<?> theClass, String methodName, Proceed reifier){
		synchronized (InstrumentationRegistry.class) {
			//clear cache first
			Map<String, List<Proceed>> methodCache = proceedChainCache.get(theClass);
			if(methodCache != null) methodCache.remove(methodName);
			//then store the reifier
			Map<String, List<Proceed>> methodMap = methodInstrumentations.get(theClass);
			if(methodMap == null){
				methodMap = new java.util.HashMap<String, List<Proceed>>();
				methodInstrumentations.put(theClass, methodMap);
			}
			List<Proceed> list = methodMap.get(methodName);
			if(list == null){
				list = new java.util.LinkedList<Proceed>();
				methodMap.put(methodName,list);
			}
			list.add(reifier);
		}
	}

	// ============ INSTRUMENTATION LIST RETRIEVAL METHODS =============

	/**
	 * This method adds all instrumentations registered for the given
	 * class to the given list.
	 * @param theClass the class
	 * @param list the list
	 */
	private static void addClassInstrumentations(Class<?> theClass, List<Proceed> list){
		List<Proceed> additions = classInstrumentations.get(theClass);
		if(additions != null) list.addAll(additions);
	}

	/**
	 * This method app all instrumentations registered for the methods
	 * of the given name within the given class to the given list.
	 * @param theClass the class
	 * @param methodName the method name
	 * @param list the list
	 */
	private static void addMethodInstrumentations(Class<?> theClass, String methodName, List<Proceed> list){
		Map<String, List<Proceed>> methodMap = methodInstrumentations.get(theClass);
		if(methodMap == null) return;
		List<Proceed> additions = methodMap.get(methodName);
		if(additions != null) list.addAll(additions);
	}

	/**
	 * A Map linking a classes method names to a list of instrumented closures.
	 * This map is used by {@link #getProceedList(Object, String, Closure)} to
	 * cache the results of {@link #buildProceedList(Class, String, Closure)}.
	 * While this map has the same signature as {@link #methodInstrumentations},
	 * it stores different values: the Class and Global instrumentations are
	 * included in the lists referenced by this cache additionally. Since a
	 * method invocation is considered to happen far more frequently than changes
	 * to the relatively static instrumentation, and the calculation of the
	 * proceed chain is a quite expensive operation (due to extensive list usage),
	 * this cache should improve an average programs execution a lot. The lists
	 * stored within this cache MUST NOT be modified! However, removing their
	 * references is not a problem since only modifications would break the
	 * iterators currently in use by the program execution.
	 */
	private static final Map<Class<?>, Map<String, List<Proceed>>> proceedChainCache = new java.util.HashMap<Class<?>, Map<String, List<Proceed>>>(); 
	
	/**
	 * This method returns a list of all instrumented Closures which is to
	 * be invoked instead of directly invoking the method. The list
	 * contains all global instrumentations, all class-wide
	 * instrumentations of the target objects class and all method
	 * specific instrumentations of the method of that class, in that order.
	 * The result will be cached.
	 * @param receiver the target object of the call
	 * @param methodName the called methods name
	 * @return the list of proceed closures
	 */
	private static List<Proceed> getProceedList(Class<?> receiverClass, String methodName) {
		synchronized (InstrumentationRegistry.class) {
			Map<String, List<Proceed>> methodCache = proceedChainCache.get(receiverClass);
			if(methodCache == null){
				methodCache = new java.util.HashMap<String, List<Proceed>>();
				proceedChainCache.put(receiverClass, methodCache);
			}
			List<Proceed> cachedProceedChain = methodCache.get(methodName);
			if(cachedProceedChain == null){
				cachedProceedChain = buildProceedList(receiverClass, methodName);
				methodCache.put(methodName, cachedProceedChain);
			}
			return cachedProceedChain;
		}
	}
	
	/**
	 * This method builds a list of all instrumented Closures which is to
	 * be invoked instead of directly invoking the method. The list
	 * contains all global instrumentations, all class-wide
	 * instrumentations of the target objects class and all method
	 * specific instrumentations of the method of that class, in that order.
	 * @param receiver the target object of the call
	 * @param methodName the called methods name
	 * @return the list of proceed closures
	 */
	private static List<Proceed> buildProceedList(Class<?> theClass, String methodName) {
		List<Proceed> allReifierForSingleClasses = new java.util.LinkedList<Proceed>();
		List<Proceed> allReifierForSingleMethods = new java.util.LinkedList<Proceed>();

		synchronized (InstrumentationRegistry.class) {
			//Iterate the class hierarchy upwards..
			for(; theClass != null; theClass = theClass.getSuperclass()){
				//..contatenating all class-wide instrumentations..
				addClassInstrumentations(theClass, allReifierForSingleClasses);
				//..and method-specific instrumentations in seperate lists.
				addMethodInstrumentations(theClass, methodName, allReifierForSingleMethods);
			}
		}

		/*
		 * Concatenate the global instrumentations list with the list
		 * collected for the objects class and the list specified for
		 * the objects class' method.
		 */
		List<Proceed> result = new java.util.ArrayList<Proceed>(
				globalInstrumentations.size() +
				allReifierForSingleClasses.size() +
				allReifierForSingleMethods.size());
		//combine the final proceed and all three separated lists into one list.
		result.addAll(allReifierForSingleMethods);
		result.addAll(allReifierForSingleClasses);
		result.addAll(globalInstrumentations);
		//reverse that list and return it.
		Collections.reverse(result);
		return result;
	}

	
	// ===== RUNTIME INVOCATION METHODS =====

	/**
	 * This method will build and invoke the instrumentation
	 * chain for the call of a method of the given name with the
	 * given arguments on the given object, including the given
	 * closure as the chains last element.
	 * @param receiver the object on which the method is being called
	 * @param receiverClass the receivers class
	 * @param methodName the called methods name
	 * @param args the calls arguments
	 * @param finalProceed the final proceed, which will invoke
	 * 		the actually called method, storing it's result in the
	 * 		{@link InstrumentationContextParameter} object which will
	 * 		be passed as its first and only parameter.
	 */
	public static Object invokeInstrumentations(Object receiver, Class<?> receiverClass, String methodName, Object[] args, Proceed finalProceed) {
		//check if popart is interested
		/*
		 *  TODO partial evaluation should depend on the instrumented join point variants.
		 *  Currently, partial evaluation is only done for method execution and call join points.
		 *  Whenever a join point, that supports partial evaluation, and evaluates to false for both
		 *  of these join point types, popart interest will be "false", causing the registry to not invoke
		 *  any instrumented join points, thereby skipping the join point instrumentation for other join
		 *  points than method call and execution, even if there were pointcuts that would match these other
		 *  join point types.
		 *  Possible solutions would be:
		 *  	-> Move partial evaluation to the (join point) instrumentations so that each reifier itself
		 *  		can decide if it should be invoked.
		 *  	-> Make the registries partial evaluation dependant on the instrumented join point types.
		 */
		if(PopartInterestCache.isPopartInterested(receiverClass, methodName)){
			//Build the proceed chain
			List<Proceed> proceeds = getProceedList(receiverClass, methodName);
			//Build the context
			InstrumentationContextParameter context = new InstrumentationContextParameter(receiver, methodName, args, proceeds.iterator(), finalProceed);
			//invoke the proceed chain
			return context.proceed();
		} else {
			//if not, there is no need to invoke the aspect system, so just continue.
			return finalProceed.proceed(new InstrumentationContextParameter(receiver, methodName, args, null, null));
		}
	}
}
