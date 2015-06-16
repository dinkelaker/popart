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

import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClass;
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClassCreationHandle;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaClassRegistry.MetaClassCreationHandle;

/**
 * This custom {@link MetaClassCreationHandle} creates default
 * MetaClass instances decorated with instrumentation behaviour.
 * It also offers a couple of static convenience methods to decorate
 * specific or default meta classes with instrumentation and inter-type
 * declaration functionality
 * @author Joscha Drechsler
 */
public class InstrumentationInterTypeDeclarationMetaClassCreationHandle extends InstrumentationMetaClassCreationHandle {
	/**
	 * This overrides createNormalMetaClass of the InstrumentationMetaClassCreationHandle
	 * to create a default metaclass decorated by an InterTypeDeclarationMetaClass,
	 * decorated by InstrumentationMetaClass instead.
	 * Any further overrides must ensure, that the outermost metaclass is an
	 * InstrumentationMetaClass, and the second outermost metaclass is an
	 * InterTypeDeclarationMetaClass
	 * @param theClass the class
	 * @param registry the registry
	 * @return the decorated meta class
	 */
	@Override
	public MetaClass createNormalMetaClass(@SuppressWarnings("unchecked")Class theClass, MetaClassRegistry registry) {
		return decorateMetaClassForInstrumentationAndInterTypeDeclarations(createOriginalMetaClass(theClass, registry));
	}
	
	/**
	 * This method decorates the given metaclass with instrumentation and inter-type
	 * declaration functionality. It will ensure, that - as long as the meta classes
	 * were not tampered with manually - the returned metaclass will only have one
	 * single layer of instrumentation and inter-type declaration decoration.
	 * @param metaClass the metaclass to decorate
	 * @return the decorated metaclass
	 */
	public static MetaClass decorateMetaClassForInstrumentationAndInterTypeDeclarations(MetaClass metaClass){
		if (isMetaClassDecoratedForInstrumentationAndInterTypeDeclarations(metaClass)){
			return metaClass;
		}else if(isMetaClassDecoratedForInstrumentation(metaClass)){
			return decorateMetaClassForInstrumentationAndInterTypeDeclarations(removeInstrumentationDecorationFromMetaClass(metaClass));
		}else if(InterTypeDeclarationMetaClassCreationHandle.isMetaClassDecoratedForInterTypeDeclaration(metaClass)){
			return decorateMetaClassForInstrumentation(metaClass);
		}else{
			return decorateMetaClassForInstrumentation(InterTypeDeclarationMetaClassCreationHandle.decorateMetaClassForInterTypeDeclarations(metaClass));
		}
	}
	
	/**
	 * This method determines, if the given metaclass is decorated with instrumentation
	 * and inter-type declaration functionality. This method may return invalid results
	 * if the metaclass was tampered with manually.
	 * @param metaClass the metaClass
	 * @return <code>true</code> if the metaClass is decorated with instrumentation and
	 * 		inter-type declaration functionality, <code>false</code> otherwise.
	 */
	public static boolean isMetaClassDecoratedForInstrumentationAndInterTypeDeclarations(MetaClass metaClass){
		return isMetaClassDecoratedForInstrumentation(metaClass) && isMetaClassDecoratedForInterTypeDeclarations(metaClass);
	}
	
	/**
	 * This method undecorates a given metaclass, thereby removing possible instrumentation
	 * and inter-type declaration functionality. Note, that this method will only remove
	 * both decorations at once! If the given metaclass has only one of these decorations,
	 * it will not be modified.
	 * @param mc the decorated metaclass
	 * @return the undecorated metaclass
	 */
	public static MetaClass removeInstrumentationAndInterTypeDeclarationDecorationFromMetaClass(MetaClass metaClass){
		if(isMetaClassDecoratedForInstrumentationAndInterTypeDeclarations(metaClass)){
			return removeInstrumentationDecorationFromMetaClass(InterTypeDeclarationMetaClassCreationHandle.removeInterTypeDeclarationDecorationFromMetaClass(metaClass));
		}else{
			return metaClass;
		}
	}
	
	/**
	 * "shortcut" to {@link GroovySystem#getMetaClassRegistry()}
	 */
	private static MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();

	/**
	 * This method decorates the given classes default metaClass with
	 * instrumentation and inter-type declaration functionality.
	 * @param theClass the class
	 * @return <code>true</code> if the previously associated MetaClass was
	 * 		decorated successfully, <code>false</code> if it had already
	 * 		been decorated before.
	 * @see #decorateMetaClassForInstrumentationAndInterTypeDeclarations(MetaClass)
	 */
	public static boolean decorateDefaultMetaClassForInstrumentationAndInterTypeDeclarations(Class<?> theClass) {
		synchronized(registry){
			MetaClass oldMC = registry.getMetaClass(theClass);
			if (isMetaClassDecoratedForInstrumentationAndInterTypeDeclarations(oldMC)) return false;
			MetaClass imc = decorateMetaClassForInstrumentationAndInterTypeDeclarations(oldMC);
			registry.setMetaClass(theClass, imc);
			return true;
		}
	}

	/**
	 * This reverses the effects done by {@link #decorateMetaClassFor(Class)}.
	 * @param theClass the class
	 * @return <code>true</code> if the associated MetaClass was
	 * 		undecorated successfully, <code>false</code> if the directly
	 * 		associated MetaClass was not an {@link InstrumentationMetaClass}
	 * 		instance and thus could not be undecorated.
	 * @see #removeInstrumentationAndInterTypeDeclarationDecorationFromMetaClass(MetaClass)
	 */
	public static boolean removeInstrumentationAndInterTypeDeclarationDecorationDecorationFromDefaultMetaClass(Class<?> theClass){
		synchronized(registry){
			MetaClass mc = registry.getMetaClass(theClass);
			if (!(mc instanceof InstrumentationMetaClass)) return false;
			registry.setMetaClass(theClass, removeInstrumentationAndInterTypeDeclarationDecorationFromMetaClass((InstrumentationMetaClass) mc));
			return true;
		}
	}

	/**
	 * This will replace the current {@link MetaClassRegistry}s
	 * {@link MetaClassCreationHandle} with an instance of this class, so all newly
	 * instantiated MetaClasses will add the instrumentation functionality
	 * to their Objects.
	 * @return the previously set {@link MetaClassCreationHandle}
	 */
	public static MetaClassCreationHandle replaceMetaClassCreationHandleForInstrumentationAndInterTypeDeclarations(){
		return replaceMetaClassCreationHandle(new InstrumentationInterTypeDeclarationMetaClassCreationHandle());
	}
	
	/**
	 * Determines, whether the currently set {@link MetaClassCreationHandle}
	 * decorates all new meta classes with instrumentation and inter-type
	 * declaration functionality.
	 * @return <code>true</code> if a proper creation handle is set,
	 * 		<code>false</code> for all other creation handles
	 */
	public static boolean isInstrumentationInterTypeDeclarationMetaClassCreationHandleInstalled(){
		return registry.getMetaClassCreationHandler() instanceof InstrumentationInterTypeDeclarationMetaClassCreationHandle;
	}
	
	/**
	 * This method is a redefinition of
	 * {@link InterTypeDeclarationMetaClassCreationHandle#decorateMetaClassForInterTypeDeclarations(MetaClass)}
	 * which accounts for the possibility of inter-type declaration decorations surrounded
	 * with additional instrumentation decorations.
	 * @param metaClass the metaClass
	 * @return the decorated metaClass
	 */
	public static MetaClass decorateMetaClassForInterTypeDeclarations(MetaClass metaClass){
		if(isMetaClassDecoratedForInstrumentationAndInterTypeDeclarations(metaClass)){
			return metaClass;
		}else if(isMetaClassDecoratedForInstrumentation(metaClass)){
			return decorateMetaClassForInstrumentationAndInterTypeDeclarations(metaClass);
		}else{
			return InterTypeDeclarationMetaClassCreationHandle.decorateMetaClassForInterTypeDeclarations(metaClass);
		}
	}
	
	/**
	 * This method is a redefinition of
	 * {@link InterTypeDeclarationMetaClassCreationHandle#removeInterTypeDeclarationDecorationFromMetaClass(MetaClass)}
	 * which accounts for the possibility of inter-type declaration decorations surrounded
	 * with additional instrumentation decorations.
	 * @param metaClass the decorated metaClass
	 * @return the undecorated metaClass
	 */
	public static MetaClass removeInterTypeDeclarationDecorationFromMetaClass(MetaClass metaClass){
		if(isMetaClassDecoratedForInstrumentationAndInterTypeDeclarations(metaClass)){
			return decorateMetaClassForInstrumentation(InterTypeDeclarationMetaClassCreationHandle.removeInterTypeDeclarationDecorationFromMetaClass(removeInstrumentationDecorationFromMetaClass(metaClass)));	
		}else{
			return InterTypeDeclarationMetaClassCreationHandle.removeInterTypeDeclarationDecorationFromMetaClass(metaClass);
		}
	}
	
	/**
	 * This method is a redefinition of
	 * {@link InterTypeDeclarationMetaClassCreationHandle#isMetaClassDecoratedForInterTypeDeclaration(MetaClass)}
	 * which accounts for the possibility of inter-type declaration decorations surrounded
	 * with additional instrumentation decorations.
	 * @param metaClass the decorated metaClass
	 * @return <code>true</code> if the meta classes provides inter-type
	 * 		declaration functionality, <code>false</code> otherwise.
	 */
	public static boolean isMetaClassDecoratedForInterTypeDeclarations(MetaClass metaClass){
		if(isMetaClassDecoratedForInstrumentation(metaClass)){
			return InterTypeDeclarationMetaClassCreationHandle.isMetaClassDecoratedForInterTypeDeclaration(removeInstrumentationDecorationFromMetaClass(metaClass));
		}else{
			return InterTypeDeclarationMetaClassCreationHandle.isMetaClassDecoratedForInterTypeDeclaration(metaClass);
		}
	}

	/**
	 * This method is a redefinition of
	 * {@link InterTypeDeclarationMetaClassCreationHandle#isInterTypeDeclarationMetaClassCreationHandleInstalled()}
	 * which accounts for the possibility of inter-type declaration decorations surrounded
	 * with additional instrumentation decorations.
	 * @return <code>true</code> if newly created meta classes will provide
	 * 		inter-type declaration functionality, <code>false</code> otherwise.
	 */
	public static boolean isInterTypeDeclarationMetaClassCreationHandleInstalled(){
		return isInstrumentationInterTypeDeclarationMetaClassCreationHandleInstalled() || InterTypeDeclarationMetaClassCreationHandle.isInterTypeDeclarationMetaClassCreationHandleInstalled();
	}
}