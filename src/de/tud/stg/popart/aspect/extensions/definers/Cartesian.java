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
import java.util.Set;

public class Cartesian {

	@SuppressWarnings("unchecked")
	public static HashMap product(Set setA, Set setB) {
		HashMap<Object, Set> product = new HashMap<Object, Set>();
		product.putAll(productAxB(setA, setB));
		product.putAll(productAxB(setB, setA));
		return product;

	}

	@SuppressWarnings("unchecked")
	private static HashMap productAxB(Set setA, Set setB) {
		HashMap<Object, Set> productAxB = new HashMap<Object, Set>();
		
		
		ArrayList listA = new ArrayList(setA);
		ArrayList listB = new ArrayList(setB);
		Set setForA = new HashSet();
		for (int i = 0; i < listA.size(); i++) {
			Object a = listA.get(i);
			for (int j = 0; j < listB.size(); j++) {
				Object b = listB.get(j);
				setForA = productAxB.get(a);
				if ((setForA != null) && (!b.equals(a))) {
					setForA.remove(a);
					setForA.add(b);
					productAxB.put(a, setForA);

				} else if (!b.equals(a)) {
					productAxB.put(a, new HashSet(Arrays.asList(b)));

				}
			}
		}
		return productAxB;
	}

}
