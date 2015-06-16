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

/**
 * @author Jan Stolzenburg
 */
public class InstrumentationActivator {
	/**
	 * Use to declare a join point for a particular method of a class.
	 * Best for declaring domain-specific join point types.
	 * The MetaClass for the class must be configured properly!
	 * @params methodPattern String to which method names exactly match. (TODO: use RegExp)
	 * @params domainClass   Only this class is instrumented.
	 * @params adapterClass  This class is responsible for instrumenting the join point shadows only in methods that names match methodPatter in class domainClass.
	 * @see {@link InstrumentationRegistry#instrumentMethod(Class,String,Closure)}
	 */
	public static void declareJoinPoint(final Class<?> domainClass, final String methodPattern, final Class<? extends JoinPointInstrumentation> adapterClass) {
		InstrumentationRegistry.instrumentMethod(domainClass, methodPattern, new Proceed() {
			public Object proceed(InstrumentationContextParameter context) {
				try {
					return adapterClass.newInstance().firePopartJoinPoints(context);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public String toString() {
				return adapterClass.getCanonicalName()+"(class.method:"+domainClass.getCanonicalName()+"."+methodPattern+")";
			}
		});
	}
	
	/**
	 * Use to declare a join point for every method of a class.
	 * The MetaClass for the class must be configured properly!
	 * @params domainClass  Only this class is instrumented.
	 * @params adapterClass This class is responsible for instrumenting the join point shadows in the domainClass.
	 * @see {@link InstrumentationRegistry#instrumentClass(Class,Closure)}
	 */
	public static void declareJoinPoint(final Class<?> domainClass, final Class<? extends JoinPointInstrumentation> adapterClass) {
		InstrumentationRegistry.instrumentClass(domainClass, new Proceed() {
			public Object proceed(InstrumentationContextParameter context) {
				try {
					return adapterClass.newInstance().firePopartJoinPoints(context);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			public String toString() {
				return adapterClass.getCanonicalName()+"(class:"+domainClass.getCanonicalName()+")";
			}
		});
	}
	
	/**
	 * Use to declare a join point for every method of every class.
	 * Best for join point types, such as method call.
	 * The MetaClass for the class must be configured properly!
	 * There are exception of classes that are not instrumented, these are defined in InstrumentationMetaClassCreationHandle. 
	 * @params adapterClass This class is responsible for instrumenting all join point shadows.
	 * @see {@link InstrumentationRegistry#replaceMetaClassCreationHandle()}
	 */
	public static void declareJoinPoint(final Class<? extends JoinPointInstrumentation> adapterClass) {
		InstrumentationRegistry.instrumentGlobally(new Proceed() {
			public Object proceed(InstrumentationContextParameter context) {
				try {
					return adapterClass.newInstance().firePopartJoinPoints(context);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			public String toString() {
				return adapterClass.getCanonicalName()+"(globally)";
			}
		});
	}
}