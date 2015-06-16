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

import org.codehaus.groovy.runtime.MetaClassHelper;

/**
 * This class represents an access to a method with a specific
 * set of arguments, at runtime
 * @author Joscha Drechsler
 */
public abstract class MethodLocation implements StructureLocation {
	/**
	 * the accessed methods name
	 */
	private String methodName = null;

	/**
	 * the used arguments
	 */
	private Object[] args = null;

	/**
	 * the arguments classes
	 */
	private Class<?>[] argumentClasses = null;
	
	/**
	 * Empty constructor. To be used in case nothing is of interest except
	 * the type of requested member (method).
	 */
	public MethodLocation() {}
	
	/**
	 * String constructor. To be used if arguments and argument classes are
	 * unknown or not of interest.
	 * @param methodName
	 */
	public MethodLocation(String methodName){
		this();
		this.methodName = methodName;
	}
	
	/**
	 * object array constructor. to be used arguments are known
	 * @param methodName the accessed methods name
	 * @param args the used arguments array
	 */
	public MethodLocation(String methodName, Object[] args){
		this(methodName, MetaClassHelper.castArgumentsToClassArray(args));
		this.args = args;
	}
	
	/**
	 * class array constructor. to be used if argument types are known.
	 * @param methodName the requested methods name
	 * @param params the requested parameter array
	 */
	public MethodLocation(String methodName, Class<?>[] params){
		this(methodName);
		this.argumentClasses = params;
	}
	
	/**
	 * retrieves the accessed methods name
	 * @return the name, or <code>null</code> if not of interest
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * retrieves the used arguments list. Note that method lookup is
	 * done based purely on the arguments classes. Therefore, any
	 * caching functionality is based on the argument classes too.
	 * If you use a method locations arguments for anything, you will
	 * need do disable all such caches!
	 * @return the list, or <code>null</code> if not of interest
	 */
	public Object[] getArgs() {
		return args;
	}
	
	/**
	 * retrieves the used argument types list
	 * @return the list, or <code>null</code> if not of interest
	 */
	public Class<?>[] getArgumentClasses(){
		return argumentClasses;
	}
	
	/**
	 * this method builds a new method location, that only contains
	 * the current locations static information. This is usefull for
	 * using a method location object as a map key: since equality and
	 * hashcode are based on the static information only, there is no
	 * need to reference concrete objects when using the location as a
	 * key. Doing so would only prevent these objects from beeing garbage
	 * collected, thereby leading to memory leak.
	 * @return
	 */
	public ClassMethodLocation staticInformationOnly(){
		return new ClassMethodLocation(getTargetClass(), getMethodName(), getArgumentClasses());
	}
	
	/**
	 * a method locations hashcode is based on the target class,
	 * a method name if present, and the classes used as arguments
	 * if present.
	 */
	@Override
	public final int hashCode() {
		int hashCode = getTargetClass().hashCode();
		if(methodName != null) hashCode ^= methodName.hashCode();
		if(argumentClasses != null) hashCode ^= java.util.Arrays.hashCode(argumentClasses);
		return hashCode;
	}
	
	/**
	 * two method locations are equal to each other, if their static
	 * information is the same. This includes their target class,
	 * their method name if present, and their set of argument classes
	 * if present.
	 */
	@Override
	public final boolean equals(Object obj) {
		if(!(obj instanceof MethodLocation)) return false;
		MethodLocation other = (MethodLocation) obj;
		
		if(!getTargetClass().equals(other.getTargetClass())) return false;
		
		if(methodName != other.methodName){
			if(methodName == null || other.methodName == null) return false;
			if(!methodName.equals(other.methodName)) return false;
		}
		
		if(!java.util.Arrays.equals(argumentClasses, other.argumentClasses)) return false;
		
		return true;
	}
}
