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

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;

/**
 * This exception indicates ambigous property access due to multiple
 * introductions matching a method call without base code involvement
 * @author Joscha Drechsler
 */
public class ActionActionPropertyConflictException extends InterTypeDeclarationConflictException {
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = -90132132387913L;
	/**
	 * the list of conflicting introduced meta properties
	 */
	private Set<InterTypeDeclarationMetaProperty> conflictingMetaProperties;
	
	/**
	 * nested constructor
	 * @param location the location
	 * @param conflictingMetaProperties the involved meta properties 
	 * @param cause the nested exception
	 */
	public ActionActionPropertyConflictException(StructureLocation location, Set<InterTypeDeclarationMetaProperty> conflictingMetaProperties, Throwable cause) {
		super(location, "Ambigous property access. Multiple conflicting introductions: "+conflictingMetaProperties+". Reason is: "+cause.getMessage(), cause);
		this.conflictingMetaProperties = conflictingMetaProperties;
	}

	/**
	 * non-nested constructor
	 * @param location the location
	 * @param conflictingMetaProperties the involved meta properties
	 */
	public ActionActionPropertyConflictException(StructureLocation location, Set<InterTypeDeclarationMetaProperty> conflictingMetaProperties) {
		super(location, "Ambigous property access. Multiple conflicting introductions: "+conflictingMetaProperties);
		this.conflictingMetaProperties = conflictingMetaProperties;
	}

	/**
	 * retrieves a list of all involved introduced meta properties
	 * @return involved meta methods
	 */
	public Set<InterTypeDeclarationMetaProperty> getConflictingMetaProperties() {
		return conflictingMetaProperties;
	}
}
