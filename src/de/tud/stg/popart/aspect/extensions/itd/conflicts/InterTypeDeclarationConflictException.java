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

import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;

/**
 * This exception indicates some conflict related to Inter-Type declarations.
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationConflictException extends RuntimeException {
	/**
	 * serial version UID 
	 */
	private static final long serialVersionUID = -1239871472347L;
	
	/**
	 * the location which was accessed causing this exception 
	 */
	private StructureLocation location;
	
	/**
	 * Message-Only constructor
	 * @param location the accessed element
	 * @param message the message
	 */
	public InterTypeDeclarationConflictException(StructureLocation location, String message) {
		super("Exception accessing "+location+": "+message);
		this.location = location;
	}

	/**
	 * Nested exception constructor
	 * @param location the accessed element
	 * @param message the message
	 * @param cause the nested exception
	 */
	public InterTypeDeclarationConflictException(StructureLocation location, String message, Throwable cause) {
		super("Exception accessing "+location+": "+message,cause);
		this.location = location;
	}
	
	/**
	 * gives information about the location within the programs runtime
	 * structure, which was accessed though this access was ambigous. 
	 * @return the location describing object
	 */
	public StructureLocation getLocation(){
		return location;
	}
}
