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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.exceptions.DuplicateEntriesException;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;

public class RelationDefinerFacade implements IRelationDefinerFacade,
		IRelationExaminer {

	private ExclusionDefiner exclusionDefiner;
	private DependencyDefiner dependencyDefiner;
	private ChoiceDefiner choiceDefiner;

	protected final int HIGHER_PRECEDENCE = -1;
	protected final int EQUAL_PRECEDENCE = 0;
	protected final int LOWER_PRECEDENCE = 1;

	private static final boolean DEBUG = false;
	private static final boolean SHOW_TASK_TIME = false;

	public RelationDefinerFacade() {
		exclusionDefiner = new ExclusionDefiner();
		dependencyDefiner = new DependencyDefiner();
		choiceDefiner = new ChoiceDefiner();

	}

	// public RelationDefinerFacade(Comparator _comparator) {
	// exclusionDefiner = new ExclusionDefiner();
	// dependencyDefiner = new DependencyDefiner();
	// choiceDefiner = new ChoiceDefiner();
	//
	// }

	public void addRule_choice(ArrayList<String> listOfAspectNames)
			throws DuplicateEntriesException {
		choiceDefiner.addRelation(listOfAspectNames);

	}

	public void addRule_dependency(HashMap<String, String> params)
			throws DuplicateEntriesException {
		dependencyDefiner.addRelation(params);

	}

	public void addRule_mutex(ArrayList<String> listOfAspectNames)
			throws DuplicateEntriesException {
		exclusionDefiner.addRelation(listOfAspectNames);

	}

	public String toString() {
		String definersToString = "";
		definersToString = exclusionDefiner.toString() + "\n" + dependencyDefiner.toString() + "\n" + choiceDefiner.toString() + "\n";
		// definersToString = definersToString.substring(0, Math.max(0,
		// (definersToString.length() - 1)));
		return definersToString;
	}

	public boolean isRelatedByExclusion(Aspect a1, Aspect a2) {
		return this.exclusionDefiner.isRelated(a1, a2);
	}

	public boolean isRelatedByDependency(Aspect a1, Aspect a2) {
		boolean directRelation = this.dependencyDefiner.isRelated(a1, a2);
		Set<String> setOfInteractingAspects = new HashSet<String>(Arrays
				.asList(a1.getName(), a2.getName()));
		HashSet<ArrayList<String>> relationSet = new HashSet<ArrayList<String>>();
		relationSet = getDependencyTransitivitySet(setOfInteractingAspects);
		ArrayList<String> path = getRelationPath(a1.getName(), a2.getName(),
				relationSet);
		boolean indirectRelation = (path != null);
		if (DEBUG)
			System.out
					.println("Indirect relation: " + a1.getName() + ", " + a2.getName() + ": " + indirectRelation + " path: " + path);
		return ((directRelation) || (indirectRelation));
	}

	public boolean isRelatedByChoice(Aspect a1, Aspect a2) {
		return this.choiceDefiner.isRelated(a1, a2);
	}

	public HashSet<ArrayList<String>> getDependencyTransitivitySet(
			Set<String> setOfInteractingAspects) {
		// if (this.dependencyTransitivitySet == null)
		// this.dependencyTransitivitySet = getNoncummutativeRelationSet(
		// setOfInteractingAspects, dependencyDefiner.relationMap);
		// return this.dependencyTransitivitySet;
		return getNoncummutativeRelationSet(setOfInteractingAspects,
				dependencyDefiner.relationMap);

	}

	public Set<String> getAspectsInvolvedInExclusion(
			Set<String> setOfInteractingAspects) {
		return getAspectsInvolvedInRelation(setOfInteractingAspects,
				exclusionDefiner.relationMap);
	}

	public Set<String> getAspectsInvolvedInChoice(
			Set<String> setOfInteractingAspects) {
		return getAspectsInvolvedInRelation(setOfInteractingAspects,
				choiceDefiner.relationMap);

	}

	public Set<String> getAspectsInvolvedInDependency(
			Set<String> setOfInteractingAspects) {
		return getAspectsInvolvedInRelation(setOfInteractingAspects,
				dependencyDefiner.relationMap);

	}

	private Set<String> getAspectsInvolvedInRelation(
			Set<String> setOfInteractingAspects,
			HashMap<String, Set<String>> relationMap) {
		Iterator it = relationMap.entrySet().iterator();
		Set<String> aspectsInvolvedInRelation = new HashSet<String>();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String a = (String) pairs.getKey();
			HashSet<String> relatedAspects = (HashSet<String>) pairs.getValue();
			aspectsInvolvedInRelation.add(a);
			aspectsInvolvedInRelation.addAll(relatedAspects);
		}
		return aspectsInvolvedInRelation;
	}

	// Optimized
	public void checkRulesConsistency(Set setOfInteractingAspects)
			throws RuleInconsistencyException {

		long startTimeMs = 0;
		long taskTimeMs = 0;

		Set<String> aspectsInvolvedInChoice = getAspectsInvolvedInChoice(setOfInteractingAspects);
		Set<String> aspectsInvolvedInExclusion = getAspectsInvolvedInExclusion(setOfInteractingAspects);
		Set<String> aspectsInvolvedInDependency = getAspectsInvolvedInDependency(setOfInteractingAspects);
		Set<String> interactingAspectsChoiceExlusion = new HashSet<String>();
		Set<String> interactingAspectsDependencyExlusion = new HashSet<String>();
		;
		Set<String> interactingAspectsDependencyChoice = new HashSet<String>();
		;

		boolean checkChoiceExclusion = true;
		boolean checkDependencyChoice = true;
		boolean checkDependencyExclusion = true;
		boolean checkDependencyCycle = true;
		String message = "";

		if (aspectsInvolvedInDependency.isEmpty()) {
			checkDependencyCycle = false;
			checkDependencyExclusion = false;
			checkDependencyChoice = false;
		} else if (aspectsInvolvedInExclusion.isEmpty()) {
			checkDependencyExclusion = false;
			checkChoiceExclusion = false;
		} else if (aspectsInvolvedInChoice.isEmpty()) {
			checkChoiceExclusion = false;
			checkDependencyChoice = false;
		}

		if (checkChoiceExclusion) {
			interactingAspectsChoiceExlusion = getInteractingAspectsAB(
					aspectsInvolvedInChoice, aspectsInvolvedInExclusion);

			if (interactingAspectsChoiceExlusion.isEmpty())
				checkChoiceExclusion = false;
		}
		if (checkDependencyExclusion) {
			interactingAspectsDependencyExlusion = getInteractingAspectsAB(
					aspectsInvolvedInDependency, aspectsInvolvedInExclusion);
			if (interactingAspectsDependencyExlusion.isEmpty())
				checkDependencyExclusion = false;
		}
		if (checkDependencyChoice) {
			interactingAspectsDependencyChoice = getInteractingAspectsAB(
					aspectsInvolvedInDependency, aspectsInvolvedInChoice);
			if (interactingAspectsDependencyChoice.isEmpty())
				checkDependencyChoice = false;
		}

		if (DEBUG) {

			System.out.println("aspectsInvolvedInChoice: " + aspectsInvolvedInChoice);
			System.out.println("aspectsInvolvedInExclusion: " + aspectsInvolvedInExclusion);
			System.out.println("aspectsInvolvedInDependency: " + aspectsInvolvedInDependency);

			System.out.println("interactingAspetsChoiceExlusion: " + interactingAspectsChoiceExlusion);
			System.out.println("interactingAspetsDependencyExlusion: " + interactingAspectsDependencyExlusion);
			System.out.println("interactingAspetsDependencyChoice: " + interactingAspectsDependencyChoice);

			System.out.println("checkDependencyCycle: " + checkDependencyCycle);
			System.out.println("checkChoiceExclusion: " + checkChoiceExclusion);
			System.out.println("checkDependencyChoice: " + checkDependencyChoice);
			System.out.println("checkDependencyExclusion: " + checkDependencyExclusion);

		}

		if (checkDependencyCycle) {
			// startTimeMs = System.currentTimeMillis();
			// message = checkDependencyForCycle(aspectsInvolvedInDependency);
			// taskTimeMs = System.currentTimeMillis() - startTimeMs;
			// System.out.println("checkDependencyCycle: " + taskTimeMs);

			if (SHOW_TASK_TIME)
				startTimeMs = System.currentTimeMillis();
			Set<String> cycle = CycleFinder
					.findCycleNodes(dependencyDefiner.relationMap);

			if (SHOW_TASK_TIME) {
				taskTimeMs = System.currentTimeMillis() - startTimeMs;
				System.out.println("checkDependencyCycle: " + taskTimeMs);
			}

			if (!(cycle.isEmpty())) {
				message = "\nA cyclic relation of DEPENDENCY was detected between the aspects " + cycle;
			}

		}

		if (!(message.isEmpty()))
			throw new RuleInconsistencyException(message);
		if (SHOW_TASK_TIME)
			startTimeMs = System.currentTimeMillis();
		if (checkChoiceExclusion)
			message = checkForMutexChoiceConsistency(interactingAspectsChoiceExlusion);
		if (SHOW_TASK_TIME) {
			taskTimeMs = System.currentTimeMillis() - startTimeMs;
			System.out.println("checkForMutexChoiceConsistency: " + taskTimeMs);
		}
		if (!(message.isEmpty()))
			throw new RuleInconsistencyException(message);
		if (SHOW_TASK_TIME)
			startTimeMs = System.currentTimeMillis();
		if (checkDependencyChoice)
			message = checkDependencyChoiceConsistency(interactingAspectsDependencyChoice);
		if (SHOW_TASK_TIME) {
			taskTimeMs = System.currentTimeMillis() - startTimeMs;
			System.out.println("checkDependencyChoiceConsistency: " + taskTimeMs);
		}
		if (!(message.isEmpty()))
			throw new RuleInconsistencyException(message);
		if (SHOW_TASK_TIME)
			startTimeMs = System.currentTimeMillis();
		if (checkDependencyExclusion)
			message = checkDependencyExclusionConsistency(interactingAspectsDependencyExlusion);
		if (SHOW_TASK_TIME) {
			taskTimeMs = System.currentTimeMillis() - startTimeMs;
			System.out.println("checkDependencyExclusionConsistency: " + taskTimeMs);
		}
		if (!(message.isEmpty()))
			throw new RuleInconsistencyException(message);

	}

	public String checkDependencyExclusionConsistency(
			Set setOfInteractingAspects) {

		Set<String> stringSetOfInteractingAspects = new HashSet<String>(
				setOfInteractingAspects);
		Set stringSetOfInteractingAspects2 = new HashSet<String>(
				stringSetOfInteractingAspects);
		HashSet<ArrayList<String>> dependencyTransitivitySet = new HashSet<ArrayList<String>>();
		dependencyTransitivitySet = getDependencyTransitivitySet(stringSetOfInteractingAspects);
		String message = "";
		Iterator it = stringSetOfInteractingAspects2.iterator();

		while (it.hasNext()) {
			String a1 = ((String) it.next());
			Iterator itr = stringSetOfInteractingAspects.iterator();
			while (itr.hasNext()) {
				String a2 = ((String) itr.next());
				message = message + checkDependencyMutexConsistencyForPair(
								dependencyTransitivitySet, a1, a2);
			}
		}
		return message;

	}

	public String checkDependencyChoiceConsistency(Set setOfInteractingAspects) {
		Set<String> stringSetOfInteractingAspects = new HashSet<String>(
				setOfInteractingAspects);
		Set stringSetOfInteractingAspects2 = new HashSet<String>(
				stringSetOfInteractingAspects);

		HashSet<ArrayList<String>> dependencyTransitivitySet = new HashSet<ArrayList<String>>();

		dependencyTransitivitySet = getDependencyTransitivitySet(stringSetOfInteractingAspects);
		String message = "";
		Iterator it = stringSetOfInteractingAspects2.iterator();

		while (it.hasNext()) {
			String a1 = ((String) it.next());
			Iterator itr = stringSetOfInteractingAspects.iterator();
			while (itr.hasNext()) {
				String a2 = ((String) itr.next());
				message = message + checkDependencyChoiceConsistencyForPair(
								dependencyTransitivitySet, a1, a2);
			}
		}
		return message;

	}

	private Set<String> getInteractingAspectsAB(
			Set<String> aspectsInvolvedInRelationA,
			Set<String> aspectsInvolvedInRelationB) {

		Set<String> aspectsInvolvedInRelationAcopy = new HashSet<String>(
				aspectsInvolvedInRelationA);
		Set<String> aspectsInvolvedInRelationBcopy = new HashSet<String>(
				aspectsInvolvedInRelationB);
		Set<String> interactingAspetsAB = new HashSet<String>();

		if (aspectsInvolvedInRelationAcopy.size() > aspectsInvolvedInRelationBcopy
				.size()) {
			aspectsInvolvedInRelationAcopy
					.retainAll(aspectsInvolvedInRelationBcopy);
			interactingAspetsAB = new HashSet<String>(
					aspectsInvolvedInRelationAcopy);
		} else {
			aspectsInvolvedInRelationBcopy
					.retainAll(aspectsInvolvedInRelationAcopy);
			interactingAspetsAB = new HashSet<String>(
					aspectsInvolvedInRelationBcopy);
		}
		return interactingAspetsAB;
	}

	private String checkDependencyMutexConsistencyForPair(
			HashSet<ArrayList<String>> dependencyTransitivitySet, String a1,
			String a2) {
		String message = "";
		if (!(a1.equals(a2))) {

			String submessage1 = "";
			String submessage2 = "";
			ArrayList<String> dependencyPath = getRelationPath(a1, a2,
					dependencyTransitivitySet);
			if (DEBUG) {
				System.out.println("Checking dep. path between: " + a1 + ", " + a2);
				System.out.println("dependencyPath: " + dependencyPath);
			}
			if ((dependencyPath != null)
					&& (exclusionDefiner.isRelated(a1, a2))) {
				message = "Both MUTEX and DEPENDENCY relations were defined on aspects " + a1 + " and " + a2;
				message = "\nCause: \n";

				if (dependencyPath.size() > 2)
					submessage1 = "INDIRECT DEPENDENCY of " + a1 + " and " + a2 + ":" + dependencyPath;
				else
					submessage1 = "DEPENDENCY of " + a1 + " and " + a2;
				submessage2 = "MUTEX of " + a1 + " and " + a2;
				message = message + submessage1 + "\n" + submessage2 + "\n";
			}

		}
		return message;
	}

	private String checkDependencyChoiceConsistencyForPair(
			HashSet<ArrayList<String>> dependencyTransitivitySet, String a1,
			String a2) {
		String message = "";
		String submessage1 = "";
		String submessage2 = "";
		ArrayList<String> dependencyPath = getRelationPath(a1, a2,
				dependencyTransitivitySet);

		if ((dependencyPath != null) && (choiceDefiner.isRelated(a1, a2))) {
			message = "Both INDEPENDENCY and DEPENDENCY relations were defined on aspects " + a1 + " and " + a2;
			message = "\nCause: \n";
			if (dependencyPath.size() > 2)
				submessage1 = "INDIRECT DEPENDENCY of " + a1 + " and " + a2 + ":" + dependencyPath;
			else
				submessage1 = "DEPENDENCY of " + a1 + " and " + a2;
			submessage2 = "INDEPENDENCY of " + a1 + " and " + a2;
			message = message + submessage1 + "\n" + submessage2 + "\n";
		}
		return message;
	}

	private String checkForMutexChoiceConsistency(Set setOfInteractingAspects) {

		String message = "";
		Set copyOfSetOfInteractingAspects = new HashSet(setOfInteractingAspects);
		Iterator it = setOfInteractingAspects.iterator();
		while (it.hasNext()) {
			String a1 = ((String) it.next());
			Iterator itr = copyOfSetOfInteractingAspects.iterator();
			while (itr.hasNext()) {
				String a2 = ((String) itr.next());

				if ((choiceDefiner.isRelated(a1, a2))
						&& ((exclusionDefiner.isRelated(a1, a2)))) {
					message = "Both MUTEX and INDEPENDENCY relations were defined on aspects " + a1 + " and " + a2;
				}

			}
		}
		return message;
	}

	private ArrayList<String> getRelationPath(String a1, String a2,
			HashSet<ArrayList<String>> transitivitySet) {
		Iterator it = transitivitySet.iterator();
		while (it.hasNext()) {
			ArrayList<String> pathFromAToB = (ArrayList<String>) it.next();
			String fromA = pathFromAToB.get(0);
			String toB = pathFromAToB.get(pathFromAToB.size() - 1);
			if ((fromA.equals(a1)) && (toB.equals(a2)))
				return pathFromAToB;

		}
		return null;
	}

	private HashSet<ArrayList<String>> getNoncummutativeRelationSet(
			Set setOfInteractingAspects,
			HashMap<String, Set<String>> relationMap) {
		HashSet<ArrayList<String>> transitiveDependencyRelationSet = new HashSet<ArrayList<String>>();

		Iterator it = setOfInteractingAspects.iterator();
		while (it.hasNext()) {
			String fromA = ((String) it.next());
			HashSet<String> setOfUnrelatedAspects = new HashSet<String>(
					setOfInteractingAspects);
			setOfUnrelatedAspects.remove(fromA);
			Iterator itr = setOfUnrelatedAspects.iterator();
			while (itr.hasNext()) {
				String toB = ((String) itr.next());
				ArrayList<String> path = PathFinder.findPath(fromA, toB,
						relationMap);

				if ((path != null) && (path.size() > 1))
					transitiveDependencyRelationSet.add(path);
			}

		}
		return transitiveDependencyRelationSet;

	}

	public Map<Relation, Map<String, Set<String>>> getMapOfSpecifiedRules(
			Set<Aspect> aspectInterferenceSet) {
		Map<Relation, Map<String, Set<String>>> mapOfSpecifiedRules = new HashMap<Relation, Map<String, Set<String>>>();
		HashMap<String, Set<String>> relationMap = new HashMap<String, Set<String>>();
		relationMap = getRelationMapForInteractingAspects(Relation.DEPENDENCY,
				aspectInterferenceSet);
		if (!(relationMap.isEmpty()))
			mapOfSpecifiedRules.put(Relation.DEPENDENCY, relationMap);

		relationMap = getRelationMapForInteractingAspects(Relation.MUTEX,
				aspectInterferenceSet);
		if (!(relationMap.isEmpty()))
			mapOfSpecifiedRules.put(Relation.MUTEX, relationMap);
		relationMap = getRelationMapForInteractingAspects(
				Relation.INDEPENDENCY, aspectInterferenceSet);
		if (!(relationMap.isEmpty()))
			mapOfSpecifiedRules.put(Relation.INDEPENDENCY, relationMap);
		return mapOfSpecifiedRules;
	}

	private HashMap<String, Set<String>> getRelationMapForInteractingAspects(
			Relation relationKind, Set setOfInteractingAspects) {
		HashMap<String, Set<String>> relationMap = new HashMap<String, Set<String>>();
		if (relationKind == Relation.DEPENDENCY)
			relationMap = dependencyDefiner.relationMap;
		if (relationKind == Relation.MUTEX)
			relationMap = exclusionDefiner.relationMap;
		if (relationKind == Relation.INDEPENDENCY)
			relationMap = choiceDefiner.relationMap;

		return getRelationMapForInteractingAspects(relationMap,
				setOfInteractingAspects);
	}

	private HashMap<String, Set<String>> getRelationMapForInteractingAspects(
			HashMap<String, Set<String>> relationMap,
			Set setOfInteractingAspects) {
		HashMap<String, Set<String>> newRelationMap = new HashMap<String, Set<String>>();

		Iterator it = relationMap.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String keyAspect = (String) pairs.getKey();
			HashSet<String> relatedAspects = (HashSet<String>) pairs.getValue();
			HashSet<String> retainSet = new HashSet<String>();
			if (relatedAspects.size() > setOfInteractingAspects.size()) {
				retainSet = new HashSet<String>(relatedAspects);
				retainSet.retainAll(setOfInteractingAspects);

			} else {
				retainSet = new HashSet<String>(Converter
						.convertFromAspectToString(setOfInteractingAspects));
				retainSet.retainAll(relatedAspects);
			}

			if ((!(retainSet.isEmpty()))
					|| (Converter
							.convertFromAspectToString(setOfInteractingAspects)
							.contains(keyAspect))) {
				newRelationMap.put(keyAspect, relatedAspects);
			}
		}

		return newRelationMap;
	}
}
