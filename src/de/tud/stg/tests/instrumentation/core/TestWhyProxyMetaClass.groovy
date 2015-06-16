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
public class TestWhyProxyMetaClass extends GroovyTestCase {
	void testExpandoMetaClass() {
		def emc =  new ExpandoMetaClass(Foo2)
		emc.invokeMethod = {methodName, args ->
			return 'Intercepted'
		}
		emc.initialize()
		GroovySystem.metaClassRegistry.setMetaClass(Foo2, emc)
		
		def obj = new Foo2()
		assert(obj.foo() == 'Intercepted')
		def closure = {
			return foo()
		}
		closure.delegate = obj
		assert(closure() == 'Not Intercepted')
	}
	void testProxyMetaClass() {
		def proxy = ProxyMetaClass.getInstance(Foo2)
		proxy.interceptor = new MyInterceptor()
		proxy.use {
			assert('Intercepted' == new Foo2().foo())
			def closure = {
				foo()
			}
			closure.delegate = new Foo2()
			assert(closure() == 'Intercepted')
		}
	}
}

class Foo2 {
	def foo() {
		return 'Not Intercepted'
	}
}

class MyInterceptor implements Interceptor {
	Object beforeInvoke(Object a_object, String a_methodName, Object[] a_arguments) {
	}
	boolean doInvoke() {
		return true
	}
	Object afterInvoke(Object a_object, String a_methodName, Object[] a_arguments, Object a_result) {
		if (a_methodName == 'foo')
			return 'Intercepted'
		else
			return a_result
	}
}