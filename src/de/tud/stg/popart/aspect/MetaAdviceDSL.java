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
package de.tud.stg.popart.aspect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.lang.Signature;

import de.tud.stg.popart.dslsupport.*;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.MetaCallJoinPoint;

/**
 * This class defines an AdviceDSL for manipulating execution semantics of all aspects.
 * @author Tom Dinkelaker
 */
public class MetaAdviceDSL extends ProceedingAdviceDSL {

	private static final boolean DEBUG = false; 

	public static Interpreter getInterpreter(HashMap context) {
		return DSLCreator.getInterpreter(new MetaAdviceDSL(),context);
	}
	
	/** Retrieves the current join point. */
	private MetaCallJoinPoint getJoinPoint() {
		JoinPoint jp = (JoinPoint) getContext().get("thisJoinPoint");
		if (DEBUG) System.out.println("jp "+jp);
		if(jp instanceof MetaCallJoinPoint){
			Signature signature = (Signature)getContext().get("signature");
			if (DEBUG) System.out.println("method "+signature.getName());
			assert signature.getName() == "interactionAtJoinPoint";
			return (MetaCallJoinPoint)jp;
		}else{
			throw new RuntimeException("Joinpoint was not a MetaCallJoinPoint");
		}
	}
	
	/** Retrieves all pointcut-and-advice (of all aspects) at the current join point. */
	private LinkedList getApplicablePAs() {
		return (LinkedList)getJoinPoint().args[2];
	}
	
	/** Retrieves all pointcut-and-advice of the aspect. */
	private LinkedList getAspectPAs(Aspect aspect) {
		List applicablePAs = getApplicablePAs();
		if (DEBUG) System.out.println("applicablePAs "+applicablePAs);
		Iterator it = applicablePAs.iterator();
		LinkedList pas = new LinkedList();
		while (it.hasNext()) {
			PointcutAndAdvice pa = (PointcutAndAdvice) it.next();
			if (DEBUG) System.out.println("getAspectPAs? "+pa.aspect+" == "+aspect);
			if (pa.aspect.equals(aspect)) { 				
				pas.add(pa);
				if (DEBUG) System.out.println("applicablePAs of aspect "+aspect+" are "+pas);
			}
		}
		if (DEBUG) System.out.println("applicablePAs of aspect "+aspect+" are "+pas);
		return pas;
	}

	/** Finds the first pointcut-and-advice of the aspect and returns its index. */
	private int firstIndexOf(Aspect aspect) {
		List applicablePAs = getApplicablePAs();
		Iterator it = applicablePAs.iterator();
		LinkedList pas = new LinkedList();
		int result = -1;
		while (it.hasNext()) {
			PointcutAndAdvice pa = (PointcutAndAdvice) it.next();
			if (pa.aspect == aspect) return applicablePAs.indexOf(pa);
		}
		return result;
	}
	
	/** Finds the last pointcut-and-advice of the aspect and returns its index. */
	private int lastIndexOf(Aspect aspect) {
		List applicablePAs = getApplicablePAs();
		Iterator it = applicablePAs.iterator();
		LinkedList pas = new LinkedList();
		int result = -1;
		while (it.hasNext()) {
			PointcutAndAdvice pa = (PointcutAndAdvice) it.next();
			if (pa.aspect == aspect) result = applicablePAs.indexOf(pa);
		}
		return result;
	}
	
	/** Reverts a list. */
	private LinkedList revert(LinkedList list) {
		LinkedList rlist = new LinkedList();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			PointcutAndAdvice pa = (PointcutAndAdvice) it.next();
			rlist.addFirst(pa);
		}
		return rlist;
	}
	
	
	/* Literals */
		
	/* Operations */
	
	
	public void exclude(Aspect aspect) {
		if (DEBUG) System.out.println("Exclude "+aspect);
		List applicablePAs = getApplicablePAs();
		applicablePAs.removeAll(getAspectPAs(aspect));
		if (DEBUG) System.out.println("applicablePAs after excluding "+applicablePAs);
	}
	
	public void exclude(String aspectName) {
		if (DEBUG) System.out.println("Exclude "+aspectName);
		Aspect aspect = AspectManager.getInstance().getAspect(aspectName);
		exclude(aspect);
	}
	
	public void include(Aspect aspect, int index) {
		if (DEBUG) System.out.println("Include "+aspect);
		PointcutAndAdvice pa = aspect.getPointcutAndAdviceAt(index);
		List applicablePAs = getApplicablePAs();
		applicablePAs.add(pa);
		if (DEBUG) System.out.println("applicablePAs after including "+applicablePAs);
	}
	
	public void include(String  aspectName, int index) {
		include(AspectManager.getInstance().getAspect(aspectName),index);
	}
	
	public void first(Aspect aspect) {
		if (DEBUG) System.out.println("First "+aspect);

		LinkedList applicablePAs = getApplicablePAs();
		
		LinkedList toMove = revert(getAspectPAs(aspect));
		applicablePAs.removeAll(toMove);

		Iterator it = toMove.iterator();
		while (it.hasNext()) {
			PointcutAndAdvice pa = (PointcutAndAdvice) it.next();
			applicablePAs.addFirst(pa);
		}
		if (DEBUG) System.out.println("applicablePAs "+applicablePAs);	
	}
	
	public void first(String  aspectname) {
		if (DEBUG) System.out.println("First "+aspectname);
		first(AspectManager.getInstance().getAspect(aspectname));
	}
	
	public void last(Aspect aspect) {
		if (DEBUG) System.out.println("Last "+aspect);
		
		List applicablePAs = getApplicablePAs();
		
		LinkedList toMove = getAspectPAs(aspect);
		applicablePAs.removeAll(toMove);
		applicablePAs.addAll(toMove);
	}
	
	public void last(String  aspectname) {
		if (DEBUG) System.out.println("Last "+aspectname);
		last(AspectManager.getInstance().getAspect(aspectname));
	}
	
	/** Moves all pointcut-and-advice of earlierAspect before the pointcut-and-advice of laterAspect. */ 
	public void moveToBefore(Aspect earlierAspect, Aspect laterAspect) {
		if (DEBUG) System.out.println("MoveToBefore "+earlierAspect+" before "+laterAspect);
		int index = firstIndexOf(laterAspect);
		LinkedList toMove = revert(getAspectPAs(earlierAspect));

		List applicablePAs = getApplicablePAs();
		applicablePAs.removeAll(toMove);
		
		Iterator it = toMove.iterator();
		while (it.hasNext()) {
			PointcutAndAdvice pa = (PointcutAndAdvice) it.next();
			applicablePAs.add(index,pa);
		}
		if (DEBUG) System.out.println("applicablePAs "+applicablePAs);	
	}

	/** Moves all pointcut-and-advice of earlierAspect before the pointcut-and-advice of laterAspect. */ 
	public void moveToBefore(String earlierAspectName, String laterAspectName) {
		moveToBefore(AspectManager.getInstance().getAspect(earlierAspectName),AspectManager.getInstance().getAspect(laterAspectName));
	}

	/** Moves all pointcut-and-advice of laterAspect after the pointcut-and-advice of earlierAspect. */ 
	public void moveToAfter(Aspect laterAspect, Aspect earlierAspect) {
		if (DEBUG) System.out.println("MoveToAfter "+laterAspect+" after "+earlierAspect);
		int index = lastIndexOf(earlierAspect);
		LinkedList toMove = revert(getAspectPAs(laterAspect));
		
		List applicablePAs = getApplicablePAs();
		applicablePAs.removeAll(toMove);
		
		if (DEBUG) System.out.println("applicablePAs "+applicablePAs);	

		Iterator it = toMove.iterator();
		while (it.hasNext()) {
			PointcutAndAdvice pa = (PointcutAndAdvice) it.next();
//			if (index+1 > applicablePAs.size()) {
  			  applicablePAs.add(index,pa);
//			} else {
//			  applicablePAs.add(pa);
//			}
		}
		if (DEBUG) System.out.println("applicablePAs after moveToAfter "+applicablePAs);	
	}	
	
	/** Moves all pointcut-and-advice of laterAspect after the pointcut-and-advice of earlierAspect. */ 
	public void moveToAfter(String laterAspectName, String earlierAspectName) {
		moveToAfter(AspectManager.getInstance().getAspect(laterAspectName),AspectManager.getInstance().getAspect(earlierAspectName));
	}
	
}



