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

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.dslsupport.DSL;
import de.tud.stg.popart.dslsupport.InterpreterCombiner;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * @author Tom Dinkelaker
 **/
public class CCCombiner extends InterpreterCombiner implements ContextDSL {

	public static PointcutDSL defaultPointcutDSL = new PointcutDSL(); 
	public static AdviceDSL defaultAdviceDSL = new ProceedingAdviceDSL(); 

	public PointcutDSL getPointcutDSL() {
		return pointcutDSL;
	}

	public void setPointcutDSL(PointcutDSL pointcutDSL) {
		this.pointcutDSL = pointcutDSL;
	}

	public AdviceDSL getAdviceDSL() {
		return adviceDSL;
	}

	public void setAdviceDSL(AdviceDSL adviceDSL) {
		this.adviceDSL = adviceDSL;
	}

	protected PointcutDSL pointcutDSL; 
	protected AdviceDSL adviceDSL; 

	private Aspect currentAspect = null;

	//TODO Check usage and assure that instance returned is retrieved using the JP context.
	public void setCurrentAspect(Aspect aspect) {
		synchronized (currentAspect) {
			this.currentAspect = aspect;
		}
	}

	//TODO Check usage and assure that instance returned is retrieved using the JP context.
	public Aspect getCurrentAspect() {
		synchronized (currentAspect) {
			return this.currentAspect;
		}
	}

	//TODO Check usage and assure that instance returned is retrieved thread-specific.
	public void setContext(Map<String,Object> context) {
		synchronized (this) {
			super.setContext(context);
			if(adviceDSL instanceof ProceedingAdviceDSL){
				((ProceedingAdviceDSL)adviceDSL).setContext(context);
			}
		}
	}

	//TODO Check usage and assure that instance returned is retrieved thread-specific.
	public Map<String,Object> getContext() {
		return super.getContext();
	}

	public CCCombiner() {
		super(defaultAdviceDSL,defaultPointcutDSL);
		adviceDSL = defaultAdviceDSL;
		pointcutDSL = defaultPointcutDSL;
	}
	
	/**
	 * Convenience constructor. Will use the specified AdviceDSL and the
	 * default PointcutDSL
	 * @param _adviceDSL the AdviceDSL
	 */
	public CCCombiner(AdviceDSL _adviceDSL){
		this(_adviceDSL, defaultPointcutDSL);
	}
	
	/**
	 * Convenience constructor. Will use the specified PoincutDSL and the
	 * default AdviceDSL
	 * @param _pointcutDSL the PointcutDSL
	 */
	public CCCombiner(PointcutDSL _pointcutDSL){
		this(defaultAdviceDSL, _pointcutDSL);
	}

	public CCCombiner(AdviceDSL _adviceDSL, PointcutDSL _pointcutDSL) {
		super(_adviceDSL,_pointcutDSL);
		
		assert _adviceDSL != null;
		adviceDSL = _adviceDSL;
		
		assert _pointcutDSL != null;
		pointcutDSL = _pointcutDSL;
	}
	
	/**
	 * Ensure that 
	 * at least one adviceDSL is set up.
	 * @param dsls
	 */
	protected void ensureAdviceDSL(Set<DSL> dsls) {
		boolean foundAnAdviceDSL = false;
		for (DSL dsl : dsls) {
			if (dsl instanceof ProceedingAdviceDSL) {
				foundAnAdviceDSL = true;
				adviceDSL = (ProceedingAdviceDSL)dsl;
				break; 
			}
		}
		if (!foundAnAdviceDSL) {
			dslDefinitions.add(defaultAdviceDSL);
			adviceDSL = defaultAdviceDSL;
		}
	}

	/**
	 * Ensure that 
	 * at least one adviceDSL is set up.
	 * @param dsls
	 */
	protected void ensurePointcutDSL(Set<DSL> dsls) {
		boolean foundAPointcutDSL = false;
		for (DSL dsl : dsls) {
			if (dsl instanceof PointcutDSL) {
				foundAPointcutDSL = true;
				pointcutDSL = (PointcutDSL)dsl;
				break; 
			}
		}
		if (!foundAPointcutDSL) {
			dslDefinitions.add(defaultPointcutDSL);
			pointcutDSL = defaultPointcutDSL;
		}
	}

	public CCCombiner(Set<DSL> dsls) {
		super(dsls);
		ensureAdviceDSL(dslDefinitions);
		ensurePointcutDSL(dslDefinitions);
		setCombinerAsBodyDelegateOfAllInterpreters();
	}

	public CCCombiner(ArrayList<DSL> dsls) {
		super(new HashSet(dsls));
		ensureAdviceDSL(dslDefinitions);
		ensurePointcutDSL(dslDefinitions);
		setCombinerAsBodyDelegateOfAllInterpreters();
	}

	/* BEGIN OF KEYWORDS */

	public Aspect eval(Map<String, Object> name, Closure definition) {
		currentAspect = AspectFactory.getInstance().createAspect(name,this,definition);
		synchronized (currentAspect) {
			currentAspect.createInstance();
			return currentAspect;
		}
	}

	public Aspect aspect(Map<String, Object> name, Closure definition) {
		Aspect aspectToBeRegistered = eval(name, definition);
		AspectManager.getInstance().register(aspectToBeRegistered);
		return aspectToBeRegistered;  
	}

	public void before(Pointcut pcexpr, Closure cl) {
		if (DEBUG) System.out.println("CCCombiner: \t\t Define before advice");
		if (DEBUG) System.out.println("CCCombiner: \t\t pcexpr = "+pcexpr);

		assert (cl != null) ;
		assert (pcexpr != null) ;
		//TODO add statement: cl.delegate = this;
		synchronized (currentAspect) {
			PointcutAndAdvice pa = new BeforePointcutAndAdvice(currentAspect,pcexpr,cl);
			currentAspect.addPointcutAndAdvice(pa);
		}
		//currentAspect.beforeAdviceClosureMap.put(pcexpr,pa);
		//currentAspect.pointcutAndAdviceList.add(pa);
	}

	public void around(Pointcut pcexpr, Closure cl) {
		if (DEBUG) System.out.println("CCCombiner: \t\t Define around advice");
		if (DEBUG) System.out.println("CCCombiner: \t\t pcexpr = "+pcexpr);
		assert (cl != null) ;
		assert (pcexpr != null); 
		//TODO add statement: cl.delegate = this;
		synchronized (currentAspect) {
			PointcutAndAdvice pa = new AroundPointcutAndAdvice(currentAspect,pcexpr,cl);
			currentAspect.addPointcutAndAdvice(pa);
		}
		//currentAspect.aroundAdviceClosureMap.put(pcexpr,pa);
		//currentAspect.pointcutAndAdviceList.add(pa);
	}

	public void after(Pointcut pcexpr, Closure cl) {
		if (DEBUG) System.out.println("CCCombiner: \t\t Define after advice");
		if (DEBUG) System.out.println("CCCombiner: \t\t pcexpr = "+pcexpr);
		assert (cl != null);
		assert (pcexpr != null);
		//println pcexpr.toString() + ":" + Thread.currentThread().getId();
		//TODO add statement: cl.delegate = this;
		synchronized (currentAspect) {
			PointcutAndAdvice pa = new AfterPointcutAndAdvice(currentAspect,pcexpr,cl);
			currentAspect.addPointcutAndAdvice(pa);
		}
		//currentAspect.afterAdviceClosureMap.put(pcexpr,pa);
		//currentAspect.pointcutAndAdviceList.add(pa);
	}

	public String toString() {
		return this.getClass().getName()+this.dslDefinitions;
	}
}

