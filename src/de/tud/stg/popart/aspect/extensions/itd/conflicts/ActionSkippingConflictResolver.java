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
import groovy.lang.MetaProperty;

import java.util.Set;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationAspectManager;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaMethod;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;

/**
 * This type of conflict resolver simply ignores any introduced methods or properties
 * whenever a conflict occurs between introduced members and members defined in the
 * base code. This can be particularly useful when Inter-Type Declarations are
 * used to ensure, that any set of classes has a specific method or property,
 * while some of these classes may already have that method or property. In that
 * case, instead of raising an error due to duplicate definitions, the introduced
 * members should simply be ignored for these classes, which is done by this
 * conflict resolver.
 * @author Joscha Drechsler
 */
public class ActionSkippingConflictResolver extends InterTypeDeclarationConflictResolver {

	/**
	 * Overrides the default behavior to skip any introduced methods when there
	 * was a method defined by the base code.
	 */
	@Override
	public MetaMethod resolveConflict(InterTypeDeclarationAspectManager aspectManager, MethodLocation location, MetaMethod baseMetaMethod, Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods) {
		if(baseMetaMethod != null) return baseMetaMethod;
		return super.resolveConflict(aspectManager, location, baseMetaMethod, conflictingMetaMethods);
	}
	
	/**
	 * Overrides the default behavior to skip any introduced property when there
	 * was a property defined by the base code.
	 */
	@Override
	public MetaProperty resolveConflict(InterTypeDeclarationAspectManager aspectManager, PropertyLocation location, MetaProperty baseMetaProperty, Set<InterTypeDeclarationMetaProperty> conflictingMetaPropertys) {
		if(baseMetaProperty != null) return baseMetaProperty;
		return super.resolveConflict(aspectManager, location, baseMetaProperty, conflictingMetaPropertys);
	}
}
