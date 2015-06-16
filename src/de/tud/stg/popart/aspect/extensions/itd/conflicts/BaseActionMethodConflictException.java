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

import groovy.lang.MetaMethod;

import java.util.Set;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaMethod;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;

/**
 * This exception indicates ambigous method access due to multiple
 * introductions matching a method call including the base code method
 * @author Joscha Drechsler
 */
public class BaseActionMethodConflictException extends InterTypeDeclarationConflictException {
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 92582358723948L;
	/**
	 * involved introduced meta properties
	 */
	private Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods;
	/**
	 * base code method
	 */
	private MetaMethod baseMetaMethod;
	
	/**
	 * nested constructor
	 * @param location the location
	 * @param baseMetaMethod the base code method
	 * @param conflictingMetaMethods the introduced methods
	 * @param cause the nested exception
	 */
	public BaseActionMethodConflictException(StructureLocation location, MetaMethod baseMetaMethod, Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods, Throwable cause) {
		super(location, "Ambigous method access. Base method "+baseMetaMethod+" conflicting with introductions: "+conflictingMetaMethods, cause);
		this.conflictingMetaMethods = conflictingMetaMethods;
		this.baseMetaMethod = baseMetaMethod;
	}

	/**
	 * non-nested constructor
	 * @param location the location
	 * @param baseMetaMethod the base code method
	 * @param conflictingMetaMethods the introduced methods
	 */
	public BaseActionMethodConflictException(StructureLocation location, MetaMethod baseMetaMethod, Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods) {
		super(location, "Ambigous method access. Base method "+baseMetaMethod+" conflicting with introductions: "+conflictingMetaMethods);
		this.conflictingMetaMethods = conflictingMetaMethods;
		this.baseMetaMethod = baseMetaMethod;
	}

	/**
	 * retrieves a list of all involved introduced meta methods.
	 * does NOT include the base code method
	 * @return involved meta methods
	 */
	public Set<InterTypeDeclarationMetaMethod> getConflictingMetaMethods() {
		return conflictingMetaMethods;
	}
	
	/**
	 * retrieves the base code meta method involved in the conflict
	 * @return the base code method
	 */
	public MetaMethod getBaseMetaMethod(){
		return baseMetaMethod;
	}

}
