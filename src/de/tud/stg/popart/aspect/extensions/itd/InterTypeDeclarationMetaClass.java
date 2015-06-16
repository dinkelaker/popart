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

import groovy.lang.Closure;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MetaObjectProtocol;
import groovy.lang.MetaProperty;

import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClass;
import de.tud.stg.popart.aspect.extensions.itd.conflicts.WritingMergedMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.locations.ClassMethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ClassPropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectMethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectPropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;

/**
 * This class extends the default {@link InstrumentationMetaClass}.
 * It overrides getProperty, setProperty and invokeMethod to look up
 * the InterTypeDeclaration system for introduced Methods and Properties.
 * This MetaClass is REQUIRED! to be decorated by an
 * {@link InstrumentationMetaClass} instance, which must be the directly
 * registered meta class for objects, in order for the management
 * functions of {@link InterTypeDeclarationFacade} to work.
 * This class is stateless. It can, at any point in time, be replaced
 * by another instance with an equal delegate.
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationMetaClass extends DelegatingMetaClass {
	/**
	 * Forwarded Constructor. Package visibility - DO NOT USE!
	 * use {@link InterTypeDeclarationMetaClassCreationHandle}
	 * @see {@link DelegatingMetaClass#DelegatingMetaClass(MetaClass)}
	 */
	InterTypeDeclarationMetaClass(MetaClass delegate){
		super(delegate);
	}

	/**
	 * Overrides respondsTo, to add matching MetaMethods to the returned
	 * List.
	 * @see {@link MetaObjectProtocol#respondsTo(Object, String, Object[])}
	 */
	@Override
	public List<MetaMethod> respondsTo(final Object object, final String methodName, final Object[] args){
		MethodLocation location = new ObjectMethodLocation(object, methodName, args);
		List<MetaMethod> result = new java.util.ArrayList<MetaMethod>(super.respondsTo(object, methodName, args));
		InterTypeDeclarationAspectManager aspectManager = (InterTypeDeclarationAspectManager) AspectManager.getInstance();
		result.addAll(aspectManager.performMethodLookup(location));
		return result;
	}

	/**
	 * Overrides respondsTo, to add matching MetaMethods to the returned
	 * List.
	 * @see {@link MetaObjectProtocol#respondsTo(Object, String)}
	 */
	@Override
	public List<MetaMethod> respondsTo(final Object object, final String methodName) {
		MethodLocation location = new ObjectMethodLocation(object, methodName);
		List<MetaMethod> result = new java.util.ArrayList<MetaMethod>(super.respondsTo(object, methodName));
		InterTypeDeclarationAspectManager aspectManager = (InterTypeDeclarationAspectManager) AspectManager.getInstance();
		result.addAll(aspectManager.performMethodLookup(location));
		return result;
	}

	/**
	 * Overrides getMethods, to add matching MetaMethods to the returned
	 * List.
	 * @see {@link MetaObjectProtocol#getMethods()}
	 */
	@Override
	public List<MetaMethod> getMethods() {
		MethodLocation location = new ClassMethodLocation(getTheClass());
		List<MetaMethod> result = new java.util.ArrayList<MetaMethod>(super.getMethods());
		InterTypeDeclarationAspectManager aspectManager = (InterTypeDeclarationAspectManager) AspectManager.getInstance();
		result.addAll(aspectManager.performMethodLookup(location));
		return result;
	}

	/**
	 * Overrides invokeMethod, to enable invocation of introduced
	 * Methods and Closure properties.
	 * @see {@link MetaObjectProtocol#invokeMethod(Object, String, Object[])}
	 */
	@Override
	public Object invokeMethod(Object object, String methodName, Object[] arguments) {
		MetaMethod metaMethod = getMetaMethod(new ObjectMethodLocation(object, methodName, arguments));
		if(metaMethod==null){
			//enable calling of introduced fields holding closures
			MetaProperty possibleClosureProperty = hasProperty(object, methodName);
			if(possibleClosureProperty != null){
				Object possibleClosure = possibleClosureProperty.getProperty(object);
				if(possibleClosure instanceof Closure){
					Closure c = (Closure)possibleClosure;
					if(c.getMetaClass().respondsTo(c, "doCall", arguments).size()>0){
						return c.call(arguments);
					}
				}
			}
			/*
			 * if the meta method was not found, this super call will
			 * either produce a MissingMethodException, or it will
			 * invoke some unpredictable method via the groovy default
			 * implementation
			 */
			return super.invokeMethod(object, methodName, arguments);
		}else{
			/*
			 * TODO The method invocation should be done via some
			 * invokeIntroducedMethod(..) in the aspect (meta-)manager.
			 */
			return metaMethod.doMethodInvoke(object, arguments);
		}
	}

	/**
	 * Overrides getMetaMethod, to include introduced Methods in the search
	 * @see {@link MetaObjectProtocol#getMetaMethod(String, Object[])}
	 */
	@Override
	public MetaMethod getMetaMethod(final String name, final Object[] args) {
		return getMetaMethod(new ClassMethodLocation(getTheClass(), name, args));
	}

	/**
	 * @see {@link MetaClass#pickMethod(String, Class[])}
	 */
	@Override
	public MetaMethod pickMethod(String name, @SuppressWarnings("unchecked")Class[] args) {
		return getMetaMethod(new ClassMethodLocation(getTheClass(), name, args));
	}

	/**
	 * tries to find a single method definition applicable for the accessed
	 * location, by looking up applicable definitions in the base code and
	 * searching inter-type declarations of currently registered aspects.
	 * @param location the accessed location
	 * @return a single matching meta method
	 */
	private MetaMethod getMetaMethod(MethodLocation location) {
		if(InterTypeDeclarationCache.hasEntry(location)){
			return InterTypeDeclarationCache.getEntry(location);
		}else{
			MetaMethod original = super.pickMethod(location.getMethodName(), location.getArgumentClasses());
			InterTypeDeclarationAspectManager aspectManager = (InterTypeDeclarationAspectManager) AspectManager.getInstance();
			Set<InterTypeDeclarationMetaMethod> candidatePool = aspectManager.performMethodLookup(location);

			MetaMethod result;
			switch(candidatePool.size()){
			case 0:
				//Case no introductions: use base code method.
				result = original;
				break;
			case 1:
				//case one introduction:
				if(original==null){
					//if no base code method: use introduction
					result = candidatePool.iterator().next();
					break;
				}
				//fall through intended!
			default:
				//else: resolve conlfict
				result = aspectManager.resolveConflict(location, original, candidatePool);
				break;
			}

			InterTypeDeclarationCache.storeEntry(location, result);
			return result;
		}
	}

	/**
	 * Overrides the default hasProperty implementation to look up
	 * possibly existing introductions.
	 * @see {@link MetaObjectProtocol#hasProperty(Object, String)}
	 */
	@Override
	public MetaProperty hasProperty(final Object obj, final String name) {
		return getMetaProperty(new ObjectPropertyLocation(obj, name));
	}

	/**
	 * Overrides the default getProperty implementation to look up
	 * possibly existing introductions.
	 * @see {@link MetaObjectProtocol#getProperty(Object, String)}
	 */
	@Override
	public Object getProperty(Object object, String property) {
		MetaProperty metaProperty = getMetaProperty(new ObjectPropertyLocation(object, property));
		if(metaProperty == null){
			return super.getProperty(object,property);
		}else{
			/*
			 * Workaround for issue http://jira.codehaus.org/browse/GROOVY-4098
			 * remove these lines once the issue is resolved.
			 * This if statement must include all meta property types
			 * that originate from inter-type declarations!
			 */
			if(
					!(metaProperty instanceof InterTypeDeclarationMetaProperty) &&
					!(metaProperty instanceof WritingMergedMetaProperty)
			){
				return super.getProperty(object,property);
			}
			// End of workaround
			/*
			 * TODO The property access should be done via some
			 * invokeIntroducedMethod(..) in the aspect (meta-)manager.
			 */
			return metaProperty.getProperty(object);
		}
	}

	/**
	 * Overrides the default setProperty implementation to look up
	 * possibly existing introductions.
	 * @see {@link MetaObjectProtocol#setProperty(Object, String, Object)}
	 */
	@Override
	public void setProperty(Object object, String property, Object newValue) {
		MetaProperty metaProperty = getMetaProperty(new ObjectPropertyLocation(object, property, newValue));
		if(metaProperty == null){
			super.setProperty(object,property,newValue);
		}else{
			/*
			 * Workaround for issue http://jira.codehaus.org/browse/GROOVY-4098
			 * remove these lines once the issue is resolved.
			 * This if statement must include all meta property types
			 * that originate from inter-type declarations!
			 */
			if(
					!(metaProperty instanceof InterTypeDeclarationMetaProperty) &&
					!(metaProperty instanceof WritingMergedMetaProperty)
			){
				super.setProperty(object,property,newValue);
				return;
			}
			// End of workaround
			/*
			 * TODO The property access should be done via some
			 * invokeIntroducedMethod(..) in the aspect (meta-)manager.
			 */
			metaProperty.setProperty(object, newValue);
		}
	}

	/**
	 * Overrides the default getProperties implementation to include
	 * existing introductions into the returned list.
	 * @see {@link MetaObjectProtocol#getProperties()}
	 */
	@Override
	public List<MetaProperty> getProperties() {
		PropertyLocation location = new ClassPropertyLocation(getTheClass());
		List<MetaProperty> result = new java.util.LinkedList<MetaProperty>(super.getProperties());
		InterTypeDeclarationAspectManager aspectManager = (InterTypeDeclarationAspectManager) AspectManager.getInstance();
		result.addAll(aspectManager.performPropertyLookup(location));
		return result;
	}

	/**
	 * Overrides the default getMetaMethod implementation to include
	 * introductions in the search.
	 * @see {@link MetaObjectProtocol#getMetaProperty(String)}
	 */
	@Override
	public MetaProperty getMetaProperty(String name) {
		return getMetaProperty(new ClassPropertyLocation(getTheClass(), name));
	}


	/**
	 * looks up the property definitions matching the accessed location in
	 * the base code and searches the inter-type declarations of all currently
	 * active aspects for matching definitions.
	 * @param location the accessed location
	 * @return a single property applicable for the accessed location
	 */
	public MetaProperty getMetaProperty(PropertyLocation location){
		if(InterTypeDeclarationCache.hasEntry(location)){
			return InterTypeDeclarationCache.getEntry(location);
		}else{
			MetaProperty original = super.getMetaProperty(location.getPropertyName());
			InterTypeDeclarationAspectManager aspectManager = (InterTypeDeclarationAspectManager) AspectManager.getInstance();
			Set<InterTypeDeclarationMetaProperty> candidatePool = aspectManager.performPropertyLookup(location);

			MetaProperty result;
			switch(candidatePool.size()){
			case 0:
				result = original;
				break;
			case 1:
				if(original==null){
					result = candidatePool.iterator().next();
					break;
				}
				//fall through intended!
			default:
				result = aspectManager.resolveConflict(location, original, candidatePool);
				break;
			}

			InterTypeDeclarationCache.storeEntry(location, result);
			return result;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof InstrumentationMetaClass) && ((InstrumentationMetaClass)obj).getAdaptee().equals(getAdaptee());
	}
}
