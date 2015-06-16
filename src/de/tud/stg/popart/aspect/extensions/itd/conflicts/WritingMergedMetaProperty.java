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
package de.tud.stg.popart.aspect.extensions.itd.conflicts;

import java.util.Set;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.declarations.Declaration;
import de.tud.stg.popart.aspect.extensions.itd.declarations.PropertyDeclaration;

import groovy.lang.MetaProperty;

/**
 * This class merges multiple meta property, propagating write accesses
 * to each merged meta property. Read access is to be defined by subclasses.
 * @author Joscha Drechsler
 */
public abstract class WritingMergedMetaProperty extends MetaProperty implements PropertyDeclaration{
	/**
	 * all merged introduced properties
	 */
	private Set<InterTypeDeclarationMetaProperty> properties;
	/**
	 * a merged property from the base code, may be null.
	 */
	private MetaProperty baseProperty;
	/**
	 * the parent which declared this meta property
	 */
	private Declaration parent;
	
	/**
	 * constructor with base code involvement
	 * @param name the properties name
	 * @param baseProperty the base codes property to be merged
	 * @param properties a list of introduced properties to be merged
	 * @param parent the parent which declared this meta property
	 */
	public WritingMergedMetaProperty(String name, MetaProperty baseProperty, Set<InterTypeDeclarationMetaProperty> properties, Declaration parent) {
		this(name, properties, parent);
		this.baseProperty = baseProperty;
	}

	/**
	 * constructor without base code involvement
	 * @param name the properties name
	 * @param properties a list of introduced properties to be merged
	 * @param parent the parent which declared this meta property
	 */
	public WritingMergedMetaProperty(String name, Set<InterTypeDeclarationMetaProperty> properties, Declaration parent) {
		super(name, Object.class);
		this.properties = properties;
		this.baseProperty = null;
		this.parent = parent;
	}

	@Override
	public abstract Object getProperty(Object object);
	
	/**
	 * sets the value on all merged properties.
	 * @param object the object whose property is accessed
	 * @param newValue the new value
	 */
	@Override
	public final void setProperty(Object object, Object newValue) {
		for(MetaProperty property : properties){
			property.setProperty(object, newValue);
		}
		if(baseProperty != null) baseProperty.setProperty(object, newValue);
	}
	
	public final MetaProperty getDeclaredMetaProperty() {
		return this;
	}
	
	public final Declaration getParent(){
		return parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof WritingMergedMetaProperty)) return false;
		WritingMergedMetaProperty other = (WritingMergedMetaProperty) obj;
		//property lists must be equal
		if(!this.properties.equals(other.properties)) return false;
		//base property must be null for both
		if(this.baseProperty == null) return other.baseProperty == null;
		//or equal for both.
		return this.baseProperty.equals(other.baseProperty);
	}
	
	/**
	 * retrieves the base codes property which is merged within this property
	 * @return the base code property, or <code>null</code> if the base code
	 * 		is not involved in the conflict.
	 */
	public final MetaProperty getBaseProperty() {
		return baseProperty;
	}
	
	/**
	 * retrieves the set of conflicting introduced meta properties
	 * @return the set
	 */
	public final Set<InterTypeDeclarationMetaProperty> getProperties() {
		return properties;
	}
}
