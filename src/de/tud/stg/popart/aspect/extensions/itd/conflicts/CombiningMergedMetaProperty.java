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

import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.declarations.Declaration;
import de.tud.stg.popart.aspect.extensions.itd.declarations.PropertyDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectPropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;

import groovy.lang.MetaProperty;

/**
 * This class merges multiple meta property, propagating write accesses
 * to each merged meta property and forwarding read accesses to the
 * abstract method {@link #combine(List)} which is to be implemented
 * by subclasses.
 * @author Joscha Drechsler
 */
public abstract class CombiningMergedMetaProperty extends WritingMergedMetaProperty implements PropertyDeclaration{
	public CombiningMergedMetaProperty(String name, MetaProperty baseProperty, Set<InterTypeDeclarationMetaProperty> properties, Declaration parent) {
		super(name, baseProperty, properties, parent);
	}

	/**
	 * constructor without base code involvement
	 * @param name the properties name
	 * @param properties a list of introduced properties to be merged
	 */
	public CombiningMergedMetaProperty(String name, Set<InterTypeDeclarationMetaProperty> properties, Declaration parent) {
		super(name, properties, parent);
	}

	/**
	 * retrieves all values of the merged properties and forwards these to
	 * {@link #combine(List)}. wraps any exceptions thrown by combine into
	 * the matching inter-type declaration conflict exceptions.
	 * @param object the object whose property is accessed
	 * @return the merged value
	 */
	@Override
	public final Object getProperty(Object object) {
		List<Object> values = new java.util.ArrayList<Object>(getProperties().size()+1);
		if(getBaseProperty() != null) values.add(getBaseProperty().getProperty(object));
		for(MetaProperty property : getProperties()){
			values.add(property.getProperty(object));
		}
		try{
			return combine(values);
		}catch(Exception e){
			StructureLocation location = new ObjectPropertyLocation(object, getName());
			if(getBaseProperty() == null){
				throw new ActionActionPropertyConflictException(location, getProperties(), e);
			}else{
				throw new BaseActionPropertyConflictException(location, getBaseProperty(), getProperties(), e);
			}
		}
	}

	/**
	 * Value-Combination method. Subclasses should implement this method
	 * and return a combined value if possible. If not, an exception may
	 * be raised. 
	 * @param values the list of all values of the merged properties, with
	 * 		the base properties value as first item (if there was a base
	 * 		property).
	 * @return the combined value
	 */
	public abstract Object combine(List<Object> values);
	
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)) return false;
		return this.getClass().equals(obj.getClass());
	}
}
