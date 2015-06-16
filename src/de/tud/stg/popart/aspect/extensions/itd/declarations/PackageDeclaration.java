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
 * This class represents a package as a declared element
 * @author Joscha Drechsler
 */
public class PackageDeclaration implements Declaration {
	/**
	 * the declared package
	 */
	public final Package thePackage;
	
	/**
	 * constructor.
	 * @param thePackage the declared package
	 */
	public PackageDeclaration(Package thePackage){
		this.thePackage = thePackage;
	}
	
	/**
	 * a package has no parent element.
	 */
	public Declaration getParent() {
		return null;
	}

	@Override
	public String toString() {
		return thePackage.toString();
	}
	
	@Override
	public int hashCode() {
		return thePackage.hashCode()+123;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof PackageDeclaration) && thePackage.equals((PackageDeclaration) obj);
	}
}
