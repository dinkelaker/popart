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

import org.codehaus.groovy.reflection.CachedClass;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.extensions.instrumentation.DelegatingMetaMethod;
import de.tud.stg.popart.aspect.extensions.itd.locations.ObjectMethodLocation;

import groovy.lang.MetaMethod;
import groovy.lang.MissingMethodException;

/**
 * This class decorates any {@link MetaMethod} associated with a
 * {@link InterTypeMethodDeclaration}, and overrides the invocation
 * execution methods to first check the object to match against
 * the introductions pattern. All other methods are simple forwarding
 * delegations.
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationMetaMethod extends DelegatingMetaMethod implements AspectMember {
	/**
	 * The declaring introduction
	 */
	private InterTypeMethodDeclaration itmd;
	
	/**
	 * Constructor.
	 * @param itmd the declaring introduction
	 * @param delegate the meta method to decorate
	 */
	public InterTypeDeclarationMetaMethod(InterTypeMethodDeclaration itmd, MetaMethod delegate) {
		super(delegate);
		this.itmd = itmd;
	}

	public Object clone() {
		return itmd.retrieveOrCreateDecoratedMetaMethod((MetaMethod)getDelegate().clone());
	}

	public Object doMethodInvoke(Object object, Object[] argumentArray) {
		if(!itmd.appliesTo(object)) throw new MissingMethodException(itmd.getMethodName(), object.getClass(), argumentArray);

		/*
		 * TODO The aspect.beforeInvokingMethod and aspect.afterInvokingMethod calls
		 * should be done through the aspect (meta-)manager.
		 */
		InterTypeDeclarationAspect aspect = getInterTypeMethodDeclaration().getDeclaringAspect();
		ObjectMethodLocation location = new ObjectMethodLocation(object, getName(), argumentArray);
		
		aspect.beforeInvokingMethod(location, this);
		Object result = super.doMethodInvoke(object, argumentArray);
		aspect.afterInvokingMethod(location, this, result);
		
		return result;
	}

	public Object invoke(Object object, Object[] arguments) {
		if(!itmd.appliesTo(object)) throw new MissingMethodException(itmd.getMethodName(), object.getClass(), arguments);
		
		/*
		 * TODO The aspect.beforeInvokingMethod and aspect.afterInvokingMethod calls
		 * should be done through the aspect (meta-)manager.
		 */
		InterTypeDeclarationAspect aspect = getInterTypeMethodDeclaration().getDeclaringAspect();
		ObjectMethodLocation location = new ObjectMethodLocation(object, getName(), arguments);

		aspect.beforeInvokingMethod(location, this);
		Object result = super.invoke(object, arguments);
		aspect.afterInvokingMethod(location, this, result);
		
		return result;
	}

	public boolean equals(Object obj) {
		if(obj instanceof InterTypeDeclarationMetaMethod){
			InterTypeDeclarationMetaMethod itdmm = (InterTypeDeclarationMetaMethod) obj;
			return itdmm.itmd.equals(itdmm.itmd) && super.equals(obj);
		}else{
			return false;
		}
	}

	public String toString() {
		/*
		 * this will construct a string like:
		 * package.name.InterTypeDecarationmetaMethod@abc123fe[methodName(pgck1.Param1, pgck2.Param2), aspect:aspectname, pattern:is_type(someClass)]
		 */
		StringBuilder builder = new StringBuilder(this.getClass().getName());
		builder.append(String.format("@"));
		builder.append(Integer.toHexString(System.identityHashCode(this)));
		builder.append("[");
		builder.append(getName());
		builder.append("(");
		CachedClass[] params = getParameterTypes();
		if(params.length>0){
			builder.append(params[0]);
			for(int i = 1; i<params.length; i++){
				builder.append(", ");
				builder.append(params[i].getTheClass());
			}
		}
		builder.append("), aspect:");
		builder.append(getAspect().getName());
		builder.append(", pattern:");
		builder.append(itmd.getPattern());
		builder.append("]");
		return builder.toString();
	}
	
	public InterTypeMethodDeclaration getInterTypeMethodDeclaration() {
		return itmd;
	}
	
	public Aspect getAspect() {
		return itmd.getDeclaringAspect();
	}

	public int compareTo(InterTypeDeclarationMetaMethod other) {
		return Collections.reverseOrder().compare(this.getAspect().getPriority(), other.getAspect().getPriority());
	}
}
