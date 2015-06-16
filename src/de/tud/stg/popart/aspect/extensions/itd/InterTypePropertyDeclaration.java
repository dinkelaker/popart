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

import java.util.Set;

import groovy.lang.MetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.declarations.PropertyDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;

/**
 * This kind of {@link InterTypeDeclaration} introduces a property with a
 * default value.
 * 
 * @author Joscha Drechsler
 */
public class InterTypePropertyDeclaration extends InterTypeDeclaration implements PropertyDeclaration {
	/**
	 * The MetaProperty of the introduced property
	 */
	private InterTypeDeclarationMetaProperty metaProperty;
	
	/**
	 * Constructor.
	 * @param aspect the aspect defining this introduction
	 * @param pattern the StructureDesignator to determine affected Objects
	 * @param propertyName the properties name
	 * @param defaultValue the properties initial value
	 */
	public InterTypePropertyDeclaration(InterTypeDeclarationAspect aspect, StructureDesignator pattern, Class<?> type, String propertyName, Object defaultValue) {
		super(aspect, pattern);
		metaProperty = new InterTypeDeclarationMetaProperty(this,type,propertyName,defaultValue);
	}

	/**
	 * @return the meta property of the introduced Property
	 */
	public InterTypeDeclarationMetaProperty getIntroductionMetaProperty() {
		return metaProperty;
	}

	public MetaProperty getDeclaredMetaProperty() {
		return metaProperty;
	}

	/**
	 * This method mimcs the reply of
	 * {@link MetaObjectProtocol#getMetaProperty(String)} as if the
	 * property introduced by this introduction was the objects only
	 * property
	 * @return the meta property of the introduced Property, if the
	 * 		name matches. <code>null</code> otherwise.
	 */
	public InterTypeDeclarationMetaProperty getIntroductionMetaProperty(String name) {
		if(name.equals(metaProperty.getName())){
			return metaProperty;
		}else{
			return null;
		}
	}

	/**
	 * This Method mimics the reply of
	 * {@link MetaObjectProtocol#hasProperty(Object, String)} as if the
	 * property introduced by this introduction was the objects only
	 * property.
	 * @param instance the object
	 * @param propertyName the properties name
	 * @return the meta property to access this introduction, or
	 * 		<code>null</code> if the introduction does not match the
	 * 		object.
	 */
	public InterTypeDeclarationMetaProperty introductionHasProperty(Object instance, String propertyName) {
		if (propertyName.equals(metaProperty.getName())	&& this.appliesTo(instance)) {
			return metaProperty;
		} else {
			return null;
		}
	}
	
	/**
	 * {@see Object#toString()}
	 */
	public String toString() {
		return "introduction(pattern:" + getPattern() + ",property:"+metaProperty+")";
	}

	public Class<?> getType(){
		return metaProperty.getType();
	}
	
	public String getName(){
		return metaProperty.getName();
	}
	
	public Object getDefaultValue(){
		return metaProperty.getDefaultValue();
	}
	
	public void resetAllValues() {
		metaProperty.resetAllValues();
	}
	
	public void resetValues(Class<?> theClass){
		metaProperty.resetValues(theClass);
	}
	
	public void resetValue(Object object){
		metaProperty.resetValue(object);
	}

	public void receivePropertyLocation(PropertyLocation location, Set<InterTypeDeclarationMetaProperty> applicableProperties) {
		String propertyName = location.getPropertyName();
		if(propertyName == null || propertyName.equals(metaProperty.getName())) {
			applicableProperties.add(metaProperty);
		}
	}
}
