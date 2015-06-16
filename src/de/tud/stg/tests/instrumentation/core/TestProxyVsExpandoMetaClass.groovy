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
package de.tud.stg.tests.instrumentation.core

import groovy.util.GroovyTestCase


/**
 * @author Jan Stolzenburg
 */
public class TestProxyVsExpandoMetaClass extends GroovyTestCase {
	void testDirectCall() {
		MyProxyMetaClass.enable(Foo1)
		assert('Intercepted' == new Foo1().foo())
	}
	void testAccessToMetaClassDoesNotChangeToExpandoMetaClass() {
		MyProxyMetaClass.enable(Foo1)
		assert('Intercepted' == new Foo1().foo())
		assert(Util.isMetaclassInstanceOf(Foo1.metaClass, MyProxyMetaClass)) //Twice, to see if it is changed after the first test.
		assert(Util.isMetaclassInstanceOf(Foo1.metaClass, MyProxyMetaClass))
		assert('Intercepted' == new Foo1().foo())
		assert(Util.isMetaclassInstanceOf(Foo1.getMetaClass(), MyProxyMetaClass))
		assert(Util.isMetaclassInstanceOf(Foo1.getMetaClass(), MyProxyMetaClass))
		assert('Intercepted' == new Foo1().foo())
		def f = new Foo1()
		assert(Util.isMetaclassInstanceOf(f.metaClass, MyProxyMetaClass))
		assert(Util.isMetaclassInstanceOf(f.metaClass, MyProxyMetaClass))
		assert('Intercepted' == f.foo())
		assert(Util.isMetaclassInstanceOf(Foo1.metaClass, MyProxyMetaClass))
		assert(Util.isMetaclassInstanceOf(f.getMetaClass(), MyProxyMetaClass))
		assert(Util.isMetaclassInstanceOf(f.getMetaClass(), MyProxyMetaClass))
		assert('Intercepted' == f.foo())
		assert(Util.isMetaclassInstanceOf(Foo1.getMetaClass(), MyProxyMetaClass))
	}
	void testClosureDelegatesCall() {
		MyProxyMetaClass.enable(Foo1)
		def closure = {foo()}
		closure.delegate = new Foo1()
		assert(closure() == 'Intercepted')
	}
	
	//The same again, but with a Metaclass inheriting from ExpandoMetaClass:
	
	void testDirectCallEmc() {
		GroovySystem.metaClassRegistry.setMetaClass(Foo1, new MyExpandoMetaClass(Foo1))
		assert('Intercepted' == new Foo1().foo())
	}
	void testAccessToMetaClassDoesNotChangeToExpandoMetaClassEmc() {
		GroovySystem.metaClassRegistry.setMetaClass(Foo1, new MyExpandoMetaClass(Foo1))
		assert('Intercepted' == new Foo1().foo())
		assert(Util.isMetaclassInstanceOf(Foo1.metaClass, MyExpandoMetaClass))
		assert(Util.isMetaclassInstanceOf(Foo1.metaClass, MyExpandoMetaClass))
		assert('Intercepted' == new Foo1().foo())
		assert(Util.isMetaclassInstanceOf(Foo1.getMetaClass(), MyExpandoMetaClass))
		assert(Util.isMetaclassInstanceOf(Foo1.getMetaClass(), MyExpandoMetaClass))
		assert('Intercepted' == new Foo1().foo())
		def f = new Foo1()
		assert(Util.isMetaclassInstanceOf(f.metaClass, MyExpandoMetaClass))
		assert(Util.isMetaclassInstanceOf(f.metaClass, MyExpandoMetaClass))
		assert('Intercepted' == f.foo())
		assert(Util.isMetaclassInstanceOf(Foo1.metaClass, MyExpandoMetaClass))
		assert(Util.isMetaclassInstanceOf(f.getMetaClass(), MyExpandoMetaClass))
		assert(Util.isMetaclassInstanceOf(f.getMetaClass(), MyExpandoMetaClass))
		assert('Intercepted' == f.foo())
		assert(Util.isMetaclassInstanceOf(Foo1.getMetaClass(), MyExpandoMetaClass))
	}
	void testClosureDelegatesCallEmc() {
		GroovySystem.metaClassRegistry.setMetaClass(Foo1, new MyExpandoMetaClass(Foo1))
		def closure = {foo()}
		closure.delegate = new Foo1()
		assert(closure() == 'Not Intercepted')
		//This is the problem, why I have to inherit from ProxyMetaClass and not from ExpandoMetaClass.
	}
}

class Foo1 {
	def foo() {
		return 'Not Intercepted'
	}
}

class MyExpandoMetaClass extends ExpandoMetaClass {
	public MyExpandoMetaClass(Class theClass) {
		super(theClass)
		this.invokeMethod = { String methodName, Object[] args ->
			if (methodName == 'foo')
				return 'Intercepted'
			else if (methodName == 'getMetaClass')
				return this;
			else assert false
		}
		initialize()
	}
}