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

import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdviceComparator;

/**
 * Defines a partial orders that combines two partial order,
 * such that if the primary partial order does not define a relateion between two objects,
 * the secondary partial order is used for comparing the objects.
 * Note that, the secondary partial order may itself be again a combinator.
 */
public class CascadingCombinatorComparator<T extends AspectMember> extends PointcutAndAdviceComparator<T> {
	
	PointcutAndAdviceComparator<T> primaryComparator;	
	PointcutAndAdviceComparator<T> secondaryComparator;
	
	public CascadingCombinatorComparator(
	  PointcutAndAdviceComparator<T> primaryComparator,	
	  PointcutAndAdviceComparator<T> secondaryComparator) {
		this.primaryComparator = primaryComparator;
		this.secondaryComparator = secondaryComparator;
	}
	
	public int compare(T o1, T o2) {
		int result = primaryComparator.compare(o1,o2);
		if (result != 0) return result;
		else return secondaryComparator.compare(o1,o2);
	}
	
}