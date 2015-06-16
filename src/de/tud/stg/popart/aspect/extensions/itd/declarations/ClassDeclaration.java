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
package de.tud.stg.popart.aspect.extensions.itd.declarations;

/**
 * This class wraps a Class as a declared object.
 * @author Joscha Drechsler
 */
public class ClassDeclaration implements Declaration {
	/**
	 * the declared class
	 */
	public final Class<?> theClass;
	
	/**
	 * the classes parent
	 */
	public final Declaration parent;
	
	/**
	 * constructor
	 * @param theClass the declared class
	 */
	public ClassDeclaration(Class<?> theClass){
		this.theClass = theClass;
		this.parent = new PackageDeclaration(theClass.getPackage());
	}

	/**
	 * a classes parent element is it's package.
	 */
	public Declaration getParent() {
		return parent;
	}
	
	public String toString(){
		return theClass.toString();
	}
	
	@Override
	public int hashCode() {
		return theClass.hashCode()+123;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ClassDeclaration) && theClass.equals(((ClassDeclaration)obj).theClass);
	}
	
	public Class<?> getTheClass() {
		return theClass;
	}
}
