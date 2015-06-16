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

import de.tud.stg.popart.aspect.*
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.aspect.extensions.instrumentation.*
import de.tud.stg.popart.aspect.extensions.itd.*

import java.util.List;

import junit.framework.TestCase

import static org.junit.Assert.*

/**
 * These tests validate various interactions between
 * Introductions and Advices.
 * @author Joscha Drechsler
 */
public class IntroductionAdviceInteractionTests extends TestCase{
	def aspect = { map, definition ->
		return new ITDCCCombiner().eval(map,definition)
	}
	
	public void testAdvice(){
		//the order of the advice invocations must be correct:
		List<String> expectedOrder = [
				"before call",
				"around call before proceed",
				"before execution",
				"around execution before proceed",
				"around execution after proceed",
				"after execution",
				"around call after proceed",
				"after call"];
		
		List<String> history = new java.util.LinkedList<String>();
		
		Aspect myAspect = aspect(name:"SimpleMethodAdviceAspect"){
			before(method_execution("m1")){
				history.add("before execution");
			}
			before(method_call("m1")){
				history.add("before call");
			}
			around(method_execution("m1")){
				history.add("around execution before proceed");
				proceed();
				history.add("around execution after proceed");
			}
			around(method_call("m1")){
				history.add("around call before proceed");
				proceed();
				history.add("around call after proceed");
			}
			after(method_execution("m1")){
				history.add("after execution");
			}
			after(method_call("m1")){
				history.add("after call");
			}
		}
		AspectManager.getInstance().register(myAspect);
		C c = new C();
		try{
			history.clear();
			c.m2();
			assertEquals 0, history.size();
			
			history.clear();
			c.m1();
			assertEquals expectedOrder, history;
			
			history.clear();
			c.m1();
			assertEquals expectedOrder, history;
			
			history.clear();
			c.m2();
			assertEquals 0, history.size();
			
			history.clear();
			c.m1();
			assertEquals expectedOrder, history;
		}finally{
			AspectManager.getInstance().unregister(myAspect);
		}
	}
	
	public void testIntroductions(){
		println "Testing basic introduction functionallity"
		Aspect myAspect = aspect(name:"SimpleIntroductionAspect"){
			introduce_field(is_type(C), "counter", 0)
			introduce_method(is_type(C), "incrementCounter"){
				counter++;
			}
		}
		AspectManager.getInstance().register(myAspect);
		try{
			C c = new C();
			assertEquals("Counter initialization", 0, c.counter)
			c.incrementCounter();
			c.counter = 3;
			c.incrementCounter();
			c.incrementCounter();
			assertEquals("Counter value", 5, c.counter)
		}finally{
			AspectManager.getInstance().unregister(myAspect);
		}
	}

	public void testIntroductionsAccessibleFromAdvice(){
		println "Testing introduction access from within advices"
		Aspect myAspect = aspect(name:"IntroductionAccessingAdviceAspect"){
			introduce_field(is_type(C), "counter1", 0)
			introduce_field(is_type(C), "counter2", 0)
			introduce_method(is_type(C), "incrementCounter"){
				delegate.counter1++;
			}
			before(method_execution("m1")){
				targetObject.incrementCounter();
				targetObject.counter2++;
			}
		}
		AspectManager.getInstance().register(myAspect);
		try{
			C c = new C();
			c.m1();
			c.m2();
			c.incrementCounter();
			c.m1();
			assertEquals("Counter 1 value", 3, c.counter1)
			assertEquals("Counter 2 value", 2, c.counter2)
		}finally{
				AspectManager.getInstance().unregister(myAspect);
		}
	}
	
	public void testClosurePropertyIntroduction(){
		println "Testing calls on introduced properties holding Closures"
		Aspect myAspect = aspect(name:"ClosurePropertyIntroductionAspect"){
			introduce_field(is_type(C), "closureProperty", {String x ->
				return "Hello, "+x+"!";
			})
		}
		AspectManager.getInstance().register(myAspect);
		try{
			C c = new C();
			assertEquals("original closure property with call access", "Hello, Jim!", c.closureProperty.call("Jim"));
			assertEquals("original closure property as method access", "Hello, Jim!", c.closureProperty("Jim"));
			c.closureProperty = {String x ->
				return "Bye, "+x+"!";
			};
			assertEquals("modified closure property as method access", "Bye, Jim!", c.closureProperty("Jim"));
			assertEquals("modified closure property with call access", "Bye, Jim!", c.closureProperty.call("Jim"));
		}finally{
			AspectManager.getInstance().unregister(myAspect);	
		}
	}
	
	/* 
	 * @todo once the instrumentation is fixed to provide aspect support
	 * to objects written in groovy, these tests should be added:
	 * 
	 * - "methods", which have been declared via a property like
	 * 		 def method = {args->code}
	 *   or introdcued as properties like
	 *   	introduce_field(pattern, "method", {args->code})
	 *   must be adviced by aspects, if the MetaClass is configured as
	 */
	
	public void testIntroducedMethodAdvice(){
		println "Testing introduced Methods to be covered by Advices"

		int adviceExecuted = 0;
		int adviceCalled = 0;
		Aspect myAspect = aspect(name:"IntroducedMethodAdviceAspect"){
			introduce_method(is_type(InstrumentationTestClass), "xyz") {
				return "method xyz executed";
			}
			before(method_execution("xyz")){
				adviceExecuted++;
			}
			before(method_call("xyz")){
				adviceCalled++;
			}
		}
		AspectManager.getInstance().register(myAspect);
		try{
			InstrumentationTestClass c = new InstrumentationTestClass();
			assertEquals("method ok: bla", c.method("bla"));
			assertEquals(0, adviceExecuted);
			assertEquals(0, adviceCalled);
			assertEquals("method xyz executed",c.xyz());
			assertEquals(1, adviceExecuted);
			assertEquals(1, adviceCalled);
		}finally{
			AspectManager.getInstance().unregister(myAspect);
		}
	}
	
	public void testIntroducedClosurePropertyAdvice(){
		println "Testing introduced Closure property calls to be covered by Advices"

		int adviceExecuted = 0;
		Aspect myAspect = aspect(name:"IntroducedClosurePropertyAdviceAspect"){
			introduce_field(is_type(InstrumentationTestClass), "xyz", {
				return "closure xyz was executed"
			});
			introduce_field(is_type(InstrumentationTestClass), "aaa", { String x ->
				return "other closure executed with parameter "+x
			});
			before(method_execution("xyz")){
				adviceExecuted++;
			}
		}
		AspectManager.getInstance().register(myAspect);
		InstrumentationTestClass c = new InstrumentationTestClass();
		try{
			assertEquals("closure xyz was executed", c.xyz.call());
			assertEquals("other closure executed with parameter test1", c.aaa.call("test1"));
			assertEquals("other closure executed with parameter test2", c.aaa("test2"));
			assertEquals(0, adviceExecuted);
			assertEquals("closure xyz was executed", c.xyz());
			assertEquals(0, adviceExecuted);
			/*
			 * To be used instead of the above 0 assertion, in case calls to
			 * closure properties should spawn method execution joinpoints
			 * assertEquals(1, adviceExecuted);
			 */
		}finally{
			AspectManager.getInstance().unregister(myAspect);
		}
	}
}
