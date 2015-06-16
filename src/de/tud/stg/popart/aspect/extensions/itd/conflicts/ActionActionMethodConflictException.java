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

import java.util.Set;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaMethod;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;

/**
 * This exception indicates ambigous method access due to multiple
 * introductions matching a method call without base code involvement
 * @author Joscha Drechsler
 */
public class ActionActionMethodConflictException extends InterTypeDeclarationConflictException {
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 2389472385752345L;
	
	/**
	 * the conflicting methods
	 */
	private Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods;
	
	/**
	 * nested constructor
	 * @param location the location
	 * @param conflictingMetaMethods the conflicting methods
	 * @param cause the nested exception
	 */
	public ActionActionMethodConflictException(StructureLocation location, Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods, Throwable cause) {
		super(location,"Ambigous method access. Multiple conflicting introductions: "+conflictingMetaMethods+". Reason is: "+cause.getMessage(), cause);
		this.conflictingMetaMethods = conflictingMetaMethods;
	}

	/**
	 * non-nested constructor
	 * @param location the location
	 * @param conflictingMetaMethods the conflicting methods
	 */
	public ActionActionMethodConflictException(StructureLocation location, Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods) {
		super(location,"Ambigous method access. Multiple conflicting introductions: "+conflictingMetaMethods);
		this.conflictingMetaMethods = conflictingMetaMethods;
	}

	/**
	 * retrieves a list of all involved introduced meta methods
	 * @return involved meta methods
	 */
	public Set<InterTypeDeclarationMetaMethod> getConflictingMetaMethods() {
		return conflictingMetaMethods;
	}

}
