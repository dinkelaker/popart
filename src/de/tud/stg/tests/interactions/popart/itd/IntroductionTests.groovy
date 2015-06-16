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

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.itd.*;

import junit.framework.TestCase;

import static org.junit.Assert.*

public class IntroductionTests extends TestCase{
	private C c;
	private Aspect myAspect;
	
	def aspect = { map, definition ->
		return new ITDCCCombiner().eval(map,definition)
	}
	
	public void setUp(){
		myAspect = createAspect();
		AspectManager.getInstance().register(myAspect)

		c = new C();
	}
	
	public void tearDown(){
		AspectManager.getInstance().unregister(myAspect)
	}
		
	public void testSimple(){
		println "Testing basic introduction functionallity."
		assertEquals(0, c.callCounter);
		assertEquals("Counter value is: 0", c.getCounterPrettyPrint());
		
		c.m1();
		c.m2();
		c.m3();

		assertEquals(3, c.callCounter);
		assertEquals("Counter value is: 3", c.getCounterPrettyPrint());
		println "Basics ok."
	}
	
	public void testClosurePropertyMaskedByMethodIntroduction(){
		Aspect myAspect = aspect(name:"ClosurePropertyMaskingAspect"){
			introduce_method(is_type(InstrumentationTestClass), "sameName") {String x ->
				return "Hello, "+x+"!"
			}
			introduce_field(is_type(InstrumentationTestClass), "sameName", {String x ->
				return "Bye, "+x+"!";
			})
			introduce_method(is_type(InstrumentationTestClass), "closureMethod") {String x ->
				return "closureMethod overwritten successfully, "+x+"!"
			}
		}
		try{
			InstrumentationTestClass c = new InstrumentationTestClass();
			assertEquals("normal closureMethod call", "closureMethod ok: Jim", c.closureMethod("Jim"));
			AspectManager.getInstance().register(myAspect);
			assertEquals("overwritten closureMethod call", "closureMethod overwritten successfully, Jim!", c.closureMethod("Jim"));
			assertEquals("introduced method call", "Hello, Jim!", c.sameName("Jim"));
			assertEquals("introduced closure property call", "Bye, Jim!", c.sameName.call("Jim"));
			println "Introduced method was successfully called instead of real closure method"
		}finally{
			if(myAspect!=null){
				AspectManager.getInstance().unregister(myAspect);
			}
		}
	}
	
	public void testMetaObjectProtocolForMethods(){
		println "Now testing Meta Object Protocol functionallity related to MetaMethods"
		c.m1(); // increase counter to 1 to ensure, it is read, as groovy probably could create a 0 from a lot of things
		
		Object[] noParameter = new Object[0];
		Object[] parameter = new Object[2];
		parameter[0]="Jack";
		parameter[1]="Sparrow";
		Class[] noParamClasses = new Class[0];
		Class[] paramClasses = new Class[2];
		paramClasses[0] = String.class;
		paramClasses[1] = String.class;
		
		MetaMethod withoutParameter = c.metaClass.getMetaMethod("getCounterPrettyPrint", noParameter);
		MetaMethod withParameter = c.metaClass.getMetaMethod("getCounterPrettyPrint", parameter);
		MetaMethod someOriginalMethod = c.metaClass.getMetaMethod("m1", noParameter);
		
		assertNotNull(withParameter);
		assertNotNull(withoutParameter);
		assertNotNull(someOriginalMethod);
		
		assertNotSame(withParameter, withoutParameter);
		assertNotSame(withParameter, someOriginalMethod);
		assertNotSame(withoutParameter, someOriginalMethod);
		
		assertSame(withoutParameter, c.metaClass.pickMethod("getCounterPrettyPrint", noParamClasses));
		assertSame(withParameter, c.metaClass.pickMethod("getCounterPrettyPrint", paramClasses));
		assertSame(someOriginalMethod, c.metaClass.pickMethod("m1", noParamClasses));
		
		assertEquals("getCounterPrettyPrint", withParameter.getName())
		assertEquals("getCounterPrettyPrint", withoutParameter.getName())
		assertEquals("m1", someOriginalMethod.getName())
		
		assertNull(c.metaClass.getMetaMethod("doesNotExist", noParameter));
		assertNull(c.metaClass.getMetaMethod("doesNotExist", parameter));
		assertNull(c.metaClass.pickMethod("doesNotExist", noParamClasses));
		assertNull(c.metaClass.pickMethod("doesNotExist", paramClasses));
								
		assertEquals("Counter value is: 1", withoutParameter.invoke(c, noParameter));
		assertEquals("Counter value is: 1", withoutParameter.doMethodInvoke(c, noParameter));
		assertEquals("Counter value is: 1", c.metaClass.invokeMethod(c, "getCounterPrettyPrint", noParameter));
		assertEquals("Hello, Jack Sparrow! Counter value is: 1", withParameter.invoke(c, parameter));
		assertEquals("Hello, Jack Sparrow! Counter value is: 1", withParameter.doMethodInvoke(c, parameter));
		assertEquals("Hello, Jack Sparrow! Counter value is: 1", c.metaClass.invokeMethod(c, "getCounterPrettyPrint", parameter));

		def List<MetaMethod> response;
		
		response = c.metaClass.respondsTo(c, "getCounterPrettyPrint");
		assertTrue((boolean)response);
		assertTrue(response.contains(withoutParameter));
		assertTrue(response.contains(withParameter));
		assertFalse(response.contains(someOriginalMethod));

		response = c.metaClass.respondsTo(c, "getCounterPrettyPrint", noParameter);
		assertTrue((boolean)response);
		assertTrue(response.contains(withoutParameter));
		assertFalse(response.contains(withParameter));
		assertFalse(response.contains(someOriginalMethod));

		response = c.metaClass.respondsTo(c, "getCounterPrettyPrint", parameter);
		assertTrue((boolean)response);
		assertFalse(response.contains(withoutParameter));
		assertTrue(response.contains(withParameter));
		assertFalse(response.contains(someOriginalMethod));

		response = c.metaClass.respondsTo(c, "m1");
		assertTrue((boolean)response);
		assertFalse(response.contains(withoutParameter));
		assertFalse(response.contains(withParameter));
		assertTrue(response.contains(someOriginalMethod));
		
		response = c.metaClass.respondsTo(c, "m1", noParameter);
		assertTrue((boolean)response);
		assertFalse(response.contains(withoutParameter));
		assertFalse(response.contains(withParameter));
		assertTrue(response.contains(someOriginalMethod));
		
		response = c.metaClass.respondsTo(c, "m1", parameter);
		assertFalse((boolean)response);
		assertFalse(response.contains(withoutParameter));
		assertFalse(response.contains(withParameter));
		assertFalse(response.contains(someOriginalMethod));
		
		response = c.metaClass.respondsTo(c, "m2");
		assertTrue((boolean)response);
		assertFalse(response.contains(withoutParameter));
		assertFalse(response.contains(withParameter));
		assertFalse(response.contains(someOriginalMethod));
		
		response = c.metaClass.respondsTo(c, "doesNotExist");
		assertFalse((boolean)response);
		assertFalse(response.contains(withoutParameter));
		assertFalse(response.contains(withParameter));
		assertFalse(response.contains(someOriginalMethod));
		
		response = c.metaClass.getMethods();
		assertTrue(response.contains(withoutParameter));
		assertTrue(response.contains(withParameter));
		assertTrue(response.contains(someOriginalMethod));
		println "MetaMethod related functionallity seems okay.";
	}
	
	public void testMetaObjectProtocolForProperties(){
		println "Now testing Meta Object Protocol functionallity related to MetaProperties"
		D d = new D();
		c.someProperty = 123;
		d.someProperty = 321;
		c.m1();
		d.m1();
		d.m1();
		
		assertEquals(123, c.someProperty);
		assertEquals(321, d.someProperty);
		assertEquals(1, c.callCounter);
		assertEquals(2, d.callCounter);
		
		c.metaClass.setProperty(c, "someProperty", 234);
		c.metaClass.setProperty(c, "callCounter", 5);
		
		assertEquals(234, c.metaClass.getProperty(c, "someProperty"));
		assertEquals(321, d.metaClass.getProperty(d, "someProperty"));
		assertEquals(5, c.metaClass.getProperty(c, "callCounter"));
		assertEquals(2, d.metaClass.getProperty(d, "callCounter"));
		
		def realMetaPropertyC = c.metaClass.getMetaProperty("someProperty");
		def realMetaPropertyD = d.metaClass.getMetaProperty("someProperty");
		def introducedMetaMethodC = c.metaClass.getMetaProperty("callCounter");
		def introducedMetaMethodD = d.metaClass.getMetaProperty("callCounter");
		
		assertNotNull(realMetaPropertyC);
		assertNotNull(realMetaPropertyD);
		assertNotNull(introducedMetaMethodC);
		assertNotNull(introducedMetaMethodD);
		assertNull(c.metaClass.getMetaProperty("doesNotExist"));

		assertNotSame(realMetaPropertyC, introducedMetaMethodC);
		assertNotSame(realMetaPropertyD, introducedMetaMethodD);

		assertEquals("someProperty", realMetaPropertyC.getName());
		assertEquals("someProperty", realMetaPropertyD.getName());
		assertEquals("callCounter", introducedMetaMethodC.getName());
		assertEquals("callCounter", introducedMetaMethodD.getName());

		realMetaPropertyD.setProperty(d,654);
		introducedMetaMethodD.setProperty(d,9);
		
		assertEquals(234, realMetaPropertyC.getProperty(c));
		assertEquals(654, realMetaPropertyD.getProperty(d));
		assertEquals(5, introducedMetaMethodC.getProperty(c));
		assertEquals(9, introducedMetaMethodD.getProperty(d));
		
		def response;
		
		assertEquals(realMetaPropertyC, c.metaClass.hasProperty(c, "someProperty"));
		assertEquals(realMetaPropertyD, d.metaClass.hasProperty(d, "someProperty"));
		assertEquals(introducedMetaMethodC, c.metaClass.hasProperty(c, "callCounter"));
		assertEquals(introducedMetaMethodD, d.metaClass.hasProperty(d, "callCounter"));
		assertNull(c.metaClass.hasProperty(c, "doesNotExist"));
		assertNull(d.metaClass.hasProperty(d, "doesNotExist"));
		
		response = c.metaClass.getProperties();
		//For some reason, MetaClassImpl removes MetaProperties representing normal fields from the list
		//assertTrue(response.contains(realMetaPropertyC));
		assertTrue(response.contains(introducedMetaMethodC));

		response = d.metaClass.getProperties();
		//For some reason, MetaClassImpl removes MetaProperties representing normal fields from the list
		//assertTrue(response.contains(realMetaPropertyD));
		assertTrue(response.contains(introducedMetaMethodD));

		println "MetaProperty related functionallity seems okay."
	}
	
	public void testIllegal(){
		println "Testing illegal property access and method calls"
		try{
			def x = c.doesNotExist;
			fail("doesNotExist can be read");
		}catch(MissingPropertyException mpe){}
		
		try{
			def x = c.doesNotExist;
			fail("doesNotExist can be written");
		}catch(MissingPropertyException mpe){}
		
		try{
			def x = c.doesNotExist();
			fail("doesNotExist() exists");
		}catch(MissingMethodException mme){}

		def d = "TestString not instance of C";

		try{
			def x = d.callCounter;
			fail("Counter introduced for class String");
		}catch(MissingPropertyException mpe){}
		
		try{
			def x = d.getCounterPrettyPrint();
			fail("CounterPrettyPrint() introduced for class String");
		}catch(MissingMethodException mme){}
	}
	
	public void testMOPillegalMethods(){
		println "Now testing illegal method calls via MOP"
		
		MetaMethod mm = c.metaClass.getMetaMethod("getCounterPrettyPrint", new Object[0])
		def d = "Some string.";

		try{
			def x = c.metaClass.invokeMethod(d, "getCounterPrettyPrint", new Object[0]);
			fail("metaclass allowed getCounterPrettyPrint invokeMethod for String");
		}catch(MissingMethodException mme){}
		
		try{
			def x = mm.invoke(d, new Object[0]);
			fail("metamethod allowed getCounterPrettyPrint invoke for String");
		}catch(MissingMethodException mme){}

		try{
			def x = mm.doMethodInvoke(d, new Object[0]);
			fail("metamethod allowed getCounterPrettyPrint doMethodInvoke for String");
		}catch(MissingMethodException mme){}
	}
	
	public void testMOPillegalProperties(){
		println "Now testing illegal property accesses via MOP"

		MetaProperty mp = c.metaClass.hasProperty(c, "callCounter")
		
		try{
			def x = c.metaClass.getProperty(c, "doesNotExist");
			fail("metaclass allowed doesNotExist write")
		}catch(MissingPropertyException mpe){}

		try{
			c.metaClass.setProperty(c, "doesNotExist", 0);
			fail("metaclass allowed doesNotExist write")
		}catch(MissingPropertyException mpe){}
		
		def d = "Some string.";
		
		try{
			def x = c.metaClass.getProperty(d, "callCounter");
			fail("metaclass allowed callCounter read for String")
		}catch(MissingPropertyException mpe){}

		try{
			c.metaClass.setProperty(d, "callCounter", 0);
			fail("metaclass allowed callCounter write for String")
		}catch(MissingPropertyException mpe){}

		try{
			def x = mp.getProperty(d);
			fail("metaproperty allowed callCounter read for String")
		}catch(MissingPropertyException mpe){}

		try{
			mp.setProperty(d, 0);
			fail("metaproperty allowed callCounter write for String")
		}catch(MissingPropertyException mpe){}
	}
	
	public void testEqualInstanceSeparation(){
		D d1 = new D();
		D d2 = new D();
		d1.callCounter = 123;
		d2.callCounter = 234;
		assertEquals(123, d1.callCounter);
		assertEquals(234, d2.callCounter);
	}
	
	private Aspect createAspect(){		
		//create aspect
		return aspect(name:"MyMethodCallCounterTestAspect") { 
			//Introduce field
			introduce_field(is_type(C), "callCounter", 0);
			//Introduce pretty print
			introduce_method(is_type(C), "getCounterPrettyPrint") {
				return "Counter value is: "+delegate.callCounter;
			}
			introduce_method(is_type(Object), "getCounterPrettyPrint") { String firstName, String lastName ->
				return "Hello, "+firstName+" "+lastName+"! "+delegate.getCounterPrettyPrint()
			}
			//Advice counting
			before ( is_type(C) & method_execution("m.*") ) {
				targetObject.callCounter++;
			}
		} 
	}
}
