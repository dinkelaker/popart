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
package de.tud.stg.popart.aspect.extensions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;
import de.tud.stg.popart.aspect.extensions.definers.Converter;
import de.tud.stg.popart.aspect.extensions.definers.IRelationExaminer;
import de.tud.stg.popart.aspect.extensions.definers.Relation;
import de.tud.stg.popart.aspect.extensions.definers.Verifier;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;
import de.tud.stg.popart.exceptions.UnspecifiedOrderException;
import de.tud.stg.popart.joinpoints.JoinPoint;

/**
 * @author Olga Gusyeva
 */
public class InteractionAwareAspectManager extends OrderedAspectManager {

	protected final int HIGHER_PRECEDENCE = -1;
	protected final int EQUAL_PRECEDENCE = 0;
	protected final int LOWER_PRECEDENCE = 1;
	protected final boolean DEBUG = false;
	protected final boolean SHOW_TASK_TIME = false;

	/**
	 * Contains the join point, aspect interaction set and pointcut and advice
	 * of current interaction
	 */
	private Map<String, Set<Object>> contextMap = new java.util.HashMap<String, Set<Object>>();

	/**
	 * Maps the aspect name to the set of aspect names to which the order was
	 * specified
	 * 
	 */
	private Map<Relation, Map<String, Set<String>>> mapOfSpecifiedRules = new java.util.HashMap<Relation, Map<String, Set<String>>>();

	/**
	 * Maps the aspect name to the set of aspect names to which the order was
	 * not specified
	 * 
	 */
	private Map<String, Set<String>> mapOfNonspecifiedRules = new java.util.HashMap<String, Set<String>>();

	/**
	 * Variables that define the Path to the report file
	 */
	public String targetDirectory = "reports";
	public String targetFile = "reportOnInteractions.txt";

	/**
	 * Variables that can be set externally to define the information that will
	 * be contained in the report file
	 */
	private boolean reportNonspecifiedInteractions = false;
	private boolean reportSpecifiedRules = false;
	private boolean reportAll = false;

	/**
	 * If true - there will be a warning written in the console every time an
	 * aspect interaction takes place
	 */
	private boolean warningOn = true;

	/**
	 * If true - the exception will be thrown every time an aspect interaction
	 * takes place where the ordering between at least two of interacting
	 * aspects was not specified
	 */
	private boolean throwException = false;

	public InteractionAwareAspectManager() {
		super();
		resetMaps();
	}

	public void interactionAtJoinPoint(JoinPoint jp, Set<Aspect> aspects, List<PointcutAndAdvice> applicablePAs) {
		resetMaps();
		reportInteraction(jp, aspects, applicablePAs);
		

		// super.interactionAtJoinPoint(jp, aspectInterferenceSet,
		// applicablePAs);
		try {
			if (DEBUG) {
				for (Aspect a : aspects){
					System.out.println("ASPECT: " + a.getName() + " has PAs: " + a.getPointcutAndAdviceSize());
				}
			}

			IRelationExaminer facade = AspectFactory.createMediator();
			long startTimeMs = 0;
			long taskTimeMsR = 0;
			if (SHOW_TASK_TIME)
				startTimeMs = System.currentTimeMillis();
			facade.checkRulesConsistency(aspects);
			if (SHOW_TASK_TIME) {
				taskTimeMsR = System.currentTimeMillis() - startTimeMs;
				System.out.println("Checking for rules consistensy: " + taskTimeMsR + " ms");
			}

			if (SHOW_TASK_TIME)
				startTimeMs = System.currentTimeMillis();

			applyRules(applicablePAs);
			if (SHOW_TASK_TIME) {
				taskTimeMsR = System.currentTimeMillis() - startTimeMs;
				System.out.println("Rules application: " + taskTimeMsR + " ms");
			}

			super.interactionAtJoinPoint(jp, aspects, applicablePAs);
		} catch (RuleInconsistencyException e) {
			System.out.println(e);
		}
	}

	public void applyRules(List<? extends AspectMember> applicablePAs) throws RuleInconsistencyException {
		if (DEBUG) System.out.println("Applying precedence rules to: " + applicablePAs);
		applyPrecedenceRules(applicablePAs);
		if (DEBUG) System.out.println("Applying exclusion rules to: " + applicablePAs);
		applyExclusionRules(applicablePAs);
		if (DEBUG) System.out.println("Applying dependency rules to: " + applicablePAs);
		applyDependencyRules(applicablePAs);
	}

	private void applyPrecedenceRules(List<? extends AspectMember> aspectMembers) {
		Collections.sort(aspectMembers, AspectFactory.getDefaultComparator());
	}

	public void applyExclusionRules(List<? extends AspectMember> aspectMembers) throws RuleInconsistencyException {
		IRelationExaminer facade = AspectFactory.createMediator();
		Comparator<? super AspectMember> comparator = AspectFactory.getDefaultComparator();

		List<String> aspectsInvolvedInExclusion = new ArrayList<String>(facade.getAspectsInvolvedInExclusion(Converter.convertFromAMToString(aspectMembers)));
		List<AspectMember> aspectMembersInvolvedInExclusion = new ArrayList<AspectMember>();

		for (String aspectName : aspectsInvolvedInExclusion){
			Aspect a = InteractionAwareAspectManager.getInstance().getAspect(aspectName);
			if(a != null) aspectMembersInvolvedInExclusion.addAll(a.getAllAspectMembers());
		}

		for (AspectMember o1 : aspectMembersInvolvedInExclusion){
			Aspect a1 = o1.getAspect();

			for (AspectMember o2 : aspectMembersInvolvedInExclusion){
				Aspect a2 = o2.getAspect();

				boolean mutualExclusion = ((facade.isRelatedByExclusion(a1, a2)) && (facade.isRelatedByExclusion(a2, a1)));
				if (DEBUG) {
					System.out.println("Aspect a1: " + a1.getName());
					System.out.println("Aspect a2: " + a2.getName());
					System.out.println("Checking for mutex betweetn " + a1.getName() + " and " + a2.getName() + ": " + mutualExclusion);
				}
				if (mutualExclusion) {
					switch (comparator.compare(o1, o2)) {

					case HIGHER_PRECEDENCE:

						aspectMembers.remove(o2);
						break;

					case LOWER_PRECEDENCE:

						aspectMembers.remove(o1);
						break;
					// a1, a2 - no precedence, a1, a2 - mutual exclusion ->
					// exception
					case EQUAL_PRECEDENCE:
						String message = "Mutual exclusion of " + a1.getName() + " and " + a2.getName() + ". Precedence needs to be specified!";
						throw new RuleInconsistencyException(message);

					}
				}
				// else if (facade.isRelatedByExclusion(a1, a2)) {
				// applicablePAs.remove(j);
				// } else if (facade.isRelatedByExclusion(a2, a1)) {
				// applicablePAs.remove(i);
				// }
			}

		}

	}

	private <T extends AspectMember> void applyDependencyRules(List<T> aspectMembers) {

		IRelationExaminer facade = AspectFactory.createMediator();
		Comparator<? super AspectMember> comparator = AspectFactory.getDefaultComparator();
		List<T> applicablePAsDependencyApplied = new ArrayList<T>(aspectMembers);

		if (DEBUG) {
			System.out.println("applicablePAs before dependency rules implied: ");
			for (int m = 0; m < applicablePAsDependencyApplied.size(); m++) {
				System.out.println(applicablePAsDependencyApplied.get(m).getAspect().getName());
			}
		}

		List<T> pasInvolvedInDependency = getApplicablePAsInvolvedInDependency(aspectMembers);

		for (T o1 : pasInvolvedInDependency){
			Aspect a1 = o1.getAspect();
			for (T o2 : pasInvolvedInDependency){
				Aspect a2 = o2.getAspect();
				if (DEBUG) {
					System.out.println("applicablePAs BEFORE compare: ");
					for (AspectMember m : applicablePAsDependencyApplied){
						System.out.print(m.getAspect().getName() + " ");
					}
					System.out.println();
				}
				if (!(a1.getName().equals(a2.getName()))) {
					if (facade.isRelatedByDependency(a1, a2)) {
						if (DEBUG) {
							System.out.println("Comparing " + a1.getName() + ", " + a2.getName());
						}

						switch (comparator.compare(o1, o2)) {
						// a1, a2 - no precedence, a1 depends on a2 ->
						// auto-ordering
						// strategy: a2 executes BEFORE a1

						case EQUAL_PRECEDENCE:
							if (DEBUG) {
								System.out.println("Equal precedence between " + a1.getName() + ", " + a2.getName());
							}
							int indexOf_o1 = applicablePAsDependencyApplied
									.indexOf(o1);
							int indexOf_o2 = applicablePAsDependencyApplied
									.indexOf(o2);
							if ((indexOf_o1 != -1) && (indexOf_o2 != -1)) {
								if (indexOf_o1 < indexOf_o2) {
									applicablePAsDependencyApplied
											.remove(indexOf_o1);
									applicablePAsDependencyApplied.add(
											indexOf_o2, o1);

								}

							}
							break;
						}
					}
				}
				if (DEBUG) {
					System.out.println("applicablePAs AFTER compare: ");
					for (AspectMember m : applicablePAsDependencyApplied) {
						System.out.print(m.getAspect().getName() + " ");
					}
					System.out.println();
				}

			}
		}
		aspectMembers.clear();
		aspectMembers.addAll(applicablePAsDependencyApplied);
	}

	private <T extends AspectMember> List<T> getApplicablePAsInvolvedInDependency(List<T> aspectMembers) {
		IRelationExaminer facade = AspectFactory.createMediator();
		List<String> stringPAsInvolvedInDependency = new ArrayList<String>(facade.getAspectsInvolvedInDependency(Converter.convertFromAMToString(aspectMembers)));

		List<String> stringApplicablePAs = new ArrayList<String>(Converter.convertFromAMToString(aspectMembers));

		List<String> stringApplicablePAsInvolvedInDependency = new ArrayList<String>();
		if (stringPAsInvolvedInDependency.size() > stringApplicablePAs.size()) {

			stringPAsInvolvedInDependency.retainAll(stringApplicablePAs);
			stringApplicablePAsInvolvedInDependency = new ArrayList<String>(stringPAsInvolvedInDependency);
		} else {
			stringApplicablePAs.retainAll(stringPAsInvolvedInDependency);
			stringApplicablePAsInvolvedInDependency = new ArrayList<String>(stringApplicablePAs);
		}

		List<AspectMember> aspectMembersInvolvedInDependency = new ArrayList<AspectMember>();

		for (int k = 0; k < stringApplicablePAsInvolvedInDependency.size(); k++) {
			String aspectName = stringApplicablePAsInvolvedInDependency.get(k);
			Aspect a = InteractionAwareAspectManager.getInstance().getAspect(aspectName);
			aspectMembersInvolvedInDependency.addAll(a.getAllAspectMembers());
		}


		aspectMembersInvolvedInDependency.retainAll(aspectMembers);
		/* Sadly, this cannot be done in a type safe manner, but it is a
		 * correct statement since, if an element as not removed, it
		 * was in aspectMembers, which is of type T, so the element itself
		 * must be of type T, so all elements of the list are of type T.
		 * 
		 * Of course, one would have to create a new List<T> and insert all
		 * elements rather than just casting the list, but since generic
		 * types are erased at compile anyways, we can spare the effort here.
		 */
		return (List<T>) aspectMembersInvolvedInDependency;
	}

	/**
	 * Resets the map variables
	 */
	private void resetMaps() {
		contextMap = new java.util.HashMap<String, Set<Object>>();
		mapOfSpecifiedRules = new java.util.HashMap<Relation, Map<String, Set<String>>>();
		mapOfNonspecifiedRules = new java.util.HashMap<String, Set<String>>();
	}

	/**
	 * Writes the information on joinpoint, aspect interference set, applicable
	 * PAs as well as information on specified and unspecified ordering for the
	 * current interaction into the report file
	 * 
	 * @param jp
	 * @param aspectInterferenceSet
	 * @param applicablePAs
	 */

	private void reportInteraction(JoinPoint jp, Set<Aspect> aspectInterferenceSet, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) {
			System.out.println("Class of Comp: " + AspectFactory.getDefaultComparator().getClass());
		}
		createContextMap(jp, aspectInterferenceSet, applicablePAs);
		boolean isPrecComp = AspectFactory.getDefaultComparator() instanceof PrecedenceComparator<?>;
		if (isPrecComp) {
			setMapOfSpecifiedRules(aspectInterferenceSet);
			setMapOfNonspecifiedRules(aspectInterferenceSet);
		}
		if (warningOn)
			warnForInteraction(jp, aspectInterferenceSet, applicablePAs);

		try {

			if ((throwException) && ((!mapOfNonspecifiedRules.isEmpty()))) {
				throw new UnspecifiedOrderException(
						"The execution order specification between following aspects is missing: \n" + mapOfNonspecifiedRules);
			}
			java.util.Date today = new java.util.Date();
			String fileName = targetDirectory + "/" + targetFile;
			File directory = new File(targetDirectory);
			if (!(directory.exists()))
				directory.mkdir();
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName,
					true));

			out.write((new java.sql.Timestamp(today.getTime())).toString() + "\n");
			reportContextMap(out);
			if (isPrecComp) {
				if ((reportSpecifiedRules) || (reportAll)) {
					reportSpecifiedRules(out, aspectInterferenceSet);
				}
				if ((reportNonspecifiedInteractions) || (reportAll)) {
					reportNonspecifiedInteractions(out, aspectInterferenceSet);
				}
			}
			out.write("\n");
			out.close();
		} catch (IOException e) {
			System.err.println("Error while writing to the report file");
		} catch (UnspecifiedOrderException e) {
			System.out.println(e);
		}

	}

	/**
	 * Writes the information on the specified ordering for the current
	 * interaction
	 * 
	 * @param out
	 * @param aspectInterferenceSet
	 * @throws IOException
	 */
	private void reportSpecifiedRules(BufferedWriter out,
			Set<Aspect> aspectInterferenceSet) throws IOException {
		out.write("SPECIFIED RULES: \n");

		for(Entry<Relation, Map<String, Set<String>>> pairs : mapOfSpecifiedRules.entrySet()){
			out.write(pairs.getKey() + ": \n" + pairs.getValue() + "\n");
		}

	}

	/**
	 * Writes the information on the unspecified ordering for the current
	 * interaction
	 * 
	 * @param out
	 * @param aspectInterferenceSet
	 * @throws IOException
	 */
	private void reportNonspecifiedInteractions(BufferedWriter out,
			Set<Aspect> aspectInterferenceSet) throws IOException {

		out.write("NONSPECIFIED RULES: \n");

		for(Map.Entry<String, Set<String>> pairs : mapOfNonspecifiedRules.entrySet()){
			if (!pairs.getValue().isEmpty())
				out.write(pairs.getKey() + ": " + pairs.getValue() + "\n");

		}
	}

	/**
	 * Computes a map of nonspecified rules for the aspect interference set of
	 * current interaction wrt. the precedence map.
	 * 
	 * @param aspectInterferenceSet
	 */

	private void setMapOfNonspecifiedRules(Set<Aspect> aspectInterferenceSet) {
		Set<String> aspectInterferenceSetString = new java.util.HashSet<String>();
		for(Aspect aspect : aspectInterferenceSet){
			aspectInterferenceSetString.add(aspect.getName());
		}
		PrecedenceComparator<?> comparator = (PrecedenceComparator<?>) AspectFactory.getDefaultComparator();
		Map<String, Set<String>> relationMap = comparator.getRuleBasedMap();
		// HashMap<Integer, String> mapOfIndexToAspect = Verifier
		// .getMapOfIndexToAspect(aspectInterferenceSetString);
		mapOfNonspecifiedRules = Verifier.getMapOfNonspecifiedRules(
				relationMap, (Set<String>) (aspectInterferenceSetString),
				aspectInterferenceSetString);
	}

	/**
	 * Computes a map of specified rules for the aspect interference set of
	 * current interaction.
	 * 
	 * @param aspectInterferenceSet
	 */
	private void setMapOfSpecifiedRules(Set<Aspect> aspectInterferenceSet) {
		IRelationExaminer re = AspectFactory.createMediator();
//		HashMap<String, Set<String>> relationMap = new HashMap<String, Set<String>>();

		mapOfSpecifiedRules = re.getMapOfSpecifiedRules(aspectInterferenceSet);

		PrecedenceComparator<?> comparator = (PrecedenceComparator<?>) AspectFactory.getDefaultComparator();
		mapOfSpecifiedRules.put(Relation.PRECEDENCE, comparator.getRuleBasedMap());

		// HashSet<String> aspectInterferenceSetString = new HashSet<String>();
		// Iterator it1 = aspectInterferenceSet.iterator();
		// while (it1.hasNext()) {
		// aspectInterferenceSetString.add(((Aspect) it1.next()).getName());
		//
		// }
		// PrecedenceComparator<PointcutAndAdvice> comparator =
		// (PrecedenceComparator<PointcutAndAdvice>) AspectFactory
		// .getInstance().createPointcutAndAdviceComperator();
		// HashMap<String, Set<String>> relationMap =
		// comparator.getRuleBasedMap();
		// HashMap<Integer, String> mapOfIndexToAspect = Verifier
		// .getMapOfIndexToAspect(aspectInterferenceSetString);
		// mapOfSpecifiedRules = Verifier
		// .getMapOfSpecifiedRules(relationMap,
		// (Set<String>) (aspectInterferenceSetString),
		// mapOfIndexToAspect);
	}

	/**
	 * Creates a map that contains information on the jointpoint, aspect
	 * interaction set and pointcut and advice of current interaction.
	 * 
	 * @param aspectInterferenceSet
	 */
	private void reportContextMap(BufferedWriter out) throws IOException {
		out.write("ASPECT INTERACTION: \n");
		for(Map.Entry<String, Set<Object>> pairs : contextMap.entrySet()){
			out.write(pairs.getKey() + ": " + pairs.getValue() + "\n");
		}
	}

	/**
	 * Writes the information on the jointpoint, aspect interaction set and
	 * pointcut and advice of current interaction into the report file.
	 * 
	 * @param jp
	 * @param aspectInterferenceSet
	 * @param applicablePAs
	 */
	private void createContextMap(JoinPoint jp, Set<Aspect> aspectInterferenceSet, List<PointcutAndAdvice> applicablePAs) {
//		IRelationExaminer mediator = AspectFactory.createMediator();
//		Comparator<? super PointcutAndAdvice> comparator = AspectFactory.getDefaultComparator();
		contextMap.put("JP", new java.util.HashSet<Object>(java.util.Arrays.asList(jp)));
		contextMap.put("AIset", new java.util.HashSet<Object>(aspectInterferenceSet));
		contextMap.put("PAs", new java.util.HashSet<Object>(applicablePAs));
	}

	/**
	 * Writes a warning into the console every time an aspect interaction takes
	 * place
	 * 
	 * @param jp
	 * @param aspectInterferenceSet
	 * @param applicablePAs
	 */

	private void warnForInteraction(JoinPoint jp, Set<Aspect> aspectInterferenceSet, List<PointcutAndAdvice> applicablePAs) {
		System.err.println("AN ASPECT INTERACTION WAS DETECTED:");
		for(Entry<String, Set<Object>> pairs : contextMap.entrySet()){
			System.err.println(pairs.getKey() + ": " + pairs.getValue());
		}

	}

	/**
	 * Getters and Setters
	 * 
	 */
	public void setReportSpecifiedRules(boolean reportSpecifiedRules) {
		this.reportSpecifiedRules = reportSpecifiedRules;
	}

	public void setReportNonspecifiedInteractions(
			boolean reportUnspecifiedInteractions) {
		this.reportNonspecifiedInteractions = reportUnspecifiedInteractions;
	}

	public void setReportAll(boolean reportAll) {
		this.reportAll = reportAll;
		if (reportAll) {
			this.reportSpecifiedRules = true;
			this.reportNonspecifiedInteractions = true;
		}
	}

	public Map<String, Set<Object>> getContextMap() {
		return contextMap;
	}

	public Map<Relation, Map<String, Set<String>>> getMapOfSpecifiedRules() {
		return mapOfSpecifiedRules;
	}

	public Map<String, Set<String>> getMapOfNonspecifiedRules() {
		return mapOfNonspecifiedRules;
	}

	public void setWarningOn(boolean warningOn) {
		this.warningOn = warningOn;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

}
