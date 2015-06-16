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

import groovy.lang.MetaProperty;

import java.util.Set;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;

/**
 * This exception indicates ambigous property access due to multiple
 * introductions matching a property access including the base code method
 * @author Joscha Drechsler
 */
public class BaseActionPropertyConflictException extends InterTypeDeclarationConflictException {
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 546545640654514L;
	/**
	 * the involved introduced meta properties
	 */
	private Set<InterTypeDeclarationMetaProperty> conflictingMetaProperties;
	/**
	 * the involved base code property
	 */
	private MetaProperty baseMetaProperty;
	
	/**
	 * nested constructor
	 * @param location the location
	 * @param baseMetaProperty the base code property
	 * @param conflictingMetaProperties the introduced properties
	 * @param cause the nested exception
	 */
	public BaseActionPropertyConflictException(StructureLocation location, MetaProperty baseMetaProperty, Set<InterTypeDeclarationMetaProperty> conflictingMetaProperties, Throwable cause) {
		super(location, "Ambigous property access. Base property "+baseMetaProperty.getType()+" "+baseMetaProperty.getName()+" conflicting with introductions: "+conflictingMetaProperties+". Reason is: "+cause.getMessage(), cause);
		this.conflictingMetaProperties = conflictingMetaProperties;
		this.baseMetaProperty = baseMetaProperty;
	}
	
	/**
	 * non-nested constructor
	 * @param location the location
	 * @param baseMetaProperty the base code property
	 * @param conflictingMetaProperties the introduced properties
	 */
	public BaseActionPropertyConflictException(StructureLocation location, MetaProperty baseMetaProperty, Set<InterTypeDeclarationMetaProperty> conflictingMetaProperties) {
		super(location, "Ambigous property access. Base property "+baseMetaProperty.getType()+" "+baseMetaProperty.getName()+" conflicting with introductions: "+conflictingMetaProperties);
		this.conflictingMetaProperties = conflictingMetaProperties;
		this.baseMetaProperty = baseMetaProperty;
	}

	/**
	 * retrieves a list of all involved introduced meta properties.
	 * does NOT include the base code property
	 * @return involved meta properties
	 */
	public Set<InterTypeDeclarationMetaProperty> getConflictingMetaProperties() {
		return conflictingMetaProperties;
	}

	/**
	 * retrieves the base code meta property involved in the conflict
	 * @return the base code property
	 */
	public MetaProperty getBaseMetaProperty(){
		return baseMetaProperty;
	}

}
