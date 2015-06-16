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
package de.tud.stg.popart.aspect.extensions.definers;

import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;

public interface IRelationExaminer {

	public void checkRulesConsistency(Set<Aspect> setOfInteractingAspects)
			throws RuleInconsistencyException;

	// public HashSet<ArrayList<String>> getDependencyTransitivitySet(
	// Set<String> setOfInteractingAspects);

	public Set<String> getAspectsInvolvedInDependency(
			Set<String> setOfInteractingAspects);

	public Set<String> getAspectsInvolvedInExclusion(
			Set<String> setOfInteractingAspects);

	public Set<String> getAspectsInvolvedInChoice(
			Set<String> setOfInteractingAspects);

	public boolean isRelatedByExclusion(Aspect a1, Aspect a2);

	public boolean isRelatedByDependency(Aspect a1, Aspect a2);

	public boolean isRelatedByChoice(Aspect a1, Aspect a2);

	public Map<Relation, Map<String, Set<String>>> getMapOfSpecifiedRules(
			Set<Aspect> aspectInterferenceSet);

}
