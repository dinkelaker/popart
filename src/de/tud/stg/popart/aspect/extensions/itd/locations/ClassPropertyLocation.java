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
 * This class represents a property access for a given class
 * of objects. For example, if a property representation
 * for a class of objects is accessed (reflection) instead
 * of the actual property of a specific object.
 * @author Joscha Drechsler
 */
public class ClassPropertyLocation extends PropertyLocation {
	/**
	 * the class
	 */
	private Class<?> theClass;
	
	/**
	 * class constructor. to be used if the properties name is not of interest
	 * @param theClass
	 */
	public ClassPropertyLocation(Class<?> theClass) {
		super();
		this.theClass = theClass;
	}
	
	/**
	 * class string constructor.
	 * @param theClass the class
	 * @param propertyName the property name
	 */
	public ClassPropertyLocation(Class<?> theClass, String propertyName){
		super(propertyName);
		this.theClass = theClass;
	}
	
	/**
	 * retrieves the class, whose property was accessed
	 * @return the class
	 */
	public Class<?> getTargetClass() {
		return theClass;
	}
	
	@Override
	public String toString() {
		return theClass.getCanonicalName()+"."+getPropertyName();
	}
}
