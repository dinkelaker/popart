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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.exceptions.RuleInconsistencyException;

public class CycleFinder {


	/**
	 * @param am
	 *            a adjacency matrix of a (directed) graph must be square
	 */
	public static Set findCycleNodes(HashMap relationMap) {
		boolean[][] am = computeAdjacencyMatrix(relationMap);
		Set setOfCycleNodesIndexes = findCycleNodesIndexes(am);
		Set setOfCycleNodes = getNodesFromIndexes(setOfCycleNodesIndexes,
				relationMap);

		return setOfCycleNodes;
	}

	private static boolean[][] computeAdjacencyMatrix(
			HashMap<String, Set<String>> relationMap) {
		// find the number of the elements of the set "keys and entries"
		Set<String> setOfDifferentElements = findDifferentElements(relationMap);
		int n = setOfDifferentElements.size();

		// initialize adjacency matrix
		boolean[][] am = new boolean[n][n];
		HashMap keyToIndexMap = assignIndexesToElements(relationMap);

		// fill the adjacency matrix
		int i = 0;
		Integer j = 0;
		Iterator ir = relationMap.entrySet().iterator();
		Set elementSet = new HashSet<String>();
		while (ir.hasNext()) {
			Map.Entry pairs = (Map.Entry) ir.next();
			String a = (String) pairs.getKey();
			i = (Integer) keyToIndexMap.get(a);
			HashSet<String> hs = (HashSet<String>) pairs.getValue();
			Iterator itr = hs.iterator();

			while (itr.hasNext()) {
				String s = (String) itr.next();
				j = (Integer) keyToIndexMap.get(s);
				am[i][j] = true;
			}

		}
		return am;

	}

	private static Set findCycleNodesIndexes(boolean[][] am) {
		Set setOfCycleNodesIndexes = new HashSet();
		int n = am.length; // n x n - matrix
		int[] wrow = new int[n]; // weights of rows
		int[] wcol = new int[n]; // weigths of columns
		boolean done = false;

		// initialize weights
		for (int i = 0; i < n; i++) {
			boolean[] row = am[i];
			for (int j = 0; j < n; j++) {
				if (row[j]) {
					wrow[i]++;
					wcol[j]++;

				}
			}
		}
		// eliminate successively nodes with zero-weight rows or cols since
		// nodes without outgoing or without incoming edges cannot be part of a
		// cycle
		 while (!done) {
			done = true;
			for (int i = 0; i < n; i++) {
				if (wrow[i] == 0) {
					if (wcol[i] > 0) {
						for (int j = 0; j < n; j++) {
							if (am[j][i])
								wrow[j]--;
						}
						done = false;
					}
					wcol[i] = wrow[i] = -1; // -1 => eliminated
				} else if (wcol[i] == 0) {
					for (int j = 0; j < n; j++) {
						if (am[i][j])
							wcol[j]--;
					}
					done = false;
					wcol[i] = wrow[i] = -1; // -1 => eliminated
				}
			}
		}
		// System.out.println("Finally: ");
		// System.out.println("WRow: " + showArray(wrow));
		// System.out.println("WCol: " + showArray(wcol));
		// boolean isCyclic=false;
		for (int i = 0; i < n; i++)
			if (wcol[i] > 0)
				setOfCycleNodesIndexes.add(i);
		return setOfCycleNodesIndexes;
	}

	private static Set getNodesFromIndexes(Set setOfCycleNodesIndexes,
			HashMap relationMap) {
		HashMap elementToIndexMap = assignIndexesToElements(relationMap);
		Set setOfCycleNodes = new HashSet();
		int findElementAt = 0;
		Iterator it = setOfCycleNodesIndexes.iterator();
		while (it.hasNext()) {
			Iterator ir = elementToIndexMap.keySet().iterator();
			String s = "";
			findElementAt = (Integer) it.next();
			for (int i = 0; ((i <= findElementAt) && (ir.hasNext())); i++) {
				s = (String) ir.next();
			}
			setOfCycleNodes.add(s);
		}
		return setOfCycleNodes;

	}

	private static HashMap assignIndexesToElements(
			HashMap<String, Set<String>> relationMap) {
		Set<String> setOfDifferentElements = findDifferentElements(relationMap);

		// assign indexes to each element

		Iterator it = setOfDifferentElements.iterator();
		HashMap keyToIndexMap = new HashMap();
		for (int m = 0; it.hasNext(); m++)
			keyToIndexMap.put(it.next(), m);
		
		return keyToIndexMap;
	}

	private static Set<String> findDifferentElements(
			HashMap<String, Set<String>> relationMap) {
		Iterator it = relationMap.entrySet().iterator();
		Set elementSet = new HashSet<String>();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String a = (String) pairs.getKey();
			elementSet.add(a);
			HashSet<String> hs = (HashSet<String>) pairs.getValue();
			Iterator itr = hs.iterator();

			while (itr.hasNext()) {
				elementSet.add(itr.next());
			}
		}
		return elementSet;
	}

}
