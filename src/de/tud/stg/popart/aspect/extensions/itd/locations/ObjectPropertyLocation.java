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
 * This class represents a property access at a specific object.
 * @author Joscha Drechsler
 */
public class ObjectPropertyLocation extends PropertyLocation implements ObjectLocation {
	/**
	 * the object
	 */
	private Object object;
	
	/**
	 * the new value
	 */
	private Object newValue = null;
	
	/**
	 * object constructor. to be used if the properties name is not of interest
	 * @param object the accessed object
	 */
	public ObjectPropertyLocation(Object object) {
		super();
		this.object = object;
	}
	
	/**
	 * object string constructor. to be used in case of read access
	 * @param object the accessed object
	 * @param propertyName the accessed property name
	 */
	public ObjectPropertyLocation(Object object, String propertyName){
		super(propertyName);
		this.object = object;
	}
	
	/**
	 * object string object constructor. to be used in case of write access
	 * @param object the object
	 * @param propertyName the properties name
	 * @param newValue the new value to be assigned
	 */
	public ObjectPropertyLocation(Object object, String propertyName, Object newValue){
		this(object, propertyName);
		this.newValue = newValue;
	}
	
	/**
	 * retrieves the object whose property was accessed
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}
	
	/**
	 * retrieves the value that is about to be assigned. Note that
	 * property lookup is done based purely on the property name.
	 * Therefore, any caching functionality is based solely on the
	 * name too. If you use a property locations newValue for anything,
	 * you will need do disable all such caches!
	 * @return the value object, or <code>null</code> if read access.
	 */
	public Object getNewValue() {
		return newValue;
	}
	
	public Class<?> getTargetClass() {
		return object.getClass();
	}
}
