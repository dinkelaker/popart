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

import junit.framework.TestCase;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.itd.*;

import static org.junit.Assert.*;

/**
 * Tests related to (possibly scoped) deployment of aspects
 * @author Joscha Drechsler
 */
public class IntroductionDeployAndScopeTests extends TestCase{
	private InterTypeDeclarationAspect myAspect;
	private C c;
	private MetaMethod mm;
	private MetaProperty mp;
	
	void setUp(){
		myAspect = createAspect();
		AspectManager.getInstance().register(myAspect)
		c = new C();
		mp = c.metaClass.getMetaProperty("callCounter");
		assertNotNull(mp);
		mm = c.metaClass.getMetaMethod("getCounterPrettyPrint", new Object[0]);
		assertNotNull(mm);
	}
	
	void testBasicDeployment(){
		println "Testing basic deployed and undeployed";
		
		assertEquals(0, c.callCounter);
		assertEquals("Counter value is: 0", c.getCounterPrettyPrint());

		c.m1();
		
		assertEquals(1, c.callCounter);
		assertEquals("Counter value is: 1", c.getCounterPrettyPrint());
				
		myAspect.undeploy();
		
		c.m1();
		unallowedAccesses(c);

		myAspect.deploy();
		
		assertEquals(0, c.callCounter);
		assertEquals("Counter value is: 0", c.getCounterPrettyPrint());
		
		c.m1();
		
		assertEquals(1, c.callCounter);
		assertEquals("Counter value is: 1", c.getCounterPrettyPrint());
	}
	
	void testPerObject(){
		println "Testing per object deployment";
		D d = new D();
		c.m1();
		c.m1();
		d.m1();
		
		assertEquals(2, c.callCounter);
		assertEquals("Counter value is: 2", c.getCounterPrettyPrint());
		assertEquals(1, d.callCounter);
		assertEquals("Counter value is: 1", d.getCounterPrettyPrint());
		
		myAspect.undeploy();
		myAspect.deployPerInstance(c);

		c.m1();
		d.m1();
		unallowedAccesses(d);
		assertEquals(1, c.callCounter);
		assertEquals("Counter value is: 1", c.getCounterPrettyPrint());
				
		myAspect.undeployPerInstance(c);

		c.m1();
		d.m1();
		unallowedAccesses(c);
		unallowedAccesses(d);

		myAspect.deploy();
		c.m1();
		d.m1();
		
		assertEquals(1, c.callCounter);
		assertEquals("Counter value is: 1", c.getCounterPrettyPrint());
		assertEquals(1, d.callCounter);
		assertEquals("Counter value is: 1", d.getCounterPrettyPrint());
	}
	
	void testPerClass(){
		println "Testing per class deployment";
		D d = new D();
		c.m1();
		d.m1();
		d.m1();
		assertEquals(1, c.callCounter);
		assertEquals("Counter value is: 1", c.getCounterPrettyPrint());
		assertEquals(2, d.callCounter);
		assertEquals("Counter value is: 2", d.getCounterPrettyPrint());
				
		myAspect.undeploy();
		myAspect.deployPerClass(C.class);

		c.m1();
		d.m1();
		assertEquals(1, c.callCounter);
		assertEquals("Counter value is: 1", c.getCounterPrettyPrint());
		assertEquals(1, d.callCounter);
		assertEquals("Counter value is: 1", d.getCounterPrettyPrint());
				
		myAspect.undeployPerClass(C.class);
		myAspect.deployPerClass(D.class);

		c.m1();
		d.m1();
		unallowedAccesses(c);
		assertEquals(1, d.callCounter);
		assertEquals("Counter value is: 1", d.getCounterPrettyPrint());
				
		myAspect.undeployPerClass(D.class);

		c.m1();
		d.m1();
		unallowedAccesses(c);
		unallowedAccesses(d);

		myAspect.deploy();
		c.m1();
		d.m1();
		
		assertEquals(1, c.callCounter);
		assertEquals("Counter value is: 1", c.getCounterPrettyPrint());
		assertEquals(1, d.callCounter);
		assertEquals("Counter value is: 1", d.getCounterPrettyPrint());
	}
	
	private void unallowedAccesses(C c){
		unallowedPropertyAccesses(c);
		unallowedMethodAccesses(c);
	}
	
	private void unallowedPropertyAccesses(C c){
		if(c.metaClass.hasProperty(c, "callCounter")){
			fail("hasProperty evaluated true");
		}
		try{
			println c.callCounter;
			fail("Introduced field was readable");
		}catch(MissingPropertyException mpe){}
		try{
			println c.metaClass.getProperty(c, "callCounter");
			fail("Introduced field was readable through meta class");
		}catch(MissingPropertyException mpe){}
		try{
			println mp.getProperty(c);
			fail("Introduced field was readable through meta property");
		}catch(MissingPropertyException mpe){}
		try{
			c.callCounter = 13;
			fail("Introduced field was writeable");
		}catch(MissingPropertyException mpe){}
		try{
			c.metaClass.setProperty(c,"callCounter", 14);
			fail("Introduced field was writeable through meta class");
		}catch(MissingPropertyException mpe){}
		try{
			mp.setProperty(c, 15);
			fail("Introduced field was writeable through meta property");
		}catch(MissingPropertyException mpe){}
	}
	
	private void unallowedMethodAccesses(C c){
		if(c.metaClass.respondsTo(c, "getCounterPrettyPrint", new Object[0])){
			fail("respondsTo with parameters evaluated true");
		}
		if(c.metaClass.respondsTo(c, "getCounterPrettyPrint")){
			fail("respondsTo without parameters evaluated true");
		}
		try{
			println c.getCounterPrettyPrint();
			fail("Introduced method was accessible");
		}catch(MissingMethodException mme){}
		try{
			println c.metaClass.invokeMethod(c, "getCounterPrettyPrint", new Object[0]);
			fail("Introduced method was accessible through meta class");
		}catch(MissingMethodException mme){}
		try{
			println mm.invoke(c, new Object[0]);
			fail("Introduced method was accessible through meta method");
		}catch(MissingMethodException mme){}
		try{
			println mm.doMethodInvoke(c, new Object[0]);
			fail("Introduced method was accessible through meta method doMethodInvoke");
		}catch(MissingMethodException mme){}
	}
	
	void tearDown(){
		AspectManager.getInstance().unregister(myAspect)
	}
	
	private Aspect createAspect(){
		def aspect = { map, definition ->
			return new ITDCCCombiner().eval(map,definition)
		}
		
		//create aspect
		return aspect(name:"IntroductionsAndScopedDeploymentTestAspect") { 
			//Introduce field
			introduce_field(is_type(C), "callCounter", 0);
			//Introduce pretty print
			introduce_method(is_type(C), "getCounterPrettyPrint") {
				return "Counter value is: "+delegate.callCounter;
			}
			//Advice counting
			before ( is_type(C) & method_execution("m.*") ) {
				targetObject.callCounter++;
			}
		} 
	}
}
