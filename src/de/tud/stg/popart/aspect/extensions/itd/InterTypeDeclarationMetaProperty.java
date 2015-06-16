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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectPropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;

import groovy.lang.MetaProperty;
import groovy.lang.MissingPropertyException;

public class InterTypeDeclarationMetaProperty extends MetaProperty implements AspectMember {
	/**
	 * The {@link InterTypePropertyDeclaration} which introduces this property
	 */
	private InterTypePropertyDeclaration itpd;
	
	/**
	 * The introduced properties initial value
	 */
	private Object defaultValue;

	/**
	 * A Map indexing object instances to property values. This map must be
	 * a WeakIdentityHashMap for best effort in preventing memory
	 * leaks. A WeakIdentityHashMap associates values to the key object
	 * identities rather than their equalities. This way, the hash key
	 * for each object is guaranteed to be independent from possibly introduced
	 * values stored within the hashmap (which would cause the key to change,
	 * if the value associated with this map would change, horrible danger!).
	 * Also, if a classes equal method would depend on the introduced value,
	 * it would otherwise be impossible, to separate two equal instances of
	 * this class since both had the same key in this hashmap. Thus, the
	 * hashmap must index values by the key object identities.<br>
	 * See {@link ITDCCCombiner#introduce_field(StructureDesignator, String, Object)}
	 * for further information on memory leaks regarding run-time introductions.
	 */
	private Map<Object, Object> values = new WeakIdentityHashMap<Object, Object>();

	/**
	 * default constructor.
	 * @param itpd the declaring link
	 * @param type the properties type
	 * @param name the properties name
	 * @param defaultValue the default value
	 */
	public InterTypeDeclarationMetaProperty(InterTypePropertyDeclaration itpd, Class<?> type, String name, Object defaultValue) {
		super(name, type);
		if(!type.isInstance(defaultValue)) throw new ClassCastException("Default value is of incompatible type: "+defaultValue.getClass()+" cannot be cast to "+type);
		this.itpd = itpd;
		this.defaultValue = defaultValue;
	}

	/**
	 * Set the properties value if the introduction matches
	 * the given object.
	 * @param obj the object
	 * @param value the new value
	 * @throws MissingPropertyException if the introduction does not match
	 */
	public void setProperty(Object obj, Object value){
		if(!itpd.appliesTo(obj)) throw new MissingPropertyException("The introduced property "+getName()+" is not valid for Object "+obj+" (pattern was: "+itpd.getPattern()+")");
		if(!((Class<?>)getType()).isInstance(value)) throw new ClassCastException("New value is of incompatible type: "+value.getClass()+" cannot be cast to "+getType());

		/*
		 * TODO The aspect.beforeFieldSet and aspect.afterFieldSet calls
		 * should be done through the aspect (meta-)manager.
		 */
		InterTypeDeclarationAspect aspect = getInterTypePropertyDeclaration().getDeclaringAspect();
		ObjectPropertyLocation location = new ObjectPropertyLocation(obj, name, value);
		
		aspect.beforeFieldSet(location, this);
		synchronized(values){
			values.put(obj, value);
		}
		aspect.afterFieldSet(location, this);
	}
	
	/**
	 * Returns the properties current value if the introduction
	 * matches the given object.
	 * @param obj the object
	 * @throws MissingPropertyException if the introduction does not match
	 */
	public Object getProperty(Object obj){
		if(!itpd.appliesTo(obj)) throw new MissingPropertyException("The introduced property "+getName()+" is not valid for Object "+obj+" (pattern was: "+itpd.getPattern()+")");

		/*
		 * TODO The aspect.beforeFieldGet and aspect.afterFieldGet calls
		 * should be done through the aspect (meta-)manager.
		 */
		InterTypeDeclarationAspect aspect = getInterTypePropertyDeclaration().getDeclaringAspect();
		ObjectPropertyLocation location = new ObjectPropertyLocation(obj, name);

		aspect.beforeFieldGet(location, this);
		Object result;
		synchronized(values){
			if(values.containsKey(obj)){
				result = values.get(obj);
			} else {
				result = defaultValue;
			}
		}
		aspect.afterFieldGet(location, this, result);
		return result;
	}
	
	/**
	 * @see {@link Object#toString()}
	 */
	public String toString(){
		return "InterTypeDeclarationMetaProperty["+getName()+"="+defaultValue+", aspect:"+getAspect().getName()+", pattern:"+itpd.getPattern()+"]";
	}
	
	public Object getDefaultValue(){
		return defaultValue;
	}
	
	public InterTypePropertyDeclaration getInterTypePropertyDeclaration(){
		return itpd;
	}

	public void resetAllValues() {
		synchronized(values){
			values.clear();
		}
	}
	
	public void resetValues(Class<?> theClass){
		synchronized(values){
			Iterator<Object> keys = values.keySet().iterator();
			while(keys.hasNext()){
				if(theClass.isInstance(keys.next())){
					keys.remove();
				}
			}
		}
	}
	
	public void resetValue(Object object){
		synchronized(values){
			values.remove(object);
		}
	}

	public Aspect getAspect() {
		return itpd.getDeclaringAspect();
	}

	public int compareTo(InterTypeDeclarationMetaProperty other) {
		return Collections.reverseOrder().compare(this.getAspect().getPriority(), other.getAspect().getPriority());
	}
}
