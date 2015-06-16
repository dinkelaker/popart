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

import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationAspect;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationAspectManager;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaMethod;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeMethodDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.InterTypePropertyDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.declarations.MetaMethodDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.declarations.MetaPropertyDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.declarations.MethodDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.declarations.PropertyDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;

/**
 * This class represents a basic implementation for conflict resolvement
 * related to introductions.
 * It also offers access to a system wide singleton instance, currently
 * installed to handle conflicts.
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationConflictResolver {	
	/**
	 * notifies the resolver of an interaction at a given method
	 * location. the default implementation only forwards to
	 * {@link #introductionInteractionAtLocation(StructureLocation, Set, List)}.
	 * May be overrode by subclasses.<br>
	 * In case of a conflict that cannot be resolved, depending on whether
	 * the base code is involved, either an
	 * {@link ActionActionMethodConflictException} or a
	 * {@link BaseActionMethodConflictException} is to be thrown. 
	 * @param location the location
	 * @param baseMetaMethod the base method, or <code>null</code> if none
	 * @param conflictingMetaMethods the list of conflicting introduced methods
	 * @return the resolved meta method which should be invoked instead 
	 */
	public MetaMethod resolveConflict(InterTypeDeclarationAspectManager aspectManager, MethodLocation location, MetaMethod baseMetaMethod, Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods) {
		//build aspect set and declarations list
		Set<MethodDeclaration> applicableDeclarations = new java.util.HashSet<MethodDeclaration>(conflictingMetaMethods.size());
		Set<InterTypeDeclarationAspect> aspectInterferenceSet = new java.util.HashSet<InterTypeDeclarationAspect>();
		for(InterTypeDeclarationMetaMethod method : conflictingMetaMethods){
			InterTypeMethodDeclaration declaration = method.getInterTypeMethodDeclaration();
			applicableDeclarations.add(declaration);
			aspectInterferenceSet.add(declaration.getDeclaringAspect());
		}
		if(baseMetaMethod != null) applicableDeclarations.add(new MetaMethodDeclaration(baseMetaMethod));

		//report introduction
		aspectManager.reportConflictToAllInvolvedAspects(location, aspectInterferenceSet, applicableDeclarations);

		/*
		 * if the aspects modified the list so only one candidate is left,
		 * return it. Otherwise, throw exceptions.
		 */
		if(applicableDeclarations.size() == 1){
			return applicableDeclarations.iterator().next().getDeclaredMetaMethod(location.getArgumentClasses());
		}else if(baseMetaMethod == null){
			throw new ActionActionMethodConflictException(location, conflictingMetaMethods);
		}else{
			throw new BaseActionMethodConflictException(location, baseMetaMethod, conflictingMetaMethods);
		}	
	}

	/**
	 * notifies the resolver of an interaction at a given property
	 * location. the default implementation only forwards to
	 * {@link #introductionInteractionAtLocation(StructureLocation, Set, List)}.
	 * May be overrode by subclasses.<br>
	 * In case of a conflict that cannot be resolved, depending on whether
	 * the base code is involved, either an
	 * {@link ActionActionPropertyConflictException} or a
	 * {@link BaseActionPropertyConflictException} is to be thrown.
	 * @param location the location
	 * @param baseMetaProperty the base code property, or <code>null</code> if none
	 * @param conflictingMetaProperties the involved introduced properties
	 * @return
	 */
	public MetaProperty resolveConflict(InterTypeDeclarationAspectManager aspectManager, PropertyLocation location, MetaProperty baseMetaProperty, Set<InterTypeDeclarationMetaProperty> conflictingMetaProperties){
		//build aspect set and declarations list
		Set<PropertyDeclaration> applicableDeclarations = new java.util.HashSet<PropertyDeclaration>(conflictingMetaProperties.size());
		Set<InterTypeDeclarationAspect> aspectInterferenceSet = new java.util.HashSet<InterTypeDeclarationAspect>();
		for(InterTypeDeclarationMetaProperty property : conflictingMetaProperties){
			InterTypePropertyDeclaration declaration = property.getInterTypePropertyDeclaration();
			applicableDeclarations.add(declaration);
			aspectInterferenceSet.add(declaration.getDeclaringAspect());
		}
		if(baseMetaProperty != null) applicableDeclarations.add(new MetaPropertyDeclaration(baseMetaProperty));

		//report interaction
		aspectManager.reportConflictToAllInvolvedAspects(location, aspectInterferenceSet, applicableDeclarations);

		/*
		 * if the aspects modified the list so only one candidate is left,
		 * return it. Otherwise, throw exceptions.
		 */
		if(applicableDeclarations.size() == 1){
			return applicableDeclarations.iterator().next().getDeclaredMetaProperty();
		}else if(baseMetaProperty == null){
			throw new ActionActionPropertyConflictException(location, conflictingMetaProperties);
		}else{
			throw new BaseActionPropertyConflictException(location, baseMetaProperty, conflictingMetaProperties);
		}	
	}
}
