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

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdviceComparator;

/**
 * Defines a partial order on <tt>PointcutAndAdvice</tt>.
 * <tt>pa1</tt> &lt; <tt>pa2</tt>, means <tt>pa1</tt> has a higher precedence that <tt>pa2</tt>   
 * <tt>pa1</tt> &gt; <tt>pa2</tt>, means <tt>pa1</tt> has a lower precedence that <tt>pa2</tt>   
 */
public class DomainPointcutAndAdviceComparator<T extends AspectMember> extends PointcutAndAdviceComparator<T> {
		
	/**
	 * Defines a partial order on PointcutAndAdvice.
	 * <tt>key</tt> is the <tt>PointcutAndAdvice</tt> with the higher precedence, the <tt>value</tt> is a set that contains all lower predences for this <tt>PointcutAndAdvice</tt>. 
	 */
	HashMap<T,Set<T>> precedenceMap = new HashMap<T,Set<T>>();
	
	/**
	 * Add a precedence rule that <tt>pa1</tt> precedes <tt>pa2</tt>.
	 * @param pa1 <tt>PointcutAndAdvice</tt> with the higher precedence (will be executed earlier).
	 * @param pa2 <tt>PointcutAndAdvice</tt> with the lower precedence (will be executed later).
	 */
	public void addPrecedenceRule(T pa1,T pa2) {
		Set<T> lowerPrecedences = precedenceMap.get(pa1);
		if (lowerPrecedences == null) {
		   lowerPrecedences = new HashSet<T>();
		   precedenceMap.put(pa1,lowerPrecedences);
		}
		lowerPrecedences.add(pa2);
	}
	
	/**
	 * Looks up a precedence rule that <tt>pa1</tt> precedes <tt>pa2</tt>.
	 * @param pa1 <tt>PointcutAndAdvice</tt> with expected higher precedence (will be executed earlier).
	 * @param pa2 <tt>PointcutAndAdvice</tt> with expected lower precedence (will be executed later).
	 * @return Returns -1 if pa1&lt;pa2, 1 if pa1&gt;pa2, and 0 if no precendence is defined. 
	 */
	public int comparePrecedence(T pa1,T pa2) {
		Set<T> lowerPrecedences = precedenceMap.get(pa1);
		if (lowerPrecedences == null) {
            //There is not precedence rule defined that pa1 < pa2 
			lowerPrecedences = precedenceMap.get(pa2);
			if (lowerPrecedences == null) {
	            //There is not precedence rule defined that pa1 > pa2 
				return 0;
			}
			if (lowerPrecedences.contains(pa1)) return 1;
		}
		if (lowerPrecedences.contains(pa2)) {
            //There is precedence rule defined that pa1 < pa2 
			return -1;
		} else {
            //There is not precedence rule defined that pa1 < pa2 
			lowerPrecedences = precedenceMap.get(pa2);
			if (lowerPrecedences == null) {
	            //There is not precedence rule defined that pa1 > pa2 
				return 0;
			}
			if (lowerPrecedences.contains(pa1)) return 1;
			return 0;
		}
	}
	
	public int compare(T o1, T o2) {
		//Try to find a precedence rules
		int precedence = comparePrecedence(o1,o2);
		if (precedence == 0) return super.compare(o1,o2); //if not precedence is defined return compare precedence of aspects (priority)
		return precedence;
	}
	
	public String toString() {
		String str = super.toString()+"[";
		Iterator<T> it1 = precedenceMap.keySet().iterator();
		while (it1.hasNext()) {
			T pa1 = it1.next();
			Set<T> pas2 = precedenceMap.get(pa1);
			Iterator<T> it2 = pas2.iterator();
			while (it2.hasNext()) {
				T pa2 = it2.next();
				str += pa1+
				    //":"+pa1.getPointcut()+
				    ") < ";
				str += pa2+
  				    //":"+pa2.getPointcut()+
				    "), ";
			}
		}
		return str+"]";
	}
}