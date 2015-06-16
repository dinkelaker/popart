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
package de.tud.stg.popart.aspect.extensions.itd;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod;

import de.tud.stg.popart.aspect.extensions.itd.declarations.MethodDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;

import groovy.lang.Closure;
import groovy.lang.MetaMethod;

/**
 * This kind of {@link InterTypeDeclaration} introduces a method.
 * @author Joscha Drechsler
 */
public class InterTypeMethodDeclaration extends InterTypeDeclaration implements MethodDeclaration {
	/**
	 * A cache associating {@link InterTypeMethodDeclaration}s
	 * {@link MetaMethod}s with their decorating
	 * {@link InterTypeDeclarationMetaMethod}s
	 */
	private final Map<Integer, InterTypeDeclarationMetaMethod> methodCache = new java.util.HashMap<Integer,InterTypeDeclarationMetaMethod>();
	
	/**
	 * retrieves the {@link InterTypeDeclarationMetaMethod} which
	 * decorates the given {@link InterTypeMethodDeclaration}s
	 * {@link MetaMethod}.
	 * @param itmd the method introduction
	 * @param mm the meta method
	 * @return the decorated meta method.
	 */
	public synchronized InterTypeDeclarationMetaMethod retrieveOrCreateDecoratedMetaMethod(MetaMethod mm){
		if(mm == null) return null;
		Integer key = System.identityHashCode(mm);
		InterTypeDeclarationMetaMethod result = methodCache.get(key);
		if(result == null){
			result = new InterTypeDeclarationMetaMethod(this, mm);
			methodCache.put(key, result);
		}
		return result;
	}

	/**
	 * The introduced methods name
	 */
	private String methodName;
	/**
	 * The introduced methods closure
	 */
	private Closure method;
	/**
	 * The list of doCall()-methods, the method closure serves
	 */
	private List<InterTypeDeclarationMetaMethod> callMethods;

	/**
	 * Constructor.
	 * @param aspect the aspect defning this introduction
	 * @param pattern the StructureDesignator to determine affected Objects
	 * @param methodName the methods name
	 * @param method the methods closure
	 */
	public InterTypeMethodDeclaration(InterTypeDeclarationAspect aspect, StructureDesignator pattern, String methodName, Closure method) {
		super(aspect, pattern);
		this.methodName = methodName;
		this.method = (Closure)method.clone();
		this.method.setResolveStrategy(Closure.DELEGATE_FIRST);
		List<MetaMethod> closureMetaMethods = ClosureMetaMethod.createMethodList(methodName, Object.class, this.method);
		List<InterTypeDeclarationMetaMethod> itdMetaMethods = new java.util.ArrayList<InterTypeDeclarationMetaMethod>(closureMetaMethods.size());
		for(MetaMethod mm : closureMetaMethods){
			itdMetaMethods.add(retrieveOrCreateDecoratedMetaMethod(mm));
		}
		this.callMethods = Collections.unmodifiableList(itdMetaMethods);
	}
	
	/**
	 * retrieves the list of meta methods declared by this inter-type
	 * declaration
	 * @return the list.
	 */
	public List<InterTypeDeclarationMetaMethod> getIntroductionMetaMethods() {
		return callMethods;
	}
	
	public String getMethodName(){
		return methodName;
	}
	
	public Closure getMethod(){
		return method;
	}
	
	/**
	 * {@see Object#toString()}
	 */
	public String toString(){
		return "methodIntroduction(pattern:"+getPattern()+",methods:"+callMethods+")";
	}

	public void receiveMethodLocation(MethodLocation location, Set<InterTypeDeclarationMetaMethod> applicableMethods) {
		String requestedMethodName = location.getMethodName();
		if(requestedMethodName != null && !requestedMethodName.equals(methodName)) return;
		for(InterTypeDeclarationMetaMethod method : callMethods){
			if(method.isValidMethod(location.getArgumentClasses())){
				applicableMethods.add(method);
				return;
			}
		}
	}

	public MetaMethod getDeclaredMetaMethod(Class<?>[] args) {
		for(InterTypeDeclarationMetaMethod method : callMethods){
			if(method.isValidMethod(args)){
				return method;
			}
		}
		return null;
	}
}
