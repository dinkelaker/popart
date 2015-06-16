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

import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaClassRegistry.MetaClassCreationHandle;

/**
 * This custom {@link MetaClassCreationHandle} creates default
 * MetaClass instances decorated with InterTypeDeclaration behaviour.
 * It also offers a couple of static convenience methods to decorate
 * specific or default meta classes with inter-type declaration
 * functionality
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationMetaClassCreationHandle extends MetaClassCreationHandle {
	/**
	 * This overrides createNormalMetaClass of the default MetaClassCreationHandle
	 * to create a default metaclass decorated by an InterTypeDeclarationMetaClass instead.
	 * Any further overrides must ensure, that the outermost metaclass is an
	 * InterTypeDeclarationMetaClass.
	 * @param theClass the class
	 * @param registry the registry
	 * @return the decorated meta class
	 */
	@Override
	public MetaClass createNormalMetaClass(@SuppressWarnings("unchecked")Class theClass, MetaClassRegistry registry) {
		return decorateMetaClassForInterTypeDeclarations(createOriginalMetaClass(theClass, registry));
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
	 * This method decorates the given metaclass with InterTypeDeclaration functionality.
	 * It will ensure, that - as long as the meta classes were not tampered with manually -
	 * the returned metaclass will only have one single layer of InterTypeDeclaration.
	 * @param metaClass the metaclass to decorate
	 * @return the decorated metaclass
	 */
	public static MetaClass decorateMetaClassForInterTypeDeclarations(MetaClass metaClass){
		if (isMetaClassDecoratedForInterTypeDeclaration(metaClass)){
			return metaClass;
		}else{
			return new InterTypeDeclarationMetaClass(metaClass);
		}
	}
	
	/**
	 * This method determines, if the given metaclass is decorated with InterTypeDeclaration
	 * fucntionality. This method may return invalid results if the metaclass was tampered
	 * with manually.
	 * @param metaClass the metaClass
	 * @return <code>true</code> if the metaClass is decorated with InterTypeDeclaration
	 * 		functionality, <code>false</code> otherwise.
	 */
	public static boolean isMetaClassDecoratedForInterTypeDeclaration(MetaClass metaClass){
		return metaClass instanceof InterTypeDeclarationMetaClass;
	}
	
	/**
	 * This method undecorates a given {@link InterTypeDeclarationMetaClass}, thereby
	 * removing the InterTypeDeclaration functionality.
	 * @param mc the decorated metaclass
	 * @return the undecorated metaclass
	 */
	public static MetaClass removeInterTypeDeclarationDecorationFromMetaClass(MetaClass metaClass){
		if(isMetaClassDecoratedForInterTypeDeclaration(metaClass)){
			return ((InterTypeDeclarationMetaClass)metaClass).getAdaptee();
		}else{
			return metaClass;
		}
	}
	
	/**
	 * "shortcut" to {@link GroovySystem#getMetaClassRegistry()}
	 */
	private static MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();

	/**
	 * This method decorates the given classes metaClass with an
	 * InterTypeDeclarationMetaClass, so invocations of the classes methods
	 * will invoke the instrumented closures.<br>
	 * Duplicate decorations are prevented, as long as the directly
	 * associated MetaClass of the given class is an
	 * {@link InterTypeDeclarationMetaClass} instance, as required its contract.<br>
	 * Replacing the MetaClass afterwards will undo this functionallity!<br>
	 * @param theClass the class
	 * @return <code>true</code> if the previously associated MetaClass was
	 * 		decorated successfully, <code>false</code> if it had already
	 * 		been decorated before.
	 */
	public static boolean decorateDefaultMetaClassFor(Class<?> theClass) {
		synchronized(registry){
			MetaClass oldMC = registry.getMetaClass(theClass);
			if (oldMC instanceof InterTypeDeclarationMetaClass) return false;
			MetaClass imc = decorateMetaClassForInterTypeDeclarations(oldMC);
			registry.setMetaClass(theClass, imc);
			return true;
		}
	}

	/**
	 * This reverses the effects done by {@link #decorateMetaClassFor(Class)}.
	 * @param theClass the class
	 * @return <code>true</code> if the associated MetaClass was
	 * 		undecorated successfully, <code>false</code> if the directly
	 * 		associated MetaClass was not an {@link InterTypeDeclarationMetaClass}
	 * 		instance and thus could not be undecorated.
	 */
	public static boolean undecorateDefaultMetaClassFor(Class<?> theClass){
		synchronized(registry){
			MetaClass mc = registry.getMetaClass(theClass);
			if (!(mc instanceof InterTypeDeclarationMetaClass)) return false;
			registry.setMetaClass(theClass, removeInterTypeDeclarationDecorationFromMetaClass((InterTypeDeclarationMetaClass) mc));
			return true;
		}
	}

	/**
	 * This will replace the current {@link MetaClassRegistry}s
	 * {@link MetaClassCreationHandle} with an
	 * {@link InterTypeDeclarationMetaClassCreationHandle}, so all newly
	 * instantiated MetaClasses will add the InterTypeDeclaration functionality
	 * to their Objects.
	 * @return the previously set {@link MetaClassCreationHandle}
	 */
	public static MetaClassCreationHandle replaceMetaClassCreationHandleForInterTypeDeclarations(){
		return replaceMetaClassCreationHandle(new InterTypeDeclarationMetaClassCreationHandle());
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
	 * decorates all new meta classes with InterTypeDeclaration functionality.
	 * @return <code>true</code> if a proper creation handle is set,
	 * 		<code>false</code> for all other creation handles
	 */
	public static boolean isInterTypeDeclarationMetaClassCreationHandleInstalled(){
		return registry.getMetaClassCreationHandler() instanceof InterTypeDeclarationMetaClassCreationHandle;
	}
}
