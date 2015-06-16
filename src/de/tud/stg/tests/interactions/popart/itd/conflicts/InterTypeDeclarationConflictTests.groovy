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
package de.tud.stg.tests.interactions.popart.itd.conflicts

import de.tud.stg.tests.interactions.popart.itd.*;
import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.itd.*;
import de.tud.stg.popart.aspect.extensions.itd.conflicts.*;

import junit.framework.TestCase;

import static org.junit.Assert.*;

/**
 * These tests validate the detection and management of
 * conflicts within inter type declarations
 * @author Joscha Drechsler
 */
public class InterTypeDeclarationConflictTests extends TestCase{
	def aspect = { map, definition ->
		return new ITDCCCombiner().eval(map,definition)
	}
	
	public void testBaseActionPropertyConflictExceptions(){
		println "Testing Base-Action conflicts for Properties"
		Aspect myAspect = aspect(name:"BaseActionPropertyConflictAspect"){
			//C has 1 extra someProperty
			//D has 0 extra someProperty
			introduce_field(is_class(C), "someProperty", 5);
			//C has 0 extra meAlsoHasSomeProperty
			//D has 2 extra meAlsoHasSomeProperty
			introduce_field(is_type(C), "meAlsoHasSomeProperty", 234);
			introduce_field(is_type(D), "meAlsoHasSomeProperty", 123);
		}
		AspectManager.getInstance().register(myAspect);
		C c = new C();
		D d = new D();
		try{
			// test access to c.someProperty
			// conflict : 1x Base + 1x Action 
			try{
				println c.someProperty;
				fail("c.someProperty read did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(1, e.getConflictingMetaProperties().size());
			}
		
			try{
				c.someProperty = 999;
				fail("c.someProperty write did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(1, e.getConflictingMetaProperties().size());
			}
		
			try{
				println c.metaClass.getMetaProperty("someProperty");
				fail("c metaProperty someProperty request did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(1, e.getConflictingMetaProperties().size());
			}
			
			try{
				println c.metaClass.hasProperty(c,"someProperty");
				fail("c hasProperty someProperty request did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(1, e.getConflictingMetaProperties().size());
			}
			
			try{
				println c.metaClass.getProperty(c,"someProperty");
				fail("c getProperty someProperty did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(1, e.getConflictingMetaProperties().size());
			}
			
			try{
				println c.metaClass.setProperty(c,"someProperty",7);
				fail("c setProperty someProperty did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(1, e.getConflictingMetaProperties().size());
			}

			// test access to d.someProperty
			// no conflict : 1x Base + 0x Action 
			assertEquals(3, d.someProperty);
			d.metaClass.hasProperty(d, "someProperty").setProperty(d, 123);
			assertEquals(123, d.metaClass.getProperty(d, "someProperty"));
			
			// test access to c.meAlsoHasSomeProperty
			// no conflict : 0x Base + 1x Action 
			assertEquals(234, c.meAlsoHasSomeProperty);
			c.metaClass.hasProperty(c, "meAlsoHasSomeProperty").setProperty(c, 432);
			assertEquals(432, c.metaClass.getProperty(c, "meAlsoHasSomeProperty"));
			
			// test access to d.meAlsoHasSomeProperty
			// conflict : 1x Base + 2x Action 
			try{
				println d.meAlsoHasSomeProperty;
				fail("d.meAlsoHasSomeProperty read did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
		
			try{
				d.meAlsoHasSomeProperty = 23;
				fail("d.meAlsoHasSomeProperty write did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
		
			try{
				println d.metaClass.getMetaProperty("meAlsoHasSomeProperty");
				fail("d getMetaPropery meAlsoHasSomeProperty request did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
			
			try{
				println d.metaClass.hasProperty(d,"meAlsoHasSomeProperty");
				fail("d hasPropery meAlsoHasSomeProperty request did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
			
			try{
				println d.metaClass.getProperty(d,"meAlsoHasSomeProperty");
				fail("d getPropery meAlsoHasSomeProperty request did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
			
			try{
				println d.metaClass.setProperty(d,"meAlsoHasSomeProperty",7);
				fail("d setPropery meAlsoHasSomeProperty request did not conflict");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaProperty());
				assertEquals(2, e.getConflictingMetaProperties().size());
			}

		}finally{
			AspectManager.getInstance().unregister(myAspect);
		}
	}

	public void testBaseActionMethodConflictExceptions(){
		println "Testing Base-Action conflicts for Methods"
		Aspect myAspect = aspect(name:"BaseActionMethodConflictAspect"){
			//C has 1 extra m1()
			//D has 2 extra m1()
			introduce_method(is_type(C), "m1") {
				println "First introduced m1";
			}
			introduce_method(is_class(D), "m1") {
				println "Second introduced m1";
			}
		}
		AspectManager.getInstance().register(myAspect);
		C c = new C();
		D d = new D();
		try{
			// Test access to c.m1();
			// conflict: 1x Base + 1x Action
			try{
				c.m1();
				fail("c.m1() did not conflict");
			}catch(BaseActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaMethod());
				assertEquals(1, e.getConflictingMetaMethods().size());
			}

			try{
				c.metaClass.getMetaMethod("m1", new Object[0]);
				fail("c getMetaMethod did not conflict");
			}catch(BaseActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaMethod());
				assertEquals(1, e.getConflictingMetaMethods().size());
			}
			
			try{
				c.metaClass.invokeMethod(c, "m1", new Object[0]);
				fail("c invokeMethod did not conflict");
			}catch(BaseActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaMethod());
				assertEquals(1, e.getConflictingMetaMethods().size());
			}
			
			assertTrue(c.metaClass.respondsTo(c, "m1").size() >= 2);
			assertTrue(c.metaClass.respondsTo(c, "m1", new Object[0]).size() >= 2);

			// Test access to d.m1();
			// conflict: 1x Base + 2x Action
			try{
				d.m1();
				fail("d.m1() did not conflict");
			}catch(BaseActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaMethod());
				assertEquals(2, e.getConflictingMetaMethods().size());
			}

			try{
				d.metaClass.getMetaMethod("m1", new Object[0]);
				fail("d getMetaMethod did not conflict");
			}catch(BaseActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaMethod());
				assertEquals(2, e.getConflictingMetaMethods().size());
			}
			
			try{
				d.metaClass.invokeMethod(d, "m1", new Object[0]);
				fail("d invokeMethod did not conflict");
			}catch(BaseActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertNotNull(e.getBaseMetaMethod());
				assertEquals(2, e.getConflictingMetaMethods().size());
			}
			
			assertTrue(d.metaClass.respondsTo(d, "m1").size() >= 3);
			assertTrue(d.metaClass.respondsTo(d, "m1", new Object[0]).size() >= 3);

		}finally{
			AspectManager.getInstance().unregister(myAspect);
		}
	}
	
	public void testActionActionPropertyConflictExceptions(){
		println "Testing Action-Action conflicts for Properties"
		Aspect myAspect = aspect(name:"ActionActionPropertyConflictAspect"){
			//C has 1 newProperty
			//D has 2 newProperty
			introduce_field(is_type(C), "newProperty", 5);
			introduce_field(is_class(D), "newProperty", 123);
		}
		AspectManager.getInstance().register(myAspect);
		C c = new C();
		D d = new D();
		try{
			// test access to c.newProperty
			// no conflict : 0x Base + 1x Action 
			assertEquals(5, c.newProperty);
			c.metaClass.hasProperty(c, "newProperty").setProperty(c, 222);
			assertEquals(222, c.metaClass.getProperty(c, "newProperty"));
			
			// test access to d.newProperty
			// conflict : 0x Base + 2x Action 
			try{
				println d.newProperty;
				fail("d.newProperty read did not conflict");
			}catch(ActionActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
		
			try{
				d.newProperty = 23;
				fail("d.newProperty write did not conflict");
			}catch(ActionActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
		
			try{
				println d.metaClass.getMetaProperty("newProperty");
				fail("d getMetaPropery newProperty request did not conflict");
			}catch(ActionActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
			
			try{
				println d.metaClass.hasProperty(d,"newProperty");
				fail("d hasPropery newProperty request did not conflict");
			}catch(ActionActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
			
			try{
				println d.metaClass.getProperty(d,"newProperty");
				fail("d getPropery newProperty request did not conflict");
			}catch(ActionActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaProperties().size());
			}
			
			try{
				println d.metaClass.setProperty(d,"newProperty",7);
				fail("d setPropery newProperty request did not conflict");
			}catch(ActionActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaProperties().size());
			}

		}finally{
			if(myAspect != null) AspectManager.getInstance().unregister(myAspect);
		}
	}

	public void testActionActionMethodConflictExceptions(){
		println "Testing Action-Action conflicts for Methods"
		Aspect myAspect = aspect(name:"ActionActionMethodConflictAspect"){
			//C has 2 newMethod()
			//D has 1 newMethod()
			introduce_method(is_type(C), "newMethod") {
				println "First new method";
			}
			introduce_method(is_class(C), "newMethod") {
				println "Second new method";
			}
		}
		AspectManager.getInstance().register(myAspect);
		C c = new C();
		D d = new D();
		try {
			// Test access to c.m1();
			// conflict: 0x Base + 2x Action
			try{
				c.newMethod();
				fail("c.newMethod() did not conflict");
			}catch(ActionActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaMethods().size());
			}

			try{
				c.metaClass.getMetaMethod("newMethod", new Object[0]);
				fail("c getMetaMethod newMethod did not conflict");
			}catch(ActionActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaMethods().size());
			}
			
			try{
				c.metaClass.invokeMethod(c, "newMethod", new Object[0]);
				fail("c invokeMethod newMethod did not conflict");
			}catch(ActionActionMethodConflictException e){
				println "Conflict detected: "+e.getMessage();
				assertEquals(2, e.getConflictingMetaMethods().size());
			}
			
			assertTrue(c.metaClass.respondsTo(c, "newMethod").size() >= 2);
			assertTrue(c.metaClass.respondsTo(c, "newMethod", new Object[0]).size() >= 2);

			// Test access to d.m1();
			// no conflict: 0x Base + 1x Action

			d.newMethod();
			d.metaClass.invokeMethod(d, "newMethod", new Object[0]);
			MetaMethod mm = d.metaClass.getMetaMethod("newMethod", new Object[0]);
			assertNotNull(mm);
			mm.invoke(d, new Object[0]);
			assertTrue(d.metaClass.respondsTo(d, "newMethod").size() > 0);
			assertTrue(d.metaClass.respondsTo(d, "newMethod", new Object[0]).size() > 0);
		}finally{
			AspectManager.getInstance().unregister(myAspect);
		}
	}
}
