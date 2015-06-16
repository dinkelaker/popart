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
package de.tud.stg.popart.aspect.extensions.itd;

import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;

import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.itd.conflicts.ActionActionMethodConflictException;
import de.tud.stg.popart.aspect.extensions.itd.conflicts.BaseActionMethodConflictException;
import de.tud.stg.popart.aspect.extensions.itd.conflicts.InterTypeDeclarationConflictResolver;
import de.tud.stg.popart.aspect.extensions.itd.declarations.Declaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;

/**
 * This AspectManager supports lookup of Inter-Type declarations and
 * resolvement of occurring conflicts.
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationAspectManager extends InteractionAwareAspectManager {
	/**
	 * The current conflict resolvement strategy.
	 */
	/* 
	 * TODO once the aspect manager and aspect meta-manager are separated,
	 * all conflict resolvement strategies should be implemented as separate
	 * meta-managers, and this field is to be deleted.
	 */
	private InterTypeDeclarationConflictResolver resolvementStrategy;
	
	/**
	 * Constructor.
	 */
	public InterTypeDeclarationAspectManager() {
		resetConflictResolver();
	}
	
	/**
	 * This method resets the current conflict resolvement strategy
	 * to its default.
	 */
	public void resetConflictResolver(){
		resolvementStrategy = new InterTypeDeclarationConflictResolver();
	}
	
	/**
	 * This method retrieves the current conflict resolver
	 * @return the resolver
	 */
	public InterTypeDeclarationConflictResolver getResolvementStrategy() {
		return resolvementStrategy;
	}
	
	/**
	 * This method replaces the current conflict resolvement strategy.
	 * @param resolvementStrategy the new resolver
	 */
	public void setResolvementStrategy(InterTypeDeclarationConflictResolver resolvementStrategy) {
		this.resolvementStrategy = resolvementStrategy;
	}
	
	/**
	 * This method collects all inter-type declarations serving the given location
	 * by propagating the location to all aspects.
	 * @param location the accessed location
	 * @return a set of applicable introduced methods
	 */
	public Set<InterTypeDeclarationMetaMethod> performMethodLookup(MethodLocation location){
		Set<InterTypeDeclarationMetaMethod> result = new java.util.HashSet<InterTypeDeclarationMetaMethod>();
		for(Aspect aspect : getAspects()){
			if(aspect instanceof InterTypeDeclarationAspect){
				((InterTypeDeclarationAspect) aspect).receiveMethodLocation(location, result);
			}
		}
		return result;
	}
	
	/**
	 * This method collects all inter-type declarations serving the given location
	 * by propagating the location to all aspects
	 * @param location the accessed location
	 * @return a set of applicable introduced properties
	 */
	public Set<InterTypeDeclarationMetaProperty> performPropertyLookup(PropertyLocation location){
		Set<InterTypeDeclarationMetaProperty> result = new java.util.HashSet<InterTypeDeclarationMetaProperty>();
		for(Aspect aspect : getAspects()){
			if(aspect instanceof InterTypeDeclarationAspect){
				((InterTypeDeclarationAspect) aspect).receivePropertyLocation(location, result);
			}
		}
		return result;
	}
	
	/**
	 * This method resolves a method conflict by taking the base code method
	 * and a set of introduced methods and calculating a single representative
	 * which is to be invoked. The representative will either by the base method,
	 * one of the introduced methods, or a completely new method which could
	 * aggregate all or some of the methods provided within the parameters.
	 * @param location the accessed location
	 * @param baseCodeDefinition a method defined by the base code, may be <code>null</code>
	 * @param introducedDefinitions a set of introduced methods
	 * @return a single method
	 * @throws BaseActionMethodConflictException if the conflict can not be resolved
	 * 		and a base code definition is included
	 * @throws ActionActionMethodConflictException if the conflict can not be resolved
	 * 		and no base code definition is included
	 */
	public MetaMethod resolveConflict(MethodLocation location, MetaMethod baseCodeDefinition, Set<InterTypeDeclarationMetaMethod> introducedDefinitions){
		return resolvementStrategy.resolveConflict(this, location, baseCodeDefinition, introducedDefinitions);
	}
	
	/**
	 * This method resolves a property conflict by taking the base code property
	 * and a set of introduced properties and calculating a single representative
	 * which is to be invoked. The representative will either by the base property,
	 * one of the introduced properties, or a completely new property which could
	 * aggregate all or some of the properties provided within the parameters.
	 * @param location the accessed location
	 * @param baseCodeDefinition a method defined by the base code, may be <code>null</code>
	 * @param introducedDefinitions a set of introduced properties
	 * @return a single property
	 * @throws BaseActionPropertyConflictException if the conflict can not be resolved
	 * 		and a base code definition is included
	 * @throws ActionActionPropertyConflictException if the conflict can not be resolved
	 * 		and no base code definition is included
	 */
	public MetaProperty resolveConflict(PropertyLocation location, MetaProperty baseCodeDefinition, Set<InterTypeDeclarationMetaProperty> introducedDefinitions){
		return resolvementStrategy.resolveConflict(this, location, baseCodeDefinition, introducedDefinitions);
	}
	
	/**
	 * This method reports an occurred conflict to all aspects
	 * @param location the accessed location
	 * @param aspects the set of aspects to be informed
	 * @param applicableDeclarations the set of applicable declarations
	 */
	public void reportConflictToAllInvolvedAspects(StructureLocation location, Set<InterTypeDeclarationAspect> aspects, Set<? extends Declaration> applicableDeclarations){
		for(InterTypeDeclarationAspect aspect : aspects){
			aspect.introductionConflict(location, aspects, applicableDeclarations);
		}
	}
}
