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

import groovy.lang.Closure;

import java.util.Map;
import java.util.List;
import java.util.Set;

import org.codehaus.groovy.runtime.GeneratedClosure;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.extensions.DynamicAspect;
import de.tud.stg.popart.aspect.extensions.OrderedAspect;
import de.tud.stg.popart.aspect.extensions.itd.declarations.ClosureDeclaration;
import de.tud.stg.popart.aspect.extensions.itd.declarations.Declaration;
import de.tud.stg.popart.aspect.extensions.itd.locations.MethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectMethodLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectPropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.PropertyLocation;
import de.tud.stg.popart.aspect.extensions.itd.locations.StructureLocation;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;
import de.tud.stg.popart.dslsupport.ContextDSL;

/**
 * This class extends the default OrderedAspect and adds the capability
 * to associate InterTypeDeclarations with the aspect.
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationAspect extends OrderedAspect implements Declaration {
	private Declaration parent;

	/**
	 * A collection holding all the aspects associated Inter Type
	 * Declarations which introduce Properties.
	 */
	List<InterTypePropertyDeclaration> interTypePropertyDeclarations;

	/**
	 * A collection holding all the aspects associated Inter Type
	 * Declarations which introduce Methods.
	 */
	List<InterTypeMethodDeclaration> interTypeMethodDeclarations;

	/**
	 * Extends the OrderedAspects constructor to extract inter type
	 * declarations from the parameters if present.
	 * @param params the parameters
	 * @param interpreter the dsl interpreter
	 * @param definition the definition
	 */
	public InterTypeDeclarationAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
		super(params, interpreter, definition);
		if(definition instanceof GeneratedClosure){
			parent = new ClosureDeclaration((GeneratedClosure)definition).getParent();
		} else {
			parent = null;
		}
		interTypePropertyDeclarations = new java.util.ArrayList<InterTypePropertyDeclaration>();
		interTypeMethodDeclarations = new java.util.ArrayList<InterTypeMethodDeclaration>();
	}

	/**
	 * extended constructor: makes additional copies of the supplied lists
	 * of inter type declarations.
	 */
	public InterTypeDeclarationAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition, List<InterTypePropertyDeclaration> itpds, List<InterTypeMethodDeclaration> itmds){
		this(params, interpreter, definition);
		for(InterTypePropertyDeclaration pd : itpds){
			interTypePropertyDeclarations.add(new InterTypePropertyDeclaration(this, pd.getPattern(), pd.getType(), pd.getName(), pd.getDefaultValue()));
		}
		for(InterTypeMethodDeclaration md : itmds){
			interTypeMethodDeclarations.add(new InterTypeMethodDeclaration(this, md.getPattern(), md.getMethodName(), md.getMethod()));
		}
	}

	/**
	 * Same as {@link OrderedAspect#clone()} with the inter type
	 * declarations set added to the parameters.
	 */
	@Override
	public Object clone() {
		Map<String,Object> params = new java.util.HashMap<String,Object>();
		params.put("name",this.name);
		params.put("deployed",this.isDeployed());
		params.put("perInstance",this.perInstanceScope);
		params.put("perClass",this.perClassScope);
		params.put("priority",this.priority);
		return new InterTypeDeclarationAspect(params, getInterpreter(), getDefinitionClosure(), interTypePropertyDeclarations, interTypeMethodDeclarations);
	}

	/**
	 * adds a property introduction to this aspect.
	 * @param pattern the structure designator pattern
	 * @param propertyName the properties name
	 * @param initialValue the properties inital value
	 */
	public void addPropertyIntroduction(StructureDesignator pattern, String propertyName, Object initialValue){
		interTypePropertyDeclarations.add(new InterTypePropertyDeclaration(this,pattern,Object.class,propertyName,initialValue));
		AspectManager.getInstance().aspectChanged();
	}

	/**
	 * adds a method introduction to this aspect
	 * @param pattern the structure designator pattern
	 * @param methodName the method name
	 * @param method the closure defining the method(s)
	 */
	public void addMethodIntroduction(StructureDesignator pattern, String methodName, Closure method){
		interTypeMethodDeclarations.add(new InterTypeMethodDeclaration(this,pattern,methodName,method));
		AspectManager.getInstance().aspectChanged();
	}

	/**
	 * @return the list of associated inter type property declarations
	 */
	public List<InterTypePropertyDeclaration> getInterTypePropertyDeclarations(){
		return new java.util.ArrayList<InterTypePropertyDeclaration>(interTypePropertyDeclarations);
	}

	/**
	 * @return the list of associated inter type property declarations
	 */
	public List<InterTypeMethodDeclaration> getInterTypeMethodDeclarations(){
		return new java.util.ArrayList<InterTypeMethodDeclaration>(interTypeMethodDeclarations);
	}

	/**
	 * adds all matching introductions to the list of applicable methods.
	 * @param location the accessed signature
	 * @param applicableMethods the set of methods to add the matching
	 * 		introductions to
	 */
	public void receiveMethodLocation(MethodLocation location, Set<InterTypeDeclarationMetaMethod> applicableMethods){
		if(!isDeployed()) return;
		if(!isInScope(location)) return;
		((InterTypeDeclarationMetaAspect) metaAspect).receiveMethodLocation(this, location, applicableMethods);
	}

	/**
	 * adds all matching introductions to the list of applicable properties
	 * @param location the accessed signature
	 * @param applicableProperties the set of properties to add matching
	 * 		introductions to
	 */
	public void receivePropertyLocation(PropertyLocation location, Set<InterTypeDeclarationMetaProperty> applicableProperties){
		if(!isDeployed()) return;
		if(!isInScope(location)) return;
		((InterTypeDeclarationMetaAspect) metaAspect).receivePropertyLocation(this, location, applicableProperties);
	}
	
	/**
	 * This method is invoked before a method, introduced by this aspect, is invoked.
	 * @param location the accessed location
	 * @param method the method about to be invoked
	 */
	public void beforeInvokingMethod(ObjectMethodLocation location, InterTypeDeclarationMetaMethod method){
		((InterTypeDeclarationMetaAspect) metaAspect).beforeMethodInvocation(this, location, method);
	}
	
	/**
	 * This method is invoked after a method, introduced by this aspect, was invoked.
	 * @param location the accessed location
	 * @param method the invoked method
	 * @param result the returned value. <code>null</code> may indicate either
	 * 		<code>null</code> as the actual return value, or a void method.
	 */
	public void afterInvokingMethod(ObjectMethodLocation location, InterTypeDeclarationMetaMethod method, Object result){
		((InterTypeDeclarationMetaAspect) metaAspect).afterMethodInvocation(this, location, method, result);
	}

	/**
	 * This method is invoked before a field, introduced by this aspect, is read.
	 * @param location the accessed location
	 * @param property the field about to be read
	 */
	public void beforeFieldGet(ObjectPropertyLocation location, InterTypeDeclarationMetaProperty property){
		((InterTypeDeclarationMetaAspect) metaAspect).beforeFieldGet(this, location, property);
	}
	/**
	 * This method is invoked after a field, introduced by this aspect, is read.
	 * @param location the accessed location
	 * @param property the read field
	 * @param result the read value
	 */
	public void afterFieldGet(ObjectPropertyLocation location, InterTypeDeclarationMetaProperty property, Object result){
		((InterTypeDeclarationMetaAspect) metaAspect).afterFieldGet(this, location, property, result);
	}

	/**
	 * This method is invoked before a field, introduced by this aspect, is written.
	 * @param location the accessed location, containing the new value
	 * @param property the field about to be written
	 */
	public void beforeFieldSet(ObjectPropertyLocation location, InterTypeDeclarationMetaProperty property){
		((InterTypeDeclarationMetaAspect) metaAspect).beforeFieldSet(this, location, property);
	}
	/**
	 * This method is invoked after a field, introduced by this aspect, was written.
	 * @param location the accessed location
	 * @param property the written field
	 */
	public void afterFieldSet(ObjectPropertyLocation location, InterTypeDeclarationMetaProperty property){
		((InterTypeDeclarationMetaAspect) metaAspect).afterFieldSet(this, location, property);
	}

	/**
	 * helper method to determine, if the given structure location is within
	 * the aspects current deployment scope
	 * @param location the accessed location
	 * @return <code>true</code> if it is in scope, <code>false</code> otherwise.
	 */
	public boolean isInScope(StructureLocation location){
		if(location instanceof ObjectLocation){
			ObjectLocation loc = (ObjectLocation) location;
			return isInScope(loc.getObject());
		}else{
			return isInScope(location.getTargetClass());
		}
	}
	
	public Declaration getParent() {
		return parent;
	}

	/**
	 * Adds all introduced methods and properties to the
	 * list of members.
	 */
	@Override
	public List<AspectMember> getAllAspectMembers() {
		List<AspectMember> result = super.getAllAspectMembers();
		for(InterTypeMethodDeclaration itmd : interTypeMethodDeclarations){
			result.addAll(itmd.getIntroductionMetaMethods());
		}
		for(InterTypePropertyDeclaration itmd : interTypePropertyDeclarations){
			result.add(itmd.getIntroductionMetaProperty());
		}
		return result;
	}

	/**
	 * This method gets called in case of a conflict.
	 * @param location the location where the conflict occurred
	 * @param aspectInterferenceSet the aspects involved, including this aspect
	 * @param applicableDeclarations all applicable declarations
	 */
	public void introductionConflict(StructureLocation location, Set<InterTypeDeclarationAspect> aspectInterferenceSet, Set<? extends Declaration> applicableDeclarations) {
		((InterTypeDeclarationMetaAspect) metaAspect).introductionConflict(this, location, aspectInterferenceSet, applicableDeclarations);
	}

	/**
	 * Overrides {@link DynamicAspect#undeploy()} to reset all registered
	 * property values on undeploy
	 */
	@Override
	public synchronized void undeploy() {
		super.undeploy();
		for(InterTypePropertyDeclaration itpd : interTypePropertyDeclarations){
			itpd.resetAllValues();
		}
	}

	/**
	 * Overrides {@link DynamicAspect#undeployPerClass(Class)} to reset
	 * all registered property values of objects of the given class on
	 * undeploy
	 * @param theClass the class
	 */
	@Override
	public synchronized void undeployPerClass(@SuppressWarnings("unchecked")Class theClass) {
		super.undeployPerClass(theClass);
		for(InterTypePropertyDeclaration itpd : interTypePropertyDeclarations){
			itpd.resetValues(theClass);
		}
	}

	/**
	 * Overrides {@link DynamicAspect#undeployPerInstance(Object)} to
	 * reset the objects registered property value on undeploy
	 * @param object the object
	 */
	@Override
	public synchronized void undeployPerInstance(Object object) {
		super.undeployPerInstance(object);
		for(InterTypePropertyDeclaration itpd : interTypePropertyDeclarations){
			itpd.resetValue(object);
		}
	}
}
