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
package de.tud.stg.popart.aspect.extensions.instrumentation;

import org.codehaus.groovy.reflection.CachedClass;

import groovy.lang.MetaMethod;

/**
 * This class decorates a MetaMethod. It is supposed to be inherited
 * and allows for selective method overriding.
 * @author Joscha Drechsler
 */
public class DelegatingMetaMethod extends MetaMethod {
	/**
	 * The decorated {@link MetaMethod}
	 */
	private MetaMethod delegate;

	public DelegatingMetaMethod(MetaMethod delegate){
		this.delegate = delegate;
	}

	public void checkParameters(Class[] arguments) {
		delegate.checkParameters(arguments);
	}

	public Object clone() {
		return new DelegatingMetaMethod((MetaMethod)delegate.clone());
	}

	public Object doMethodInvoke(Object object, Object[] argumentArray) {
		return delegate.doMethodInvoke(object, argumentArray);
	}

	public boolean equals(Object obj) {
		return (obj instanceof DelegatingMetaMethod) && delegate.equals(((DelegatingMetaMethod)obj).delegate);
	}

	public CachedClass getDeclaringClass() {
		return delegate.getDeclaringClass();
	}

	public String getDescriptor() {
		return delegate.getDescriptor();
	}

	public int getModifiers() {
		return delegate.getModifiers();
	}

	public String getMopName() {
		return delegate.getMopName();
	}

	public String getName() {
		return delegate.getName();
	}

	public Class[] getNativeParameterTypes() {
		return delegate.getNativeParameterTypes();
	}

	public CachedClass[] getParameterTypes() {
		return delegate.getParameterTypes();
	}

	public Class getReturnType() {
		return delegate.getReturnType();
	}

	public String getSignature() {
		return delegate.getSignature();
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public Object invoke(Object object, Object[] arguments) {
		return delegate.invoke(object, arguments);
	}

	public boolean isAbstract() {
		return delegate.isAbstract();
	}

	public boolean isCacheable() {
		return delegate.isCacheable();
	}

	public boolean isMethod(MetaMethod method) {
		return delegate.isMethod(method);
	}

	public boolean isStatic() {
		return delegate.isStatic();
	}

	public boolean isValidMethod(Class[] arguments) {
		return delegate.isValidMethod(arguments);
	}

	public boolean isValidMethod(Object[] arguments) {
		return delegate.isValidMethod(arguments);
	}

	public boolean isVargsMethod(Object[] arguments) {
		return delegate.isVargsMethod(arguments);
	}

	/*
	 * Builds "some.package.DelegatingMetaMethod@123aff2e[<original meta method string representation>]
	 */
    public String toString() {
        return this.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this)) + "[" + delegate.toString() + "]";
    }

    /**
     * retrieves the MetaClass which is decorated by this instance
     * @return the delegate
     */
	public MetaMethod getDelegate() {
		return delegate;
	}
}
