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
 * this class represents a method access at a specific object
 * @author Joscha Drechsler
 */
public class ObjectMethodLocation extends MethodLocation implements ObjectLocation {
	/**
	 * the object
	 */
	private Object object;
	
	/**
	 * Object constructor. to be used if neither method name nor arguments
	 * are of any interest.
	 * @param object the object
	 */
	public ObjectMethodLocation(Object object) {
		super();
		this.object = object;
	}
	
	/**
	 * object string constructor. to be used if arguments are not of
	 * interest.
	 * @param object the object
	 * @param methodName the method name
	 */
	public ObjectMethodLocation(Object object, String methodName){
		super(methodName);
		this.object = object;
	}
	
	/**
	 * object array constructor
	 * @param object the object
	 * @param methodName the method name
	 * @param args the arguments
	 */
	public ObjectMethodLocation(Object object, String methodName, Object[] args){
		super(methodName, args);
		this.object = object;
	}

	/**
	 * class array constructor
	 * @param object the object
	 * @param methodName the method name
	 * @param params the requested parameter array
	 */
	public ObjectMethodLocation(Object object, String methodName, Class<?>[] params){
		super(methodName, params);
		this.object = object;
	}
	
	/**
	 * retrieves the object on which the method was called.
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}
	
	public Class<?> getTargetClass() {
		return object.getClass();
	}
}
