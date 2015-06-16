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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Verifier {
	/**
	 * Creates a map that maps an integer (i.e., a unique index) to an aspect
	 * name
	 * 
	 * @param setOfAspects
	 * @return
	 */
	public static HashMap<Integer, String> getMapOfIndexToAspect(
			Set<String> setOfAspects) {
		HashMap<Integer, String> mapOfIndexToAspect = new HashMap<Integer, String>();
		Integer index = 0;

		for (String s : setOfAspects) {
			mapOfIndexToAspect.put(index, s);
			index++;
		}
		return mapOfIndexToAspect;
	}

	/**
	 * Computes a map of the rules specified by the relationMap mapping an
	 * aspect name to the set of aspect names to which a relation was specified
	 * 
	 * @param relationMap
	 * @param setOfAspects
	 * @param mapOfIndexToAspect
	 * @return
	 */
	public static HashMap<String, Set<String>> getMapOfSpecifiedRules(
			HashMap<String, Set<String>> relationMap, Set<String> setOfAspects,
			HashMap<Integer, String> mapOfIndexToAspect) {
		return getMapOfRules(mapOfIndexToAspect, setOfAspects,
				getSpecifiedRulesMatrix(relationMap, setOfAspects,
						mapOfIndexToAspect));
	}

	/**
	 * Computes a map of an aspect name to the set of aspect names to which
	 * there is no relation specified by relationMap
	 * 
	 * @param relationMap
	 * @param setOfAspects
	 * @param mapOfIndexToAspect
	 * @return
	 */

	public static HashMap<String, Set<String>> getMapOfNonspecifiedRules(
			Map<String, Set<String>> relationMap, Set<String> setOfAspects, Set aspectInterferenceSetString) {
		HashMap<Integer, String> mapOfIndexToAspect = getMapOfIndexToAspect(aspectInterferenceSetString);
		return getMapOfRules(mapOfIndexToAspect, setOfAspects,
				getNonspecifiedRulesMatrix(relationMap, setOfAspects,
						mapOfIndexToAspect));
	}

	/**
	 * Verifies whether two relation maps define the same set of relations
	 * between the aspect names
	 * 
	 * @param relationMap1
	 * @param relationMap2
	 * @return
	 */
	public static boolean isEqual(HashMap<String, Set<String>> relationMap1,
			HashMap<String, Set<String>> relationMap2) {
		if (!(getBaseSet(relationMap1).equals(getBaseSet(relationMap2))))
			return false;

		else {
			HashSet<String> baseSet = getBaseSet(relationMap1);
			HashMap<Integer, String> mapOfIndexToAspect = getMapOfIndexToAspect(baseSet);
			HashMap<String, Set<String>> mapOfSpecifiedRules1 = getMapOfSpecifiedRules(
					relationMap1, baseSet, mapOfIndexToAspect);
			HashMap<String, Set<String>> mapOfSpecifiedRules2 = getMapOfSpecifiedRules(
					relationMap2, baseSet, mapOfIndexToAspect);
			if (mapOfSpecifiedRules1.equals(mapOfSpecifiedRules2))
				return true;
			else
				return false;
		}
	}

	/**
	 * Computes the set of aspect name used in relationMap
	 * 
	 * @param relationMap
	 * @return
	 */
	private static HashSet<String> getBaseSet(
			HashMap<String, Set<String>> relationMap) {
		HashSet<String> baseSet = new HashSet<String>();
		Iterator it = relationMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			baseSet.add((String) pairs.getKey());
			baseSet.addAll((HashSet<String>) (pairs.getValue()));
		}
		return baseSet;

	}

	/**
	 * Creates a square identity matrix with its size equals the size of the
	 * setOfAspects
	 * 
	 * @param setOfAspects
	 * @return
	 */
	private static int[][] createIdentityMatrix(Set<String> setOfAspects) {
		int maxIndex = setOfAspects.size();
		int[][] identityMatrix = new int[maxIndex][maxIndex];
		for (int i = 0; i < maxIndex; i++) {
			for (int j = 0; j < maxIndex; j++)
				identityMatrix[i][j] = 1;
		}
		return identityMatrix;
	}

	/**
	 * Creates a square 0/1 matrix of specified rules. Hereby is Aij = 1: there
	 * is a relation specified by relationMap between the aspect names mapped to
	 * the indexes i and j by mapOfIndexToAspect Aij = 0: otherwise
	 * 
	 * @param relationMap
	 * @param setOfAspects
	 * @param mapOfIndexToAspect
	 * @return
	 */
	private static int[][] getSpecifiedRulesMatrix(
			Map<String, Set<String>> relationMap, Set<String> setOfAspects,
			Map<Integer, String> mapOfIndexToAspect) {
		int maxIndex = setOfAspects.size();
		int[][] specifiedRulesMatrix = new int[maxIndex][maxIndex];
		for (int i = 0; i < maxIndex; i++) {
			for (int j = 0; j < maxIndex; j++) {
				if (isRelated(mapOfIndexToAspect.get(i), mapOfIndexToAspect
						.get(j), relationMap))
					specifiedRulesMatrix[i][j] = 1;
			}
		}
		return specifiedRulesMatrix;
	}

	/**
	 * Creates a square 0/1 matrix of non-specified rules. Hereby is Aij = 1:
	 * there is NO relation specified by relationMap between the aspect names
	 * mapped to the indexes i and j by mapOfIndexToAspect Aij = 0: otherwise Is
	 * computed as a difference between the identity matrix and the specified
	 * rules matrix
	 * 
	 * @param relationMap
	 * @param setOfAspects
	 * @param mapOfIndexToAspect
	 * @return
	 */
	private static int[][] getNonspecifiedRulesMatrix(
			Map<String, Set<String>> relationMap, Set<String> setOfAspects,
			Map<Integer, String> mapOfIndexToAspect) {
		int maxIndex = setOfAspects.size();
		int[][] nonspesifiedRulesMatrix = new int[maxIndex][maxIndex];
		int[][] identityMatrix = createIdentityMatrix(setOfAspects);
		int[][] specifiedRulesMatrix = getSpecifiedRulesMatrix(relationMap,
				setOfAspects, mapOfIndexToAspect);
		for (int i = 0; i < maxIndex; i++) {
			for (int j = 0; j < maxIndex; j++) {
				nonspesifiedRulesMatrix[i][j] = identityMatrix[i][j]
						- specifiedRulesMatrix[i][j];
			}
		}
		return nonspesifiedRulesMatrix;
	}

	/**
	 * Determines whether relationMap specifies a relation between the aspect
	 * names a1 and a2
	 * 
	 * @param a1
	 * @param a2
	 * @param relationMap
	 * @return
	 */
	private static boolean isRelated(String a1, String a2,
			Map<String, Set<String>> relationMap) {

		return (((relationMap.get(a1) != null) && (relationMap.get(a1)
				.contains(a2))) || ((relationMap.get(a2) != null) && (relationMap
				.get(a2).contains(a1))));

	}

	/**
	 * Computes a map of aspect names defined by a square 0/1 rulesMatrix wrt.
	 * to the mapOfIndexToAspect that maps an integer (i.e., unique index) to an
	 * aspect name
	 * 
	 * @param mapOfIndexToAspect
	 * @param setOfAspects
	 * @param rulesMatrix
	 * @return
	 */
	private static HashMap<String, Set<String>> getMapOfRules(
			HashMap<Integer, String> mapOfIndexToAspect,
			Set<String> setOfAspects, int[][] rulesMatrix) {
		int maxIndex = setOfAspects.size();
		HashMap<String, Set<String>> mapOfRules = new HashMap<String, Set<String>>();
		for (int i = 0; i < maxIndex; i++) {
			for (int j = 0; j < i; j++) {
				if (rulesMatrix[i][j] == 1) {
					if (mapOfRules.get(mapOfIndexToAspect.get(i)) != null)
						mapOfRules.get(mapOfIndexToAspect.get(i)).add(
								mapOfIndexToAspect.get(j));
					else
						mapOfRules.put(mapOfIndexToAspect.get(i),
								new HashSet<String>(Arrays
										.asList(mapOfIndexToAspect.get(j))));
				}
			}
		}
		return mapOfRules;
	}

}
