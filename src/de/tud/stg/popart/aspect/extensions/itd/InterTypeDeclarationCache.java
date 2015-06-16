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

import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;

import java.util.Map;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectChangeListener;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.itd.locations.ClassMethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ClassPropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;

/**
 * This class is a monostate cache for inter-type declaration lookup results.
 * @author Joscha Drechsler
 */
public abstract class InterTypeDeclarationCache implements AspectChangeListener{
	/**
	 * a switch to enable or disable the cache.
	 */
	private static boolean enabled = true;
	
	/**
	 * a switch to enable or disable the cache.
	 * default is true.
	 * @param enabled the new state.
	 */
	public static void setEnabled(boolean enabled) {
		reset();
		InterTypeDeclarationCache.enabled = enabled;
	}
	
	/**
	 * this map holds the results for single meta method lookups.
	 * Also used for synchronization of method cache operations.
	 */
	private static Map<ClassMethodLocation, MetaMethod> methodCache;
	/**
	 * this map holds the results for single meta property lookups.
	 * Also used for synchronization of property cache operations.
	 */
	private static Map<ClassPropertyLocation, MetaProperty> propertyCache;

	/**
	 * this map holds the results for single meta method lookups
	 * for objects, which have results that are not purely dependent
	 * on their class. For example, if an aspect is deployed for a
	 * single object only, this object will behave different from
	 * other objects of its class.
	 */
	private static WeakIdentityHashMap<Object, Map<ClassMethodLocation,MetaMethod>> exceptionObjectsMethodCache;
	/**
	 * this map holds the results for single meta property lookups
	 * for objects, which have results that are not purely dependent
	 * on their class. For example, if an aspect is deployed for a
	 * single object only, this object will behave different from
	 * other objects of its class.
	 */
	private static WeakIdentityHashMap<Object, Map<ClassPropertyLocation,MetaProperty>> exceptionObjectsPropertyCache;
	
	static {
		methodCache = new java.util.HashMap<ClassMethodLocation,MetaMethod>();
		propertyCache = new java.util.HashMap<ClassPropertyLocation,MetaProperty>();
		exceptionObjectsMethodCache = new WeakIdentityHashMap<Object, Map<ClassMethodLocation,MetaMethod>>();
		exceptionObjectsPropertyCache = new WeakIdentityHashMap<Object, Map<ClassPropertyLocation,MetaProperty>>();
		
		AspectManager aspectManager = AspectManager.getInstance();
		collectExceptionObjects(aspectManager);
		aspectManager.registerAspectChangeListener(new AspectChangeListener() {
			public void aspectsChanged() {
				if(!enabled) return;
				reset();
			}
		});
	}
	
	/**
	 * resets all cache fields.
	 */
	private static void reset(){
		synchronized (methodCache){
			synchronized (propertyCache) {
				methodCache.clear();
				exceptionObjectsMethodCache.clear();
				propertyCache.clear();
				exceptionObjectsPropertyCache.clear();
				collectExceptionObjects(AspectManager.getInstance());
			}
		}
	}
	
	/**
	 * this method collects all object, that possibly behave different
	 * from other objects of their class. For example, if an aspect is
	 * deployed for a single object only, this object will behave different
	 * from other objects of its class.<br>
	 * for every object that is found, an instance-specific cache map is
	 * created.
	 * @param aspectManager the aspect manager to search for aspects
	 * 		that modify single objects behavior
	 */
	private static void collectExceptionObjects(AspectManager aspectManager){
		for(Aspect aspect : aspectManager.getAspects()){
			if(aspect instanceof InterTypeDeclarationAspect){
				InterTypeDeclarationAspect itdAspect = (InterTypeDeclarationAspect) aspect;
				if(itdAspect.isDeployed()){
					Object perInstanceScope = itdAspect.getPerInstanceScope();
					if(perInstanceScope != null){
						exceptionObjectsMethodCache.put(perInstanceScope, new java.util.HashMap<ClassMethodLocation, MetaMethod>());
						exceptionObjectsPropertyCache.put(perInstanceScope, new java.util.HashMap<ClassPropertyLocation, MetaProperty>());
					}
				}
			}
		}
	}

	/**
	 * This method looks up, if a result has been stored for the given
	 * location.
	 * @param location the location
	 * @return <code>true</code> if a cached result is present, otherwise
	 * 		<code>false</code>
	 */
	public static boolean hasEntry(MethodLocation location){
		if(!enabled) return false;
		synchronized (methodCache) {
			if(location instanceof ObjectLocation){
				Object target = ((ObjectLocation) location).getObject();
				Map<ClassMethodLocation,MetaMethod> objectSpecific = exceptionObjectsMethodCache.get(target);
				if(objectSpecific != null) return objectSpecific.containsKey(location);
			}
			return methodCache.containsKey(location);
		}
	}
	
	/**
	 * This method retrieves a result from the cache for the given location.
	 * note that <code>null</code> may refer to either a missing entry,
	 * or <code>null</code> as the actual cached result. Use
	 * {@link #hasEntry(MethodLocation)} to differ these cases.
	 * @param location the location
	 * @return the result, may be <code>null</code>
	 */
	public static MetaMethod getEntry(MethodLocation location){
		if(!enabled) throw new RuntimeException("Cache is disabled! Do not use it.");
		synchronized (methodCache) {
			if(location instanceof ObjectLocation){
				Object target = ((ObjectLocation) location).getObject();
				Map<ClassMethodLocation,MetaMethod> objectSpecific = exceptionObjectsMethodCache.get(target);
				if(objectSpecific != null) return objectSpecific.get(location);
			}
			return methodCache.get(location);
		}
	}
	
	/**
	 * this method stores a single method lookup result in the cache.
	 * Generally, the result is stored for the whole class, except if there
	 * are any aspects registered within the system, that may affect the
	 * result for a single accessed object only. In this case, the result
	 * is stored specific to that object. Also note, that any result is
	 * stored solely based on the static information of the location: a copy
	 * of the location object with all references to concrete objects removed
	 * will be used as index parameter. Therefore, if your method selection
	 * process depends on concrete objects, rather than only the classes
	 * involved, you must NOT use this cache.
	 * @param location the location
	 * @param metho the result, may be null.
	 */
	public static void storeEntry(MethodLocation location, MetaMethod method){
		if(!enabled) return;
		synchronized (methodCache) {
			if(location instanceof ObjectLocation){
				Object target = ((ObjectLocation) location).getObject();
				Map<ClassMethodLocation,MetaMethod> objectSpecific = exceptionObjectsMethodCache.get(target);
				if(objectSpecific != null){
					objectSpecific.put(location.staticInformationOnly(), method);
					return;
				}
			}
			methodCache.put(location.staticInformationOnly(), method);
		}
	}

	/**
	 * this method looks up, if the cache holds a stored value for the
	 * given location.
	 * @param location the location
	 * @return <code>true</code> if a value is cached, <code>false</code>
	 * 		otherwise.
	 */
	public static boolean hasEntry(PropertyLocation location){
		if(!enabled) return false;
		synchronized (propertyCache) {
			if(location instanceof ObjectLocation){
				Object target = ((ObjectLocation) location).getObject();
				Map<ClassPropertyLocation,MetaProperty> objectSpecific = exceptionObjectsPropertyCache.get(target);
				if(objectSpecific != null) return objectSpecific.containsKey(location);
			}
			return propertyCache.containsKey(location);
		}
	}
	
	/**
	 * this method retrieves a stored result from the cache. Note, that
	 * <code>null</code> as a result may indicate both either a missing
	 * entry or <code>null</code> as the actual stored result. Use
	 * {@link #hasEntry(PropertyLocation)} to differ these cases.
	 * @param location the location
	 * @return the cached result, may be <code>null</code>.
	 */
	public static MetaProperty getEntry(PropertyLocation location){
		if(!enabled) throw new RuntimeException("Cache is disabled! Do not use it!");
		synchronized (propertyCache) {
			if(location instanceof ObjectLocation){
				Object target = ((ObjectLocation) location).getObject();
				Map<ClassPropertyLocation,MetaProperty> objectSpecific = exceptionObjectsPropertyCache.get(target);
				if(objectSpecific != null) return objectSpecific.get(location);
			}
			return propertyCache.get(location);
		}
	}
	
	/**
	 * this method stores a single property lookup result in the cache.
	 * Generally, the result is stored for the whole class, except if there
	 * are any aspects registered within the system, that may affect the
	 * result for a single accessed object only. In this case, the result
	 * is stored specific to that object. Also note, that any result is
	 * stored solely based on the static information of the location: a copy
	 * of the location object with all references to concrete objects removed
	 * will be used as index parameter. Therefore, if your method selection
	 * process depends on concrete objects, rather than only the classes
	 * involved, you must NOT use this cache.
	 * @param location the location
	 * @param metho the result, may be null.
	 */
	public static void storeEntry(PropertyLocation location, MetaProperty property){
		if(!enabled) return;
		synchronized (propertyCache) {
			if(location instanceof ObjectLocation){
				Object target = ((ObjectLocation) location).getObject();
				Map<ClassPropertyLocation,MetaProperty> objectSpecific = exceptionObjectsPropertyCache.get(target);
				if(objectSpecific != null){
					objectSpecific.put(location.staticInformationOnly(), property);
					return;
				}
			}
			propertyCache.put(location.staticInformationOnly(), property);
		}
	}
}
