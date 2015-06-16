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

import java.util.Set;

import groovy.lang.MetaClassRegistry;
import de.tud.stg.popart.aspect.MetaAspect;
import de.tud.stg.popart.aspect.extensions.itd.declarations.Declaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectMethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectPropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;

public class InterTypeDeclarationMetaAspect extends MetaAspect {
	/**
	 * Constructor.
	 * @param registry the meta class registry to be used for static call lookups etc
	 * @param theClass the aspect class
	 */
	public InterTypeDeclarationMetaAspect(MetaClassRegistry registry, Class<?> theClass) {
		super(registry, theClass);
	}
	
	/**
	 * This method is invoked when a method location is accessed,
	 * to collect all applicable method introductions from an aspect.
	 * @param aspect the aspect
	 * @param location the location
	 * @param applicableMethods the set of methods to add candidates to
	 */
	public void receiveMethodLocation(InterTypeDeclarationAspect aspect, MethodLocation location, Set<InterTypeDeclarationMetaMethod> applicableMethods){
		for(InterTypeMethodDeclaration itmd : aspect.getInterTypeMethodDeclarations()){
			StructureDesignator designator = itmd.getPattern();
			if(matchStructureDesignator(aspect, location, designator)){
				matchedStructureDesignator(aspect, location, designator);
				itmd.receiveMethodLocation(location, applicableMethods);
			}else{
				notMatchedStructureDesignator(aspect, location, designator);
			}
		}
	}
	/**
	 * This method is invoked when a property location is accessed,
	 * to collect all applicable property introductions from an aspect.
	 * @param aspect the aspect
	 * @param location the location
	 * @param applicableProperties the set of properties to add candidates to
	 */
	public void receivePropertyLocation(InterTypeDeclarationAspect aspect, PropertyLocation location, Set<InterTypeDeclarationMetaProperty> applicableProperties){
		for(InterTypePropertyDeclaration itpd : aspect.getInterTypePropertyDeclarations()){
			StructureDesignator designator = itpd.getPattern();
			if(matchStructureDesignator(aspect, location, designator)){
				matchedStructureDesignator(aspect, location, designator);
				itpd.receivePropertyLocation(location, applicableProperties);
			}else{
				notMatchedStructureDesignator(aspect, location, designator);
			}
		}
	}
	
	/**
	 * This method is invoked to evaluate a structure designator.
	 * @param aspect the current aspect
	 * @param location the accessed location
	 * @param designator the designator to be evaluated
	 * @return <code>true</code> if the location is covered by the designator,
	 * 		<code>false</code> otherwise.
	 */
	public boolean matchStructureDesignator(InterTypeDeclarationAspect aspect, StructureLocation location, StructureDesignator designator){
		Class<?> theClass = location.getTargetClass();
		return designator.matchesCached(theClass);
	}
	
	/**
	 * This method is invoked when a structure designator matched
	 * @param aspect the current aspect
	 * @param location the location the designator was matched against
	 * @param designator the designator that was matched
	 */
	public void matchedStructureDesignator(InterTypeDeclarationAspect aspect, StructureLocation location, StructureDesignator designator){
		
	}
	/**
	 * This method is invoked when a structure designator did not match
	 * @param aspect the current aspect
	 * @param location the location the designator was matched against
	 * @param designator the designator that was matched
	 */
	public void notMatchedStructureDesignator(InterTypeDeclarationAspect aspect, StructureLocation location, StructureDesignator designator){
		
	}
	
	/**
	 * This method is invoked before a method, introduced by an aspect, is invoked.
	 * @param aspect the aspect
	 * @param location the accessed location
	 * @param method the method about to be invoked
	 */
	public void beforeMethodInvocation(InterTypeDeclarationAspect aspect, ObjectMethodLocation location, InterTypeDeclarationMetaMethod method){
		
	}
	/**
	 * This method is invoked after a method, introduced by an aspect, was invoked.
	 * @param aspect the aspect
	 * @param location the accessed location
	 * @param method the invoked method
	 * @param result the returned value
	 */
	public void afterMethodInvocation(InterTypeDeclarationAspect aspect, ObjectMethodLocation location, InterTypeDeclarationMetaMethod method, Object result){
		
	}
	
	/**
	 * This method is invoked before a field, introduced by an aspect, is read.
	 * @param aspect the aspect
	 * @param location the accessed location
	 * @param property the field about to be read
	 */
	public void beforeFieldGet(InterTypeDeclarationAspect aspect, ObjectPropertyLocation location, InterTypeDeclarationMetaProperty property){
		
	}
	/**
	 * This method is invoked after a field introduced by an aspect, was read.
	 * @param aspect the aspect
	 * @param location the accessed location
	 * @param property the read field
	 * @param result the read value
	 */
	public void afterFieldGet(InterTypeDeclarationAspect aspect, ObjectPropertyLocation location, InterTypeDeclarationMetaProperty property, Object result){
		
	}

	/**
	 * This method is invoked before a field, introduced by an aspect, is written.
	 * @param aspect the aspect
	 * @param location the accessed location, containing the new value
	 * @param property the field about to be written
	 */
	public void beforeFieldSet(InterTypeDeclarationAspect aspect, ObjectPropertyLocation location, InterTypeDeclarationMetaProperty property){
		
	}
	/**
	 * This method is invoked after a field, introduced by an aspect, was written.
	 * @param aspect the aspect
	 * @param location the accessed location
	 * @param property the written field
	 */
	public void afterFieldSet(InterTypeDeclarationAspect aspect, ObjectPropertyLocation location, InterTypeDeclarationMetaProperty property){
		
	}

	/**
	 * This method is invoked when a conflict occurred involving a member of
	 * the aspect.
	 * @param interTypeDeclarationAspect the aspect
	 * @param location the accessed location
	 * @param aspectInterferenceSet all involved aspects
	 * @param applicableDeclarations all applicable declarations
	 */
	public void introductionConflict(InterTypeDeclarationAspect interTypeDeclarationAspect, StructureLocation location, Set<InterTypeDeclarationAspect> aspectInterferenceSet, Set<? extends Declaration> applicableDeclarations) {
		
	}
}
