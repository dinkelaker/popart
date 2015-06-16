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
import groovy.lang.GroovyObjectSupport;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * @author Tom Dinkelaker
 */
public class Aspect extends GroovyObjectSupport implements Comparable<Aspect> {

	protected static boolean DEBUG = false;

	protected static boolean NO_DEBUG_FOR_SUPER_CLASS = false;

	protected MetaAspect metaAspect;

	protected String name;

	protected Integer priority = 0;

	private List<PointcutAndAdvice> pointcutAndAdviceList;

	private List<BeforePointcutAndAdvice> beforeAdviceClosureMap;

	private List<AroundPointcutAndAdvice> aroundAdviceClosureMap;

	private List<AfterPointcutAndAdvice> afterAdviceClosureMap;

	private Closure definitionClosure = null; 

	private ContextDSL interpreter = null;

	public Aspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
		if (DEBUG && !NO_DEBUG_FOR_SUPER_CLASS) System.out.println("Aspect.<init>: \t\t New Aspect linking PointcutDSL and Advice DSL");

		if (DEBUG && !NO_DEBUG_FOR_SUPER_CLASS) System.out.print("Aspect.<init>: \t\t New Aspect name:"+params.get("name"));

		this.name = (params.get("name")).toString();
		this.interpreter = interpreter;

		if (params.get("priority") != null) 
			this.priority = (Integer)params.get("priority");
		else 
			this.priority = 0; //default priotity is zero

		if (DEBUG && !NO_DEBUG_FOR_SUPER_CLASS) System.out.print("priotity:"+this.priority);

		pointcutAndAdviceList = new java.util.LinkedList<PointcutAndAdvice>();

		beforeAdviceClosureMap = new java.util.LinkedList<BeforePointcutAndAdvice>();
		aroundAdviceClosureMap = new java.util.LinkedList<AroundPointcutAndAdvice>();
		afterAdviceClosureMap = new java.util.LinkedList<AfterPointcutAndAdvice>();

		if (DEBUG && !NO_DEBUG_FOR_SUPER_CLASS) System.out.print("Aspect.<init>: \t\t Setting the definition closure");
		this.definitionClosure = definition;
		definition.setDelegate(this.interpreter);

		if (DEBUG && !NO_DEBUG_FOR_SUPER_CLASS) System.out.print("Aspect.<init>: \t\t aspect metaClass="+this.metaAspect+" for created instance");
		metaAspect = (MetaAspect)(this.getMetaClass());
	}

	//METHODS USED BY ASPECT MANAGER

	public Closure getDefinitionClosure() {
		return definitionClosure;
	}

	/**
	 * Changes the DSL interpreter that interpret the keywords in advice.
	 */
	public void setInterpreter(ContextDSL interpreter) {
		this.interpreter = interpreter;
	}

	public ContextDSL getInterpreter() {
		return this.interpreter;
	}

	/*
	 * TODO: When using createInstance() on an aspect that was already
	 * created using createInstance(), clone() already filled that aspects
	 * fields and invoking the definition closure again would duplicate all
	 * the fields contents - how is this meant to work?
	 */
	public Aspect createInstance() {
		Aspect newInstance = (Aspect)this.clone();
		interpreter.setCurrentAspect(newInstance);
		newInstance.getDefinitionClosure().setDelegate(interpreter);
		newInstance.getDefinitionClosure().call();
		return newInstance;
	}    

	public Object clone() {
		Map<String,Object> params = new java.util.HashMap<String,Object>();
		params.put("name",this.name);
		params.put("priority",this.priority);
		return new Aspect(params, interpreter, definitionClosure);
	}

	public int compareTo(Aspect other) {
		return this.priority.compareTo(other.priority);
	}

	public void receiveBefore(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		metaAspect.receiveBefore(this,jp,applicablePAs);
	}

	/**
	 * Recieves a join point and decides whether a defined advice should be executed.
	 * Add pointcut-and-advice that pointcut match at cuttent join point to the <tt>applicablePAs</tt> list.
	 * @param  jp				The current join point intercepted by aspect manager
	 * @param  applicablePAs	List of pointcut-and-advice bindings to which apply at the current join point.
	 */
	public void receiveAround(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		metaAspect.receiveAround(this,jp,applicablePAs);
	}

	public void receiveAfter(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		metaAspect.receiveAfter(this,jp,applicablePAs);
	}

	//METHODS FOR META-ASPECT PROTOCOL 
	public void matchingPointcut(JoinPoint jp, PointcutAndAdvice pa) {
		//nothing to do in the default implementation	
	}

	public void notMatchingPointcut(JoinPoint jp, PointcutAndAdvice pa) {
		//nothing to do in the default implementation	
	}

	public void beforeCallingAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		//nothing to do in the default implementation	
	}

	public void afterCallingAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		//nothing to do in the default implementation	
	}

	public void interactionAtJoinPoint(JoinPoint jp, Set<Aspect> aspectInterferenceSet, List<PointcutAndAdvice> applicablePAs) {
		//nothing to do in the default implementation	
	}

	//METHODS USED BY RUN-TIME API

	public void setMetaAspect(MetaAspect amc) {
		this.metaAspect = amc;
	}

	public MetaAspect getMetaAspect() {
		return this.metaAspect;
	}

	public String getName() { return name; }

	public String toString() { return super.toString()+"[name="+name+"]"; }

	public int getPriority() { return priority; }

	public List<PointcutAndAdvice> findAllPointcutsAndAdvice() {
		return new java.util.ArrayList<PointcutAndAdvice>(pointcutAndAdviceList); 
	}
	
	public List<AspectMember> getAllAspectMembers(){
		List<AspectMember> result = new java.util.LinkedList<AspectMember>(pointcutAndAdviceList);
		return result;
	}

	public List<BeforePointcutAndAdvice> getBeforePAs() {
		return beforeAdviceClosureMap; 
	}

	public List<AroundPointcutAndAdvice> getAroundPAs() {
		return aroundAdviceClosureMap; 
	}

	public List<AfterPointcutAndAdvice> getAfterPAs() {
		return afterAdviceClosureMap; 
	}

	public int getPointcutAndAdviceIndex(PointcutAndAdvice pa) {
		return pointcutAndAdviceList.indexOf(pa);
	}

	public int getPointcutAndAdviceSize() {
		return pointcutAndAdviceList.size();
	}

	public PointcutAndAdvice getPointcutAndAdviceAt(int index) {
		return pointcutAndAdviceList.get(index);
	}

	/**
	 * Overloading the Array "[index]"-Operator that by default returns the i-th pointcut-and-advice defined.
	 */
	public PointcutAndAdvice getAt(int index) {
		return getPointcutAndAdviceAt(index);
	}

	public List<Pointcut> findAllPointcuts() {
		List<Pointcut> pcs = new java.util.LinkedList<Pointcut>();
		for(PointcutAndAdvice pa : pointcutAndAdviceList){
			pcs.add(pa.getPointcut()); 
		}
		return pcs;
	}

	public List<Closure> findAllAdvice() {
		List<Closure> advice = new java.util.LinkedList<Closure>();
		for(PointcutAndAdvice pa : pointcutAndAdviceList){
			advice.add(pa.getAdvice()); 
		}
		return advice;
	}

	public void addPointcutAndAdvice(PointcutAndAdvice pa) {
		if (pa instanceof BeforePointcutAndAdvice){
			beforeAdviceClosureMap.add((BeforePointcutAndAdvice)pa);
		}else if (pa instanceof AroundPointcutAndAdvice){
			aroundAdviceClosureMap.add((AroundPointcutAndAdvice)pa);
		}else if (pa instanceof AfterPointcutAndAdvice){
			afterAdviceClosureMap.add((AfterPointcutAndAdvice)pa);
		}else{
			throw new RuntimeException("Unknown point in time for advice.");
		}
		pointcutAndAdviceList.add(pa);
		AspectManager.getInstance().aspectChanged();
	}

	public void removePointcutAndAdvice(PointcutAndAdvice pa) {
		beforeAdviceClosureMap.remove(pa.getPointcut());
		pointcutAndAdviceList.remove(pa);
		AspectManager.getInstance().aspectChanged();
	}
}

