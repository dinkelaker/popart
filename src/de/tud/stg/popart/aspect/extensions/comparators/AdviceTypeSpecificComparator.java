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
import de.tud.stg.popart.aspect.BeforePointcutAndAdvice;
import de.tud.stg.popart.aspect.AroundPointcutAndAdvice;
import de.tud.stg.popart.aspect.AfterPointcutAndAdvice;
import de.tud.stg.popart.aspect.PointcutAndAdviceComparator;

/**
 * Defines different partial orders for before, after and around advice, or resp. for subtypes of <tt>PointcutAndAdvice</tt>.
 */
public class AdviceTypeSpecificComparator<T extends AspectMember> extends CombinatorComparator<T> {

	PointcutAndAdviceComparator<T> beforeComparator;	
	PointcutAndAdviceComparator<T> aroundComparator;	
	PointcutAndAdviceComparator<T> afterComparator;	

	public AdviceTypeSpecificComparator(
			PointcutAndAdviceComparator<T> beforeComparator,	
			PointcutAndAdviceComparator<T> aroundComparator,	
			PointcutAndAdviceComparator<T> afterComparator) {
		this.beforeComparator = beforeComparator;
		this.aroundComparator = aroundComparator;
		this.afterComparator = afterComparator;
	}

	public int compare(T o1, T o2) {
		if (o1 instanceof BeforePointcutAndAdvice) return beforeComparator.compare(o1,o2);
		if (o1 instanceof AroundPointcutAndAdvice) return aroundComparator.compare(o1,o2);
		if (o1 instanceof AfterPointcutAndAdvice) return afterComparator.compare(o1,o2);
		throw new RuntimeException("Illegal Advice Type o1="+o1+" type="+o1.getClass());
	}

	public String toString() {
		String str = super.toString()+"[";
		str += "before="+beforeComparator.toString()+", ";
		str += "around="+aroundComparator.toString()+", ";
		str += "after="+afterComparator.toString();
		return str+"]";
	}
}