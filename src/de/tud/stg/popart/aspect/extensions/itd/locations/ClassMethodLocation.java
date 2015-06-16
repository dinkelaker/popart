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
 * This class represents a method access for a given class
 * of objects. For example, if a method representation
 * for a class of objects is accessed (reflection) instead
 * of the actual method of a specific object called.
 * @author Joscha Drechsler
 */
public class ClassMethodLocation extends MethodLocation {
	/**
	 * the class
	 */
	private Class<?> theClass;
	
	/**
	 * class only constructor
	 * @param theClass the class
	 */
	public ClassMethodLocation(Class<?> theClass) {
		super();
		this.theClass = theClass;
	}
	
	/**
	 * list constructor
	 * @param theClass the class
	 * @param methodName the methods name
	 * @param params the requested parameters array
	 */
	public ClassMethodLocation(Class<?> theClass, String methodName, Class<?>[] params){
		super(methodName, params);
		this.theClass = theClass;
	}
	
	/**
	 * array constructor
	 * @param theClass the class
	 * @param methodName the methods name
	 * @param args the used argument array
	 */
	public ClassMethodLocation(Class<?> theClass, String methodName, Object[] args){
		super(methodName, args);
		this.theClass = theClass;
	}
	
	/**
	 * retrieves the class whose method representation was requested
	 * @return the class
	 */
	public Class<?> getTargetClass() {
		return theClass;
	}
}
