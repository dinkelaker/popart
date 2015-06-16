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

import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;

/**
 * This is a MetaClass which enables the Instrumentation functionality
 * by decorating an original meta class.<br>
 * This MetaClass is REQUIRED! to be be the directly associated MetaClass
 * (meaning not decorated by other meta classes or similar) in order
 * for the {@link InstrumentationRegistry}s management methods to work.<br>
 * This class is stateless! it can at any point in time be replaced by
 * another instance with an equal delegate.
 * @author Joscha Drechsler
 */
public class InstrumentationMetaClass extends DelegatingMetaClass {
	/**
	 * A cache associating all the delegates {@link MetaMethod} instances
	 * with their decorated {@link InstrumentationMetaMethod} instances
	 */
	private final Map<Integer, InstrumentationMetaMethod> methodCache = new java.util.HashMap<Integer, InstrumentationMetaMethod>();
	
	/**
	 * decorates the given method with instrumentation functionality.
	 * This method will always return the same decorated instance for
	 * the same delegate meta method.
	 * @param delegate the delegate meta method
	 * @return the decorated meta method
	 */
	public synchronized InstrumentationMetaMethod retrieveOrCreateDecoratedMetaMethod(MetaMethod delegate){
		if(delegate == null) return null;
		Integer key = new Integer(System.identityHashCode(delegate));
		InstrumentationMetaMethod result = methodCache.get(key);
		if(result == null){
			result = new InstrumentationMetaMethod(this, delegate);
			methodCache.put(key, result);
		}
		return result;
	}
	
	/**
	 * The final proceed for normal method calls.
	 */
	private Proceed finalProceed;
	
	/**
	 * The final proceed for static method calls.
	 */
	private Proceed staticFinalProceed;
	
	/**
	 * determines the global behavior if default parameter redirect calls should
	 * be instrumented. Validating, if a call is a default parameter redirect, may
	 * be extremely costly, so you may want to simply enable instrumentation of
	 * these redirect calls globally to remove the necessity of that validation.<br>
	 * Therefore:<br>
	 * 		setting the value to <code>true</code> will simply instrument all calls.
	 * 		This is the fastest way to go.<br>
	 * 		setting the value to <code>false</code> will only instrument the actual
	 * 		method invocations after default parameters have been set. In order to
	 * 		do that, redirect calls must be detected which can be extremly slow.
	 * 		However, this does prevent things like closure.doCall(), which is
	 * 		redirected to closure.doCall(null), from being instrumented twice.
	 */
	public static boolean defaultEnableInstrumentationForDefaultParameterRedirectCalls = true;

	/**
	 * determines, whether the meta class instance should use the global
	 * or its local redirect instrumentation behavior
	 */
	private boolean useGlobalRedirectCallBehavior;
	
	/**
	 * determines the local redirect behavior. <code>true</code> means, all redirect calls
	 * will be instrumented, <code>false</code> means, redirect calls will not be instrumented.
	 * not instrumenting redirect calls may be expensive since redirect calls must be detected.
	 */
	private boolean localRedirectBehavior;
		
	/**
	 * Default Constructor. Package visibility - DO NOT USE!
	 * use methods of {@link InstrumentationMetaClassCreationHandle}.
	 * @param theClass the class for which the instance is created
	 * @param delegate the meta class which is to be decorated
	 */
	InstrumentationMetaClass(MetaClass delegate) {
		super(delegate);
		localRedirectBehavior = defaultEnableInstrumentationForDefaultParameterRedirectCalls;
		useGlobalRedirectCallBehavior = true;
		
		finalProceed = new Proceed(){
			public Object proceed(InstrumentationContextParameter context) {
				return InstrumentationMetaClass.this.getAdaptee().invokeMethod(context.getReceiver(), context.getMethodName(), context.args);
			}
			@Override
			public String toString() {
				return InstrumentationMetaClass.this+".finalProceed";
			}
		};
		staticFinalProceed = new Proceed(){
			public Object proceed(InstrumentationContextParameter context) {
				return InstrumentationMetaClass.this.getAdaptee().invokeStaticMethod(context.getReceiver(), context.getMethodName(), context.args);
			}
			@Override
			public String toString() {
				return InstrumentationMetaClass.this+".staticFinalProceed";
			}
		};
	}

	/**
	 * Constructor with specified redirect behavior. See {@link #InstrumentationMetaClass(MetaClass).
	 * The created metaclass will use the given behavior for redirect calls.
	 * @param delegate the delegate metaclass
	 * @param instrumentDefaultParameterRedirectCalls whether or not to instrument default
	 * 		parameter redirect calls. Setting <code>false</code> may be extremly slow.
	 */
	public InstrumentationMetaClass(MetaClass delegate, boolean instrumentDefaultParameterRedirectCalls) {
		this(delegate);
		localRedirectBehavior = instrumentDefaultParameterRedirectCalls;
		useGlobalRedirectCallBehavior = false;
	}
	
	/**
	 * sets the local redirect behavior, disables use of global redirect behavior.
	 * @param instrumentDefaultParameterRedirectCalls whether or not to instrument default
	 * 		parameter redirect calls. Setting <code>false</code> may be extremly slow.
	 */
	public void setInstrumentDefaultParameterRedirectCalls(boolean instrumentDefaultParameterRedirectCalls) {
		useGlobalRedirectCallBehavior = false;
		this.localRedirectBehavior = instrumentDefaultParameterRedirectCalls;
	}
	
	/**
	 * Reverts to global redirect behavior.
	 */
	public void useGlobalRedirectBehavior(){
		useGlobalRedirectCallBehavior = true;
	}
	
	@Override
	public MetaMethod getMetaMethod(String name, Object[] args) {
		return retrieveOrCreateDecoratedMetaMethod(super.getMetaMethod(name, args));
	}
	
	@Override
	public List<MetaMethod> respondsTo(Object obj, String name) {
		return decorate(super.respondsTo(obj, name));
	}
	
	@Override
	public List<MetaMethod> respondsTo(Object obj, String name, Object[] argTypes) {
		return decorate(super.respondsTo(obj, name, argTypes));
	}

	@Override
	public MetaMethod getStaticMetaMethod(String name, @SuppressWarnings("unchecked") Class[] argTypes) {
		return retrieveOrCreateDecoratedMetaMethod(super.getStaticMetaMethod(name, argTypes));
	}
	
	@Override
	public MetaMethod getStaticMetaMethod(String name, Object[] args) {
		return retrieveOrCreateDecoratedMetaMethod(super.getStaticMetaMethod(name, args));
	}

	@Override
	public List<MetaMethod> getMethods() {
		return decorate(super.getMethods());
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public MetaMethod pickMethod(String methodName, @SuppressWarnings("unchecked") Class[] arguments) {
		return retrieveOrCreateDecoratedMetaMethod(super.pickMethod(methodName, arguments));
	}

	/**
	 * This map contains associates threads with strings, to have a quick
	 * lookup, if a new method call can be excluded as possible
	 * redirect call to skip stack lookup.
	 */
	private static Map<Thread,String> latestMethodNameOnStack = Collections.synchronizedMap(new java.util.HashMap<Thread,String>());
	
	/**
	 * Wraps the called method into a closure. Next, all the reifiers are called, and finally the wraped closure ist called.
	 */
	@Override
	public Object invokeMethod(Object object, String methodName, Object[] arguments) {
		if(delegate.getMetaMethod(methodName,arguments) == null) return super.invokeMethod(object,methodName,arguments);

		boolean instrumentRedirectCalls = useGlobalRedirectCallBehavior ? defaultEnableInstrumentationForDefaultParameterRedirectCalls : localRedirectBehavior;
		if(!instrumentRedirectCalls){
			//default parameter redirect calls should not be instrumented.
			boolean redirectCall = methodName.equals(latestMethodNameOnStack.get(Thread.currentThread())) && StackUtils.isThisCallDefaultParameterRedirect(getTheClass(), methodName);
			if(redirectCall) return super.invokeMethod(object, methodName, arguments);
		}
		
		latestMethodNameOnStack.put(Thread.currentThread(), methodName);
		Object result = InstrumentationRegistry.invokeInstrumentations(object, getTheClass(), methodName, arguments, finalProceed);
		latestMethodNameOnStack.remove(Thread.currentThread());
		return result;
	}
	
	/**
	 * Wraps the called method into a closure. Next, all the reifiers are called, and finally the wraped closure ist called.
	 */
	@Override
	public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
		MetaMethod method = delegate.getStaticMetaMethod(methodName,arguments);
		/*
		 * non existant calls or calls to Class<getTheClass> instance methods
		 * which the groovy MOP erroneously interpreted as static calls
		 * on this class are not to be instrumented.
		 */
		if(method == null || !method.getDeclaringClass().isAssignableFrom(getTheClass())) return super.invokeStaticMethod(object,methodName,arguments);
		
		boolean instrumentRedirectCalls = useGlobalRedirectCallBehavior ? defaultEnableInstrumentationForDefaultParameterRedirectCalls : localRedirectBehavior;
		if(!instrumentRedirectCalls){
			//default parameter redirect calls should not be instrumented.
			boolean redirectCall = methodName.equals(latestMethodNameOnStack.get(Thread.currentThread())) && StackUtils.isThisCallDefaultParameterRedirect(getTheClass(), methodName);
			if(redirectCall) return super.invokeStaticMethod(object, methodName, arguments);
		}

		latestMethodNameOnStack.put(Thread.currentThread(), methodName);
		Object result = InstrumentationRegistry.invokeInstrumentations(object, getTheClass(), methodName, arguments, staticFinalProceed);
		latestMethodNameOnStack.remove(Thread.currentThread());
		return result;
	}
	
	/**
	 * Decorates a list of meta methods
	 * @see {@link #retrieveOrCreateDecoratedMetaMethod(MetaMethod)}
	 * @param list the list of meta methods to be decorated
	 * @return the list of decorated meta methods
	 */
	private List<MetaMethod> decorate(List<MetaMethod> list){
		List<MetaMethod> result = new java.util.ArrayList<MetaMethod>(list.size());
		for(MetaMethod mm : list){
			result.add(retrieveOrCreateDecoratedMetaMethod(mm));
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof InstrumentationMetaClass) && ((InstrumentationMetaClass)obj).getAdaptee().equals(getAdaptee());
	}
}
