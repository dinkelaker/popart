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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.exceptions.DuplicateEntriesException;

public abstract class ARelationDefiner implements IRelationDefiner {
	HashMap<String, Set<String>> relationMap = new HashMap<String, Set<String>>();
	String definerType;

	public ARelationDefiner(String definerType){
		this.definerType = definerType;
	}
	
	public void addRelation(HashMap params) throws DuplicateEntriesException {

	}

	public void addRelation(ArrayList<String> listOfAspectNames)
			throws DuplicateEntriesException {

	}

	void addNoncommutativeRelation(HashMap params)
			throws DuplicateEntriesException {

		String from = (params.get("from")).toString();
		if (params.keySet().contains("to")) {
			String to = (params.get("to")).toString();
			ArrayList<String> toListOfAspectNames = new ArrayList<String>(
					Arrays.asList(to));
			addNoncommutativeRelation(from, toListOfAspectNames);
		} else if (params.keySet().contains("toArray")) {
			ArrayList<String> toListOfAspectNames = (ArrayList<String>) (params
					.get("toArray"));
			addNoncommutativeRelation(from, toListOfAspectNames);

		}

	}

	public HashMap<String, Set<String>> getRelationMap() {
		return relationMap;
	}

	public String getDefinerType() {
		return definerType;
	}

	void addNoncommutativeRelation(String aspectName,
			ArrayList<String> listOfAspectNames)
			throws DuplicateEntriesException {

		checkForInputParametersConsistency(aspectName, listOfAspectNames);
		Set<String> relatedAspects = relationMap.get(aspectName);

		if (relatedAspects != null) {
			relatedAspects.addAll(new HashSet<String>(listOfAspectNames));
			relationMap.put(aspectName, relatedAspects);

		} else
			relationMap.put(aspectName, new HashSet<String>(listOfAspectNames));

	}

	void addCommutativeRelation(ArrayList<String> listOfAspectNames)
			throws DuplicateEntriesException {
		checkForInputParametersConsistency(listOfAspectNames);
		HashMap<String, Set<String>> newRelationMap = new HashMap<String, Set<String>>();
		newRelationMap = Cartesian.product(new HashSet<String>(
				listOfAspectNames), new HashSet<String>(listOfAspectNames));
		if (relationMap.isEmpty()) {
			relationMap.putAll(newRelationMap);
		} else {
			Iterator it = newRelationMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				String referenceAspect = (String) pairs.getKey();
				Set<String> newSetOfRelatedAspects = (Set<String>) pairs
						.getValue();
				Set<String> oldSetOfRelatedAspects = relationMap
						.get(referenceAspect);
				if (oldSetOfRelatedAspects != null)
					newSetOfRelatedAspects.addAll(oldSetOfRelatedAspects);
				relationMap.put(referenceAspect, newSetOfRelatedAspects);
			}
		}
	}

	public boolean isRelated(Aspect a1, Aspect a2) {
		return isRelated(a1.getName(), a2.getName());

	}

	boolean isRelated(String a1, String a2) {

		Set<String> namesOfAspectsRelatedTo_a1 = relationMap.get(a1);
		return ((namesOfAspectsRelatedTo_a1 != null) && (namesOfAspectsRelatedTo_a1
				.contains(a2)));

	}

	public String toString() {
		String map2string = definerType + "\n";
		if ((relationMap == null) || (relationMap.isEmpty()))
			map2string = map2string + "[]";
		else if (relationMap != null) {
			HashMap<String, Set<String>> relationMapString = new HashMap<String, Set<String>>();

			Iterator it = relationMap.entrySet().iterator();
			Set subset = new HashSet<String>();
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
		}

		return map2string;
	}

	private ArrayList<String> turnStringIntoArray(String s) {
		ArrayList<String> list = new ArrayList<String>();
		String coma;
		int comaIndex = 0;
		boolean isComa = false;
		int i = 0;
		int j = 0;
		String aspectName = "";

		s = s.substring(1, s.length() - 1);
		s = s.replaceAll(" ", "");

		while (i < s.length()) {
			while (((!isComa) && (j < s.length()))) {
				coma = Character.toString(s.charAt(j));
				isComa = coma.equals(",");
				comaIndex = j;
				j++;
			}
			if (comaIndex < s.length() - 1)
				aspectName = s.substring(i, comaIndex);

			else
				aspectName = s.substring(i);
			list.add(aspectName);

			isComa = false;
			i = comaIndex + 1;
			j = i;
		}
		return list;
	}

	void checkForInputParametersConsistency(String aspectName,
			ArrayList<String> listOfAspectNames)
			throws DuplicateEntriesException {
		Set<String> setOfAspectNames = new HashSet<String>(listOfAspectNames);
		checkForInputParametersConsistency(listOfAspectNames);
		if (listOfAspectNames.contains(aspectName)) {

			String message = "The aspect " + aspectName + " can't be contained in the relation list " + listOfAspectNames;
			throw new DuplicateEntriesException(message);
		}
	}

	void checkForInputParametersConsistency(ArrayList<String> listOfAspectNames)
			throws DuplicateEntriesException {
		Set<String> setOfAspectNames = new HashSet<String>(listOfAspectNames);
		if (listOfAspectNames.size() > setOfAspectNames.size()) {
			String message = "" + listOfAspectNames;
			throw new DuplicateEntriesException(message);
		}
	}

}
