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

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationAspectManager;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaMethod;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;
import de.tud.stg.popart.aspect.extensions.itd.declarations.ClassDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;

/**
 * This conflict decorates another resolver, by applying defined rules to the
 * set of conflicting members and filtering out all members which do not share
 * the highest priority within the set. The base code is always preferred over
 * any introduced member. If there are multiple members sharing the highest
 * priority, the decorated conflict resolvement will be invoked.
 * @author Joscha Drechsler
 */
public class RuleBasedConflictResolver extends InterTypeDeclarationConflictResolver {
	private InterTypeDeclarationConflictResolver delegate;
	
	private boolean dropPrioritySupersededMembers = false;
	
	/**
	 * Constructor. Decorates another conflict resolver with the rule
	 * based filtering defined by this resolver. Disables dropping of
	 * members, which are superseded in priority.
	 * @param delegate the delegate
	 */
	public RuleBasedConflictResolver(InterTypeDeclarationConflictResolver delegate){
		this.delegate = delegate;
	}
	
	/**
	 * Customized Constructor. Decorates another conflict resolver with rule
	 * based filtering and sets the "drop on supersede" option to the given
	 * boolean value.
	 * @param delegate the delegate
	 * @param dropPrioritySupersededMembers the option setting
	 */
	public RuleBasedConflictResolver(InterTypeDeclarationConflictResolver delegate, boolean dropPrioritySupersededMembers){
		this.delegate = delegate;
		this.dropPrioritySupersededMembers = dropPrioritySupersededMembers;
	}
	
	/**
	 * Overrides the default property resolveConflict method, replacing all
	 * conflicting meta properties by a {@link WritingMergedMetaProperty},
	 * merging all the conflicting properties for write access and
	 * selecting the most highly prioritized property on read conflicts.
	 */
	@Override
	public MetaProperty resolveConflict(InterTypeDeclarationAspectManager aspectManager, PropertyLocation location, MetaProperty baseMetaProperty, Set<InterTypeDeclarationMetaProperty> conflictingMetaProperties) {
		final MetaProperty readProperty;
		if(baseMetaProperty != null){
			readProperty = baseMetaProperty;
		}else{
			//copy all properties, which have the same highest priority, to a new set
			Set<InterTypeDeclarationMetaProperty> reducedConflictSet = reduceConflictListToOnlyHighestPriority(aspectManager, conflictingMetaProperties);
			if(reducedConflictSet.size() == 1){
				//if there is only one property of the highest priority, use it
				readProperty = reducedConflictSet.iterator().next();
			}else{
				//continue conflict resolvement with the reduced set of high priority properties.
				readProperty = delegate.resolveConflict(aspectManager, location, baseMetaProperty, reducedConflictSet);
			}
		}

		return new WritingMergedMetaProperty(location.getPropertyName(), baseMetaProperty, conflictingMetaProperties, new ClassDeclaration(this.getClass())) {
			@Override
			public Object getProperty(Object object) {
				return readProperty.getProperty(object);
			}
		};
	}
	
	/**
	 * Overrides the default method resolveConflict method, to select the
	 * most highly prioritized method on conflicts.
	 */
	@Override
	public MetaMethod resolveConflict(InterTypeDeclarationAspectManager aspectManager, MethodLocation location, MetaMethod baseMetaMethod, Set<InterTypeDeclarationMetaMethod> conflictingMetaMethods) {
		if(baseMetaMethod != null) return baseMetaMethod;
		//copy all methods, which have the same highest priority, to a new set
		Set<InterTypeDeclarationMetaMethod> reducedConflictSet = reduceConflictListToOnlyHighestPriority(aspectManager, conflictingMetaMethods);
		if(reducedConflictSet.size() == 1){
			//if there is only one method of the highest priority, use it
			return reducedConflictSet.iterator().next();
		}else{
			//continue conflict resolvement with the reduced set of high priority methods.
			return delegate.resolveConflict(aspectManager, location, baseMetaMethod, reducedConflictSet);
		}
	}
	
	/**
	 * This method applies all Rules to the set of conflicting methods
	 * and returns a new Set containing only the elements remaining after
	 * the rules application, and only those of highest priority.<br>
	 * @param <T> the type of aspect members to filter
	 * @param aspectManager the aspect manager that called this method.
	 * @param conflictings the set of conflicting T's
	 * @return the reduced set of conflicting T's.
	 * @see {@link InteractionAwareAspectManager#applyRules(List)}
	 */
	public <T extends AspectMember> Set<T> reduceConflictListToOnlyHighestPriority(InterTypeDeclarationAspectManager aspectManager, Set<T> conflictings) {
		Comparator<? super AspectMember> comparator = AspectFactory.getDefaultComparator(); //iaam.getComparator();
		
		//apply rules to conflict list
		List<T> sortedDeclarations = new java.util.ArrayList<T>(conflictings);
		try {
			aspectManager.applyRules(sortedDeclarations);
		} catch (RuleInconsistencyException e) {
			throw new RuntimeException(e);
		}

		/*
		 *  This additional filtering is optional, because it creates implicit
		 *  assumptions of mutual exlusion between inter-type declarations of aspects,
		 *  which are only sorted to precedence.
		 *  However, this also prevents inter-type declaration conflict resolution
		 *  through aspect priorities, as these are used equivalent to precedence
		 *  declarations by this conflict resolver.
		 */
		if(dropPrioritySupersededMembers){
			T first = sortedDeclarations.get(0);
	
			//revert to set containing only the highest priority elements.
			Set<T> reducedConflictSet = new java.util.HashSet<T>();
			for(T method : sortedDeclarations){
				if(comparator.compare(first, method) != 0) break;
				reducedConflictSet.add(method);
			}
			return reducedConflictSet;
		}else{
			return new java.util.HashSet<T>(sortedDeclarations);
		}
	}
}
