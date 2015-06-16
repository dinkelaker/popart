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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PathFinder {
	private static final boolean DEBUG = false;

	private static ArrayList<String> findPath(String fromA, String toB,
			HashMap<String, Set<String>> relationMap,
			ArrayList<String> partialPath) {
		ArrayList<String> path = new ArrayList<String>();
		partialPath.add(fromA);
		if (relationMap.get(fromA) == null) {
			partialPath = new ArrayList<String>();
			return null;
		} else {
			HashSet<String> setOfValues = new HashSet<String>(relationMap
					.get(fromA));

			if (setOfValues.contains(toB)) {
				partialPath.add(toB);
				return partialPath;
			} else {

				Iterator it = setOfValues.iterator();
				while (it.hasNext()) {
					String node = (String) it.next();
					path = findPath(node, toB, relationMap, partialPath);
					if (DEBUG) {
						System.out.println("node: " + node);
						System.out.println("toB: " + toB);
						System.out.println("path: " + path);
					}

					if ((path == null) || (path.size() < 2))
						partialPath.remove(node);
					else
						return partialPath;

				}
				return partialPath;
			}
		}

	}

	public static ArrayList<String> findPath(String fromA, String toB,
			HashMap<String, Set<String>> relationMap) {
		return findPath(fromA, toB, relationMap, new ArrayList<String>());
	}

}
