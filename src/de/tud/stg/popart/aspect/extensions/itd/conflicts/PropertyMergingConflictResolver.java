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

import groovy.lang.MetaProperty;

import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationAspectManager;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.declarations.ClassDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;

/**
 * This extended conflict resolver resolves property access conflicts by
 * merging the conflicting meta properties, where the read operation
 * is still considered ambigous, if any of the merged properties reports
 * a value which differs from the other properties values.<br>
 * The conflicts resolved by this resolver cover indeed a very broad spectrum
 * of conflicts with property introductions since, once a property is
 * introduced for any object multiple times, this property can (without the
 * use of groovys meta reflection api) only be accessed in an all-in-one manner,
 * meaning either all properties located at the object would be accessed, or
 * none. This way, this resolver allows for introduction of properties
 * across the whole class hierarchy including intersections between the
 * patterns of these introductions and, assuming all properties are introduced
 * with the same default value and groovys meta reflection api is not used
 * to specificly circumvent merged access, without causing any exceptions due
 * to these intersections. Since the value of any of these properties is
 * definitely linked to the accessed object, the value at any intersection
 * is always well defined, since intersections only occur on single objects.
 * @author Joscha Drechsler
 *
 */
public class PropertyMergingConflictResolver extends InterTypeDeclarationConflictResolver {
	/**
	 * constructor, does nothing.
	 */
	public PropertyMergingConflictResolver() {
		super();
	}
	
	/**
	 * Overrides the default resolveConflict method, replacing all
	 * conflicting meta properties by a merged meta property, merging
	 * all the conflicting properties.
	 */
	@Override
	public MetaProperty resolveConflict(InterTypeDeclarationAspectManager aspectManager, PropertyLocation location, MetaProperty baseMetaProperty, Set<InterTypeDeclarationMetaProperty> conflictingMetaPropertys) {
		return new CombiningMergedMetaProperty(location.getPropertyName(), baseMetaProperty, conflictingMetaPropertys, new ClassDeclaration(this.getClass())) {
			/**
			 * values are combined by comparing all values to each other.
			 * if all values are equal, the equilibrium is returned.
			 * otherwise, an exception is thrown.
			 * @param values the list of values.
			 */
			@Override
			public Object combine(List<Object> values) {
				Object value = values.get(0);
				for(Object otherValue : values){
					if(!value.equals(otherValue)){
						throw new RuntimeException("Cannot combine properties with unequal values. List of values is: "+values);
					}
				}
				return value;
			}
		};
	}
}
