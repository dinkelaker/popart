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
import java.util.Iterator;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationAspectManager;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaMethod;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;

/**
 * This type of conflict resolver simply uses the first introduction
 * @author Joscha Drechsler
 */
public class LastActionConflictResolver extends InterTypeDeclarationConflictResolver {

	/**
	 * Overrides the default behavior to use the last introduced methods when there
	 * was a method defined by the base code.
	 */
	@Override
	public MetaMethod resolveConflict(InterTypeDeclarationAspectManager aspectManager, MethodLocation location, MetaMethod baseMetaMethod, Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods) {
		Iterator<InterTypeDeclarationMetaMethod> it = conflictingMetaMethods.iterator();
		MetaMethod last = null;
		while (it.hasNext()) {
			last = it.next();
		}
		if (last == null) return baseMetaMethod;
		return last;
	}
	
	/**
	 * Overrides the default behavior to use the last introduced property when there
	 * was a property defined by the base code.
	 */
	@Override
	public MetaProperty resolveConflict(InterTypeDeclarationAspectManager aspectManager, PropertyLocation location, MetaProperty baseMetaProperty, Set<InterTypeDeclarationMetaProperty> conflictingMetaPropertys) {
		Iterator<InterTypeDeclarationMetaProperty> it = conflictingMetaPropertys.iterator();
		MetaProperty last = null;
		while (it.hasNext()) {
			last = it.next();
		}
		if (last == null) return baseMetaProperty;
		return last;
	}
}
