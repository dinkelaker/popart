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

import groovy.lang.Closure;

import java.util.Map;
import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.joinpoints.JoinPoint;

/**
 * This class extends the <tt>Aspect</tt> meta object class with the semantics of dynamic deployed aspects.
 * @author Tom Dinkelaker
 */
public class DynamicAspect extends Aspect {

    /**
     * This attribute determines whether an aspect is currently deployed or not.
     */
	private boolean deployed;
    
	/**
	 * Determines the per instance scope of this aspect.
	 * If not null, the aspect is only deployed for that instance. 
	 * If null, aspect is active for all instances.
	 */
    protected Object perInstanceScope; 

	/**
	 * Determines the per class scope of this aspect.
	 * If not null, the aspect is only deployed for that class. 
	 * If null, aspect is active for all classes.
	 */
    protected Class<?> perClassScope; 
    
	public DynamicAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
        super(params,interpreter,definition);
		
        //params name
        if (DEBUG) System.out.print("DynamicAspect.<init>: \t\t New Aspect name:"+((String)params.get("name")));

        //params deployed 
        if (params.get("deployed") != null) 
    		this.setDeployed((Boolean)params.get("deployed"));
    	else 
    		this.setDeployed(true); //default static deploy
    	if (DEBUG) System.out.print("deployed:"+this.isDeployed());

    	//params perInstance
   		this.perInstanceScope = (Object)params.get("perInstance"); 
    	if (DEBUG) System.out.print("perInstance:"+this.perInstanceScope);
    	
    	//params perClass
   		this.perClassScope = (Class<?>)params.get("perClass");
    	if (DEBUG) System.out.print("perClass:"+this.perClassScope);
	}
	
	//METHODS USED BY ASPECT MANAGER
		
    public Object clone() {
    	Map<String,Object> params = new java.util.HashMap<String,Object>();
    	params.put("name",this.name);
    	params.put("deployed",this.isDeployed());
    	params.put("perInstance",this.perInstanceScope);
    	params.put("perClass",this.perClassScope);
    	params.put("priority",this.priority);
    	return new DynamicAspect(params, getInterpreter(), getDefinitionClosure());
    }
    
	public void receiveBefore(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("DynamicAspect.receivingBefore: \t\t Receiving before JP="+jp+" CTXT="+jp.context); 
		if (!isDeployed()) return; 
		if (!isInScope(jp)) return; 
		super.receiveBefore(jp,applicablePAs);
	}
	
	/**
	 * Recieves a join point and decides whether a defined advice should be executed.
     * The <tt>adviced</tt> return parameter signals to the instrumentation whether the join point was advised of not. In case the join point was not adviced (by default) the original join actions are executed .  
	 * @return True if join point was advised; False, if no pointcut has matched and no advice was executed.
	 */
	public void receiveAround(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("DynamicAspect.receivingAround: \t\t Receiving before JP="+jp+" CTXT="+jp.context); 
		if (!isDeployed()) return; 
		if (!isInScope(jp)) return; 
        super.receiveAround(jp,applicablePAs); 
	}

	public void receiveAfter(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG) System.out.println("DynamicAspect.receivingAfter: \t\t Receiving after JP="+jp+" CTXT="+jp.context); 
		if (!isDeployed()) return;
		if (!isInScope(jp)) return; 
		super.receiveAfter(jp,applicablePAs);
	}
	
	/**
	 * This helper method determines whether the join point is in the scope of the currently deployed aspect, by taking into account the scoping of aspect on particular classes and instances.
	 * @param  jp	The join point representing the current scope.
	 * @return When returning true, the aspect is deployed or respectively active for the join point's scope.
	 */
	public synchronized boolean isInScope(JoinPoint jp) {
		return isInScope(jp.context.get("targetObject"));
	}

	/**
	 * This helper method determines whether the object is in the scope of the
	 * currently deployed aspect, by taking into account the scoping of aspect
	 * on particular classes and instances.
	 * @param  object the object in question.
	 * @return When returning true, the aspect is deployed or respectively active for the join point's scope.
	 */
	public synchronized boolean isInScope(Object targetObject) {
		boolean isInInstanceScope = (perInstanceScope == null) || targetObject.equals(perInstanceScope);
		boolean isInClassScope = isInScope(targetObject.getClass());

		boolean result = isInClassScope && isInInstanceScope;
		if (DEBUG) System.out.println("DynamicAspect.isInScope: \t\t In scope? $result as (inst.($perInstanceScope):$isInInstanceScope($perClassScope),class:$isInClassScope), JP='$jp' perInstance=$perInstanceScope perClass="+perClassScope);
		return result;
	}
	
	/**
	 * This helper method determines whether the class is in the scope of the
	 * currently deployed  aspect, by taking into account the scoping of aspect
	 * on particular classes.
	 * @param theClass the class
	 * @return <code>true</code> if the class is in scope of the aspect, <code>false</code> otherwise.
	 */
	public synchronized boolean isInScope(Class<?> theClass){
		return (perClassScope == null) || (perClassScope.isAssignableFrom(theClass));
	}
	
	//METHODS FOR META-ASPECT PROTOCOL 

	public void interactionAtJoinPoint(JoinPoint jp, Set<Aspect> aspects, List<PointcutAndAdvice> applicablePAs) {
        super.interactionAtJoinPoint(jp,aspects,applicablePAs);	
	}
	
    //METHODS USED BY RUN-TIME API
    
	/**
	 * Invoking this method will deploy the aspect (introduces its effects from the system).
	 */
	public synchronized void deploy() {
		this.setDeployed(true);
	}
	
	/**
	 * Invoking this method will undeploy the aspect (removes its effects from the system).
	 */
	public synchronized void undeploy() {
		this.setDeployed(false);
	}
	
	/**
	 * When using this method to deploy an aspect, the aspect will be deployed only for a particular object, but not for other objects.
	 * @param object
	 */
	public void deployPerInstance(Object object) {
		this.setDeployed(true);
		this.perInstanceScope = object;
	}
	
	public synchronized void undeployPerInstance(Object object) {
		this.perInstanceScope = null;
		this.setDeployed(false);
	}	
	
	/**
	 * When using this method to deploy an aspect, the aspect will be deployed only for a particular class, but not for other classes.
	 * @param object
	 */
	public synchronized void deployPerClass(Class<?> clazz) {
		this.setDeployed(true);
		this.perClassScope = clazz;
	}
	
	public synchronized void undeployPerClass(Class<?> clazz) {
		this.perClassScope = null;
		this.setDeployed(false);
	}

	protected synchronized void setDeployed(boolean deployed) {
		this.deployed = deployed;
		AspectManager.getInstance().aspectChanged();
	}

	public synchronized boolean isDeployed() {
		return deployed;
	}
	
	public Object getPerInstanceScope() {
		return perInstanceScope;
	}
}

