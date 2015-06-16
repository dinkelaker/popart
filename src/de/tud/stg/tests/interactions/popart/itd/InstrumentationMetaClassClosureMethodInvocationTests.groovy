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
package de.tud.stg.tests.interactions.popart.itd

import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClass

import junit.framework.TestCase

import static org.junit.Assert.*

/**
 * These Tests were created as follow up on the discovery of a bug in the
 * Instrumentation Implementation, which broke Calls to class methods, which
 * were defined as Closures.<br>
 * The following example, which does work in normal Groovy, would instead throw
 * a MissingMethodException, when using the InstrumentationMetaClass:
 * class C{<br>}
 * 		def method = {String x -> println x}<br>
 * 		public static void main(String[] args){<br>
 * 			new C().method("Test");<br>
 * 		}<br>
 * }<br>
 * @author Joscha Drechsler
 */
public class InstrumentationMetaClassClosureMethodInvocationTests extends TestCase{
	InstrumentationTestClass c;
	
	public void setUp(){
		c = new InstrumentationTestClass();
	}
	
	public void testGetterProperty(){
		def actual = c.getterProperty;
		assertEquals("getter property accessed successfully", actual);
	}

	public void testMethod(){
		def actual = c.method("c.method(String)")
		assertEquals("method ok: c.method(String)", actual);
	}
	
	public void testVarArgsMethod(){
		def actual = c.varArgsMethod("c", "varArgsMethod", "String, String, String")
		assertEquals("varArgsMethod ok: [c, varArgsMethod, String, String, String]", actual);
	}

	public void testClosureMethod(){
		def actual = c.closureMethod("c.closureMethod(String)");
		assertEquals("closureMethod ok: c.closureMethod(String)", actual);
	}
	
	public void testVarArgsClosureMethod(){
		def actual = c.varArgsClosureMethod("c", "varArgsClosureMethod", "String, String, String")
		assertEquals("varArgsClosureMethod ok: [c, varArgsClosureMethod, String, String, String]", actual);
	}

	public void testClosureGetterMethod(){
		def actual = c.closureGetterMethod("c.closureGetterMethod(String)");
		assertEquals("closureGetterMethod ok: c.closureGetterMethod(String)", actual);
	}
	
	public void testVarArgsClosureGetterMethod(){
		def actual = c.varArgsClosureGetterMethod("c", "varArgsClosureGetterMethod", "String, String, String");
		assertEquals("varArgsClosureGetterMethod ok: [c, varArgsClosureGetterMethod, String, String, String]", actual);
	}
}
