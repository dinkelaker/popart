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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.joinpoints.JoinPoint;

/**
 * An example application-specific <tt>AspectManager</tt> that defines advice precedence rules.
 */
class OrderedAspectManager extends AspectManager {
	
	protected static boolean INTERNAL_DEBUG = false;
	
	public OrderedAspectManager() {
		super();
	}
	
	public void interactionAtJoinPoint(JoinPoint jp, Set<Aspect> aspects, List<PointcutAndAdvice> applicablePAs) {
		super. interactionAtJoinPoint(jp,aspects, applicablePAs);   	
		
		if (DEBUG || INTERNAL_DEBUG) {
			System.err.print("OrderedAspectManager.interactionAtJoinPoint: \t\t intercating aspects= {");
			for(Object o : aspects){
				Aspect aspect = (Aspect)o;
				System.err.print(aspect.getName()+"("+aspect.getPriority()+", ");
			}
			System.err.println("}");
		}
		//if (DEBUG || INTERNAL_DEBUG) println "COMPARATOR : "+AspectFactory.getDefaultComparator();
		Collections.sort(applicablePAs,AspectFactory.getDefaultComparator());
		
		if (DEBUG || INTERNAL_DEBUG) {
			System.err.print("OrderedAspectManager.interactionAtJoinPoint: \t\t sorted= {");
			for(Object o : aspects){
				Aspect aspect = (Aspect)o;
				System.err.print(aspect.getName()+"("+aspect.getPriority()+", ");
			}
			System.err.println("}");
		}
	}
		
	/**
	 * An application-level dynamic exclusion constraint.
	 */
	public Object invokeAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		if (DEBUG || INTERNAL_DEBUG) {
			System.err.print("OrderedAspectManager.invokeAdvice: \t executing advice: "+pa); //aspect = $aspect index=${aspect.getPointcutAndAdviceIndex(pa)}";
			ArrayList argList = (ArrayList)jp.context.get("args");
			System.err.print("\t args = "+java.util.Arrays.toString((Object[])argList.toArray()));
			System.err.print("\t executing advice ...");
		}
		
		Object result = super.invokeAdvice(jp, pa);
		
		if (DEBUG || INTERNAL_DEBUG) System.err.println("\t ... result = "+result);
		return result;
	}
}