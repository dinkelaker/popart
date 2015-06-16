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

import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaClassRegistry.MetaClassCreationHandle;

/**
 * This custom {@link MetaClassCreationHandle} creates default
 * MetaClass instances decorated with instrumentation behaviour.
 * It also offers a couple of static convenience methods to decorate
 * specific or default meta classes with instrumentation functionality
 * @author Joscha Drechsler
 */
public class InstrumentationMetaClassCreationHandle extends MetaClassCreationHandle {
	/**
	 * This overrides createNormalMetaClass of the default MetaClassCreationHandle
	 * to create a default metaclass decorated by an InstrumentationMetaClass instead.
	 * Any further overrides must ensure, that the outermost metaclass is an
	 * InstrumentationMetaClass.
	 * @param theClass the class
	 * @param registry the registry
	 * @return the decorated meta class
	 */
	@Override
	public MetaClass createNormalMetaClass(@SuppressWarnings("unchecked")Class theClass, MetaClassRegistry registry) {
		return decorateMetaClassForInstrumentation(createOriginalMetaClass(theClass, registry));
	}

	/**
	 * This method builds the default meta class for the given class.
	 * @param theClass the class
	 * @param registry the registry
	 * @return the default meta class
	 */
	public MetaClass createOriginalMetaClass(@SuppressWarnings("unchecked") Class theClass, MetaClassRegistry registry) {
		return super.createNormalMetaClass(theClass, registry);
	}
	
	/**
	 * This method decorates the given metaclass with instrumentation functionality.
	 * It will ensure, that - as long as the meta classes were not tampered with manually -
	 * the returned metaclass will only have one single layer of instrumentation.
	 * @param metaClass the metaclass to decorate
	 * @return the decorated metaclass
	 */
	public static MetaClass decorateMetaClassForInstrumentation(MetaClass metaClass){
		if (isMetaClassDecoratedForInstrumentation(metaClass)){
			return metaClass;
		}else{
			return new InstrumentationMetaClass(metaClass);
		}
	}
	
	/**
	 * This method determines, if the given metaclass is decorated with instrumentation
	 * fucntionality. This method may return invalid results if the metaclass was tampered
	 * with manually.
	 * @param metaClass the metaClass
	 * @return <code>true</code> if the metaClass is decorated with instrumentation
	 * 		functionality, <code>false</code> otherwise.
	 */
	public static boolean isMetaClassDecoratedForInstrumentation(MetaClass metaClass){
		return metaClass instanceof InstrumentationMetaClass;
	}
	
	/**
	 * This method undecorates a given {@link InstrumentationMetaClass}, thereby
	 * removing the instrumentation functionality.
	 * @param mc the decorated metaclass
	 * @return the undecorated metaclass
	 */
	public static MetaClass removeInstrumentationDecorationFromMetaClass(MetaClass metaClass){
		if (isMetaClassDecoratedForInstrumentation(metaClass)){
			return ((InstrumentationMetaClass)metaClass).getAdaptee();
		} else {
			return metaClass;
		}
	}
	
	/**
	 * "shortcut" to {@link GroovySystem#getMetaClassRegistry()}
	 */
	private static MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();

	/**
	 * This method decorates the given classes metaClass with an
	 * InstrumentationMetaClass, so invocations of the classes methods
	 * will invoke the instrumented closures.<br>
	 * Duplicate decorations are prevented, as long as the directly
	 * associated MetaClass of the given class is an
	 * {@link InstrumentationMetaClass} instance, as required its contract.<br>
	 * Replacing the MetaClass afterwards will undo this functionallity!<br>
	 * @param theClass the class
	 * @return <code>true</code> if the previously associated MetaClass was
	 * 		decorated successfully, <code>false</code> if it had already
	 * 		been decorated before.
	 */
	public static boolean decorateDefaultMetaClassForInstrumentation(Class<?> theClass) {
		synchronized(registry){
			MetaClass oldMC = registry.getMetaClass(theClass);
			if (oldMC instanceof InstrumentationMetaClass) return false;
			MetaClass imc = decorateMetaClassForInstrumentation(oldMC);
			registry.setMetaClass(theClass, imc);
			return true;
		}
	}

	/**
	 * This reverses the effects done by {@link #decorateMetaClassForInstrumentation(Class)}.
	 * @param theClass the class
	 * @return <code>true</code> if the associated MetaClass was
	 * 		undecorated successfully, <code>false</code> if the directly
	 * 		associated MetaClass was not an {@link InstrumentationMetaClass}
	 * 		instance and thus could not be undecorated.
	 */
	public static boolean removeInstrumentationDecorationFromDefaultMetaClass(Class<?> theClass){
		synchronized(registry){
			MetaClass mc = registry.getMetaClass(theClass);
			if (!(mc instanceof InstrumentationMetaClass)) return false;
			registry.setMetaClass(theClass, removeInstrumentationDecorationFromMetaClass((InstrumentationMetaClass) mc));
			return true;
		}
	}

	/**
	 * This will replace the current {@link MetaClassRegistry}s
	 * {@link MetaClassCreationHandle} with an
	 * {@link InstrumentationMetaClassCreationHandle}, so all newly
	 * instantiated MetaClasses will add the instrumentation functionality
	 * to their Objects.
	 * @return the previously set {@link MetaClassCreationHandle}
	 */
	public static MetaClassCreationHandle replaceMetaClassCreationHandleForInstrumentation(){
		return replaceMetaClassCreationHandle(new InstrumentationMetaClassCreationHandle());
	}
	
	/**
	 * This will replace the current {@link MetaClassRegistry}s
	 * {@link MetaClassCreationHandle} with the given one.
	 * @param newHandle the new handle
	 * @return the previously set handle
	 */
	public static MetaClassCreationHandle replaceMetaClassCreationHandle(MetaClassCreationHandle newHandle){
		synchronized(registry){
			MetaClassCreationHandle oldHandle = registry.getMetaClassCreationHandler();
			registry.setMetaClassCreationHandle(newHandle);
			return oldHandle;
		}
	}
	
	/**
	 * Determines, whether the currently set {@link MetaClassCreationHandle}
	 * decorates all new meta classes with instrumentation functionality.
	 * @return <code>true</code> if a proper creation handle is set,
	 * 		<code>false</code> for all other creation handles
	 */
	public static boolean isInstrumentationMetaClassCreationHandleInstalled(){
		return registry.getMetaClassCreationHandler() instanceof InstrumentationMetaClassCreationHandle;
	}
}