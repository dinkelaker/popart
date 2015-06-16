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

import groovy.lang.MetaMethod;

/**
 * This MetaMethod decorates another MetaMethod adding the instrumentation
 * functionality that reifies the application context information. 
 * @author Joscha Drechsler
 */
public class InstrumentationMetaMethod extends DelegatingMetaMethod {
	/**
	 * the InstrumentationMetaClass where this method is from.
	 */
	private InstrumentationMetaClass decorator;
	
	/**
	 * the final proceed closure for proceed chains invoked from this meta method.
	 */
	private Proceed finalProceed;

	/**
	 * Default constructor.
	 * @param decorator the instrumentation meta class declaring this method
	 * @param delegate the method to be decorated
	 */
	public InstrumentationMetaMethod(InstrumentationMetaClass declaringMetaClass, MetaMethod delegate){
		super(delegate);
		decorator = declaringMetaClass;
		this.parameterTypes = delegate.getParameterTypes();
		
		/*
		 * Build the final proceed closures.
		 * Since the parameters may have been changed, try to retrieve
		 * a MetaMethod for the new argument set. If none can be found,
		 * do a normal method invocation on the decorated meta class
		 * which will usually throw the corresponding
		 * MissingMethodException. If one can be found, forward
		 * the doMethodInvoke call.
		 */
		finalProceed = new Proceed(){
			public Object proceed(InstrumentationContextParameter context) {
				if(isStatic()){
					MetaMethod newMetaMethod = decorator.getAdaptee().getStaticMetaMethod(context.getMethodName(), context.getArgs());
					if(newMetaMethod == null){
						return decorator.getAdaptee().invokeStaticMethod(context.getReceiver(), context.getMethodName(), context.getArgs());
					}else{
						return newMetaMethod.doMethodInvoke(context.getReceiver(), context.getArgs());
					}
				}else{
					MetaMethod newMetaMethod = decorator.getAdaptee().getMetaMethod(context.getMethodName(), context.getArgs());
					if(newMetaMethod == null){
						return decorator.getAdaptee().invokeMethod(context.getReceiver(), context.getMethodName(), context.getArgs());
					}else{
						return newMetaMethod.doMethodInvoke(context.getReceiver(), context.getArgs());
					}
				}
			}
			@Override
			public String toString() {
				return InstrumentationMetaMethod.this+".finalProceed";
			}
		};
	}

	@Override
	public Object clone() {
		return decorator.retrieveOrCreateDecoratedMetaMethod((MetaMethod)getDelegate().clone());
	}

	@Override
	public Object doMethodInvoke(Object object, Object[] argumentArray) {
		//invoke instrumentation chain.
		return InstrumentationRegistry.invokeInstrumentations(object, decorator.getTheClass(), getSimpleName(), argumentArray, finalProceed);
	}

	@Override
	public Object invoke(Object object, Object[] arguments) {
		//invoke instrumentation proceed chain
		return InstrumentationRegistry.invokeInstrumentations(object, decorator.getTheClass(), getSimpleName(), arguments, finalProceed);
	}
	
	/**
	 * This builds the simple method name which was used at the call site.
	 * @return a simple method name like "toString"
	 */
	private String getSimpleName(){
		String name = getName();
		/**
		 * my.package.SomeClass.InnerClass$SomeAnonymousClass.toString
		 * my.package.SomeClass.this$2$overloadMethod
		 * my.package.SomeClass$AnonymousClass.super$1$superCall
		 */
		//cut at last "."
		name = name.substring(name.lastIndexOf(".")+1);
		/*
		 * and cut at last "$" (this is simple because if there is no
		 * $ char in this name, lastIndexOf returns -1 which - increased
		 * by 1 - is 0, and thus substring(0) returns the whole String.
		 */
		name = name.substring(name.lastIndexOf(String.valueOf((char)36))+1);
		return name;
	}
}
