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
package de.tud.stg.popart.aspect.extensions.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdviceComparator;
import de.tud.stg.popart.aspect.extensions.definers.CycleFinder;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;

//TODO remove inheritance from PointcutAndAdviceComparator.
/**
 * @author Olga Gusyeva
 */
public class PrecedenceComparator<T extends AspectMember> extends PointcutAndAdviceComparator<T> implements Comparator<T>{

	/**
	 * Maps an aspect name (key) to the set of the names of the aspects that
	 * have lower precedence (values)
	 */
	protected HashMap<String, Set<String>> ruleBasedMap;
	protected String comparatorType;

	protected final int HIGHER_PRECEDENCE = -1;
	protected final int EQUAL_PRECEDENCE = 0;
	protected final int LOWER_PRECEDENCE = 1;
	private final boolean DEBUG = false;

	public PrecedenceComparator() {
		ruleBasedMap = new HashMap<String, Set<String>>();
		comparatorType = "PrecedenceComparator";
	}

	public void addRule(ArrayList<String> listOfAspectNames) {

		if (containsDuplicateEtries(listOfAspectNames)) {
			String message = "There are duplicate entries in your precedence list " + listOfAspectNames + "!";
			throw new IllegalStateException(message);
		} else {
			/**
			 * Set of the names of the aspects with lower precedence that are
			 * already contained in the precedenceMap
			 */
			Set<String> oldSet = new HashSet<String>();

			/**
			 * Set of the names of the aspects with lower precedence to be
			 * inserted into the precedenceMap according to the new precedence
			 * rule defined by precedenceList
			 */
			Set<String> newSet = new HashSet<String>();

			for (int i = 0; i < listOfAspectNames.size(); i++) {

				oldSet = ruleBasedMap.get(listOfAspectNames.get(i));
				newSet = new HashSet<String>(listOfAspectNames.subList(i + 1,
						Math.max(i + 1, listOfAspectNames.size())));

				if ((oldSet != null) && (!oldSet.isEmpty()))
					newSet.addAll(oldSet);

				if (DEBUG) {
					System.out.println("------------");
					System.out.println("i: " + i);
					System.out.println(comparatorType + " list: " + listOfAspectNames);
					System.out.println(comparatorType + " map before: ");
					System.out.println(toString());
					System.out.println("Subset boundaries: from " + (i + 1) + " to " + Math.max(i + 1, listOfAspectNames.size()));
					System.out.println("Old subset for " + listOfAspectNames.get(i) + ": " + oldSet);
					System.out.println("New subset for " + listOfAspectNames.get(i) + ": " + newSet);
				}

				if (!newSet.isEmpty())
					ruleBasedMap.put(listOfAspectNames.get(i), newSet);

				if (DEBUG) {
					System.out.println(comparatorType + " after: ");
					System.out.println(toString());
				}
			}
		}
	}

	/**
	 * @param listOfStrings
	 *            - the list to be checked for duplicate entries
	 * @return true - if the list contains duplicate entries, false - otherwise
	 */
	protected boolean containsDuplicateEtries(ArrayList<String> listOfStrings) {
		Set<String> setOfStrings = new HashSet<String>(listOfStrings);
		return listOfStrings.size() > setOfStrings.size();
	}

	public int compare(T o1, T o2) {
		Aspect a1 = o1.getAspect();
		Aspect a2 = o2.getAspect();
		Set<String> namesOfAspectsWithLowerPrecedence_a1 = ruleBasedMap.get(a1
				.getName());
		Set<String> namesOfAspectsWithLowerPrecedence_a2 = ruleBasedMap.get(a2
				.getName());
		try {
			String message = checkRulesConsistency();
		} catch (RuleInconsistencyException e) {
			System.out.println(e.getMessage());
		}

		if ((namesOfAspectsWithLowerPrecedence_a1 != null)
				&& (namesOfAspectsWithLowerPrecedence_a1.contains(a2.getName()))) {
			if (DEBUG)
				System.out.println(a1.getName() + " contains " + a2.getName());
			return HIGHER_PRECEDENCE;
		} else if ((namesOfAspectsWithLowerPrecedence_a2 != null)
				&& (namesOfAspectsWithLowerPrecedence_a2.contains(a1.getName()))) {
			if (DEBUG)
				System.out.println(a1.getName() + " contains " + a2.getName());
			return LOWER_PRECEDENCE;
		} else {
			if (DEBUG)
				System.out.println(a1.getName() + " vs. " + a2.getName() + " no rule");
			return EQUAL_PRECEDENCE;
		}

	}

	private String checkPrecedenceMapForCycle(
			HashMap<String, Set<String>> ruleBasedMap)
			throws RuleInconsistencyException {
		String message = "";
		Set<String> cycle = CycleFinder.findCycleNodes(ruleBasedMap);

		if (!(cycle.isEmpty())) {
			message = "\nA cyclic relation of PRECEDENCE was detected between the aspects " + cycle;
		}
		if (!(message.isEmpty()))
			throw new RuleInconsistencyException(message);
		return message;
	}
	

	public String checkRulesConsistency()
			throws RuleInconsistencyException {

		return checkPrecedenceMapForCycle(ruleBasedMap);
	}

	/**
	 * @return the string representation of the precedenceMap
	 */
	public String toString() {
		return toString(ruleBasedMap);

	}

	/**
	 * @return the string representation of the precedenceMap
	 */
	protected String toString(HashMap<String, Set<String>> aMap) {
		HashMap<String, Set<String>> aMapString = new HashMap<String, Set<String>>();
		Iterator it = aMap.entrySet().iterator();
		Set subset = new HashSet<String>();
		String map2string = comparatorType + "\n";
		if ((aMap == null) || (aMap.isEmpty())) {
			return map2string = map2string + "[]";
		} else {
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				String a = (String) pairs.getKey();
				HashSet<String> hs = (HashSet<String>) pairs.getValue();
				Iterator itr = hs.iterator();

				while (itr.hasNext()) {
					subset.add(itr.next());
				}
				map2string = map2string + a + ": " + subset + "\n";
				subset = new HashSet<String>();

			}
			map2string = map2string.substring(0, Math.max(0, (map2string
					.length() - 1)));
			return map2string;
		}
	}

	public String getComparatorType() {
		return comparatorType;
	}

	public HashMap<String, Set<String>> getRuleBasedMap() {
		return ruleBasedMap;
	}

}
