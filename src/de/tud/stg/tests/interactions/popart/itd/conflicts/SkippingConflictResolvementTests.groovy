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
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;
import de.tud.stg.popart.aspect.extensions.itd.*;
import de.tud.stg.popart.aspect.extensions.itd.conflicts.*;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;

import groovy.lang.MetaProperty;

import java.util.List;

import junit.framework.TestCase;

import static org.junit.Assert.*;

/**
 * these tests validate the skipping conflict resolvements, meaning
 * that conflicts are solved by skipping actions (= ignoring introductions).
 * @author Joscha Drechsler
 */
public class SkippingConflictResolvementTests extends TestCase{
	def aspect = { map, definition ->
		return new ITDCCCombiner().eval(map,definition)
	}
	
	public void tearDown(){
		AspectManager.getInstance().resetConflictResolver();
	}
	
	public void testBaseActionPropertySkipping(){
		println "Testing conflict resolvement for Properties: Base Action Skip Action"
		Aspect myAspect = aspect(name:"ActionSkippingConflictResolverAspect"){
			introduce_field(is_type(C), "meAlsoHasSomeProperty", 333);
			introduce_field(is_type(C), "x", 0);
			introduce_field(is_class(D), "x", 0);
		}
		AspectManager.getInstance().register(myAspect);
		C c = new C();
		D d = new D();
		try{
			//access to c should work
			assertEquals(333, c.meAlsoHasSomeProperty);
			//access to d should not work since the conflict resolver is not yet set
			try{
				d.meAlsoHasSomeProperty = 32;
				fail("Access to d.meAlsoHasSomeProperty should have failed");
			}catch(BaseActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
			}
		
			try{
				println d.x;
				fail("Access to d.x should have failed");
			}catch(ActionActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
			}
		
			//now set the conflict resolver
			AspectManager.getInstance().setResolvementStrategy(new ActionSkippingConflictResolver());
			
			//access to c and d should work in all variants
			MetaProperty mpc = c.metaClass.getMetaProperty("meAlsoHasSomeProperty");
			MetaProperty mpd = d.metaClass.getMetaProperty("meAlsoHasSomeProperty");
			
			mpc.setProperty c, 777;
			mpd.setProperty d, 888;
			
			assertEquals(777, c.metaClass.getProperty(c,"meAlsoHasSomeProperty"));
			assertEquals(888, d.metaClass.getProperty(d,"meAlsoHasSomeProperty"));
			
			assertEquals(0, c.x);
			/*
			 * except for d.x which is an action action conflict, and thus will not
			 * be resolved as there is no base property involved.
			 */
			try{
				d.x = 123;
				fail("Access to d.x should have failed");
			}catch(ActionActionPropertyConflictException e){
				println "Conflict detected: "+e.getMessage();
			}
		} finally {
			AspectManager.getInstance().unregister(myAspect);
		}
	}
	
	private Closure firstAspectClosure = {
		StructureDesignator sd = is_type(C) | is_type(InstrumentationTestClass);
		introduce_field(sd, "someProperty", 5);
		introduce_method(sd, "method"){String x ->
			return "first aspects method called with parameter: "+x;
		}
	}
	
	private Closure secondAspectClosure = {
		StructureDesignator sd = is_type(C) | is_type(InstrumentationTestClass);
		introduce_field(sd, "someProperty", 7);
		introduce_method(sd, "method"){String x ->
			return "second aspects method called with parameter: "+x;
		}
	}
	
	public void testPrioritySkipping(){
		println "Testing conflict resolvement via priorities";
		
		//AspectManager.getInstance().setMetaManager(new RuleBasedAspectMetaManager(new InterTypeDeclarationAspectMetaManager());
		AspectManager.getInstance().setResolvementStrategy(new RuleBasedConflictResolver(new InterTypeDeclarationConflictResolver(), true));
		
		Aspect firstAspectWithLowPriority = aspect(name:"FirstAspectLowPriority", priority:1, firstAspectClosure);
		Aspect firstAspectWithMediumPriority = aspect(name:"FirstAspectMediumPriority", priority:2, firstAspectClosure);
		Aspect firstAspectWithHighPriority = aspect(name:"FirstAspectHighPriority", priority:3, firstAspectClosure);
		
		Aspect secondAspectWithMediumPriority = aspect(name:"SecondAspectMediumPriority", priority:2, secondAspectClosure);
		
		runThePriorityTest(firstAspectWithHighPriority, firstAspectWithMediumPriority, firstAspectWithLowPriority, secondAspectWithMediumPriority);
	}

	public void testPrecedenceSkipping(){
		println "Testing conflict resolvement via Precedence";
				
		AspectFactory.setDefaultComparator(new PrecedenceComparator());
		
		//AspectManager.getInstance().setMetaManager(new RuleBasedAspectMetaManager(new InterTypeDeclarationAspectMetaManager());
		AspectManager.getInstance().setResolvementStrategy(new RuleBasedConflictResolver(new InterTypeDeclarationConflictResolver(), false));
				
		Aspect firstAspectWithPrecedence = aspect(name:"FirstAspectOne", firstAspectClosure);
		Aspect firstAspect = aspect(name:"FirstAspectTwo", firstAspectClosure);
		Aspect firstAspectNoPrecedence = aspect(name:"FirstAspectThree", firstAspectClosure);
		
		Aspect secondAspect = aspect(name:"SecondAspect", secondAspectClosure);
		
		aspect(name:"PrecedenceDeclaration"){
			declare_precedence("FirstAspectOne", "SecondAspect", "FirstAspectThree");
			declare_mutex("FirstAspectOne", "SecondAspect", "FirstAspectThree");
		}
		
		runThePriorityTest(firstAspectWithPrecedence, firstAspect, firstAspectNoPrecedence, secondAspect);
	}
	
	/**
	 * executes the priority test using the given aspects.
	 * The conflict resolver must be configured properly to resolve
	 * the prioritized aspects conflicts.
	 * @param highPriority an aspect based on firstAspectClosure, with higher priority than secondAspect.
	 * @param normalPriority an aspect based on firstAspectClosure, with equal priority to secondAspect.
	 * @param lowPriority an aspect based on firstAspectClosure, with lower priority to secondAspect.
	 * @param secondAspect an aspect based on secondAspectClosure.
	 */
	private void runThePriorityTest(Aspect highPriority, Aspect normalPriority, Aspect lowPriority, Aspect secondAspect){
		//ensure proper setup.
		assert highPriority.getDefinitionClosure() == firstAspectClosure;
		assert normalPriority.getDefinitionClosure() == firstAspectClosure;
		assert lowPriority.getDefinitionClosure() == firstAspectClosure;
		assert secondAspect.getDefinitionClosure() == secondAspectClosure;
		//do the testing.
		try{
			C c = new C();
			InstrumentationTestClass d = new InstrumentationTestClass();
			
			AspectManager.getInstance().register(secondAspect);
			
			AspectManager.getInstance().register(normalPriority);
			//aspects have same priority, so accesses will fail where no base code is involved:
			assertEquals(3, c.someProperty);
			try{
				println c.method("test1");
				fail("access to c.method(String) should have failed.");
			}catch(ActionActionMethodConflictException e){
				println "Caught exception: "+e.getMessage();
			}
			
			try{
				println d.someProperty;
				fail("access to d.someProperty should have failed.");
			}catch(ActionActionPropertyConflictException e){
				println "Caught exception: "+e.getMessage();
			}
			assertEquals("method ok: test2", d.method("test2"));
			
			AspectManager.getInstance().unregister(normalPriority);
			AspectManager.getInstance().register(lowPriority);
			//first aspect has lower priority:
			assertEquals(3, c.someProperty);
			assertEquals("second aspects method called with parameter: test3", c.method("test3"));
			
			assertEquals(7, d.someProperty);
			assertEquals("method ok: test4", d.method("test4"));
			
			AspectManager.getInstance().unregister(lowPriority);
			AspectManager.getInstance().register(highPriority);
			//first aspect has higher priority:
			assertEquals(3, c.someProperty);
			assertEquals("first aspects method called with parameter: test5", c.method("test5"));
			
			assertEquals(5, d.someProperty);
			assertEquals("method ok: test6", d.method("test6"));
			
		} finally {
			AspectManager.getInstance().unregister(lowPriority);
			AspectManager.getInstance().unregister(normalPriority);
			AspectManager.getInstance().unregister(highPriority);
			
			AspectManager.getInstance().unregister(secondAspect);
		}
	}
}
