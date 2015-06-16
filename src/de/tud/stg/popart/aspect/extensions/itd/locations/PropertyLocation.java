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
package de.tud.stg.popart.aspect.extensions.itd.locations;

/**
 * A property location is a property access.
 * @author Joscha Drechsler
 */
public abstract class PropertyLocation implements StructureLocation {
	/**
	 * the properties name
	 */
	private String propertyName = null;
	
	/**
	 * empty constructor. to be used if only the type of accessed element
	 * (property) is of interest.
	 */
	public PropertyLocation(){}
	
	/**
	 * string constructor
	 * @param propertyName the properties name
	 */
	public PropertyLocation(String propertyName){
		this.propertyName = propertyName;
	}
	
	/**
	 * retrieves the accessed properties name
	 * @return the name, or <code>null</code> if not of interest.
	 */
	public String getPropertyName(){
		return propertyName;
	}

	/**
	 * constructs a new ClassPropertyLocation containing only the
	 * static information of this location. This is usefull for using
	 * the location as a hashmap key: If the actual location itself
	 * was used, and that actual location would refer concrete objects,
	 * these objects would be held in memory forever. By using
	 * only the objects classes, the objects themselves are not referenced
	 * any longer and there is no memory leak.
	 * @return the same location, without references to concrete objects.
	 */
	public ClassPropertyLocation staticInformationOnly(){
		return new ClassPropertyLocation(getTargetClass(), getPropertyName());
	}

	/**
	 * hashCode is dependent on the target class and a property name,
	 * if one is set.
	 */
	@Override
	public final int hashCode() {
		int hashCode = getTargetClass().hashCode();
		if(propertyName != null) hashCode ^= propertyName.hashCode();
		return hashCode;
	}
	
	/**
	 * two property locations are equal to each other, if there static
	 * information is the same. A property locations static information
	 * includes the target class, the properties name. the assigned value
	 * is assumed to be class object, so there is no need to include this
	 * in the check as object is always equal to object.
	 */
	@Override
	public final boolean equals(Object obj) {
		if(!(obj instanceof PropertyLocation)) return false;
		PropertyLocation other = (PropertyLocation) obj;
		
		if(!getTargetClass().equals(other.getTargetClass())) return false;
		
		if(propertyName != other.propertyName){
			if(propertyName == null || other.propertyName == null) return false;
			if(!propertyName.equals(other.propertyName)) return false;
		}
				
		return true;
	}
}
