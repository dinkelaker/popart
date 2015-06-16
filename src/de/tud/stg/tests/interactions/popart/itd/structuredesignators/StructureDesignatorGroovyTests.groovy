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
package de.tud.stg.tests.interactions.popart.itd.structuredesignators

import junit.framework.TestCase

import de.tud.stg.popart.aspect.*
import de.tud.stg.popart.aspect.extensions.itd.*
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.*

import static org.junit.Assert.*;

/**
 * This test validates the functionallty of all {@link StructureDesignator}s
 * @author Joscha Drechsler
 */
public class StructureDesignatorGroovyTests extends TestCase{

	public void testSds(){
		println "Now testing all Structure Designators true and false evaluations"
		def aspect = { map, definition ->
			return new ITDCCCombiner().eval(map,definition)
		}
		Aspect myAspect = aspect(name:"MyAspect") { 
			StructureDesignator sd;

			// === Type Matching ===
			
			sd = is_class(StructureDesignatorGroovyTests)
			assertTrue("is_class(Class) true", sd.matches(this.class))
			assertFalse("is_class(Class) false", sd.matches(sd.class))
			
			sd = is_class(".*GroovyTests")
			assertTrue("is_class(String) true", sd.matches(this.class))
			assertFalse("is_class(String) false", sd.matches(sd.class))
			
			sd = is_class(~/ClassPatternSD/)
			assertFalse("is_class(Pattern) false", sd.matches(this.class))
			assertTrue("is_class(Pattern) true", sd.matches(sd.class))

			// === Sub Type Matching ===
			
			sd = is_type(TestCase)
			assertTrue("is_type(Class) true", sd.matches(this.class))
			assertFalse("is_type(Class) false", sd.matches(sd.class))

			sd = is_type(".*Case")
			assertTrue("is_type(String) true", sd.matches(this.class))
			assertFalse("is_type(String) false", sd.matches(sd.class))
			
			sd = is_type(~/TypePatternSD/)
			assertFalse("is_type(Pattern) false", sd.matches(this.class))
			assertTrue("is_type(Pattern) true", sd.matches(sd.class))
			
			// === Package Matching ===

			sd = within_package(StructureDesignator.class.getPackage())
			assertFalse("within_package(Package) false", sd.matches(this.class))
			assertTrue("within_package(Package) true", sd.matches(sd.class))
			
			sd = within_package(".*tests.*")
			assertTrue("within_package(String) true", sd.matches(this.class))
			assertFalse("within_package(String) false", sd.matches(sd.class))
			
			sd = within_package(~/de\.tud\.stg\.popart.*/)
			assertFalse("within_package(Pattern) false", sd.matches(this.class))
			assertTrue("within_package(Pattern) true", sd.matches(sd.class))
			
			// === Sub Package Matching ===
			
			sd = within_package_hierarchy(InterTypeDeclarationAspect.class.getPackage())
			assertFalse("within_package_hierarchy(Package) false", sd.matches(this.class))
			assertTrue("within_package_hierarchy(Package) true", sd.matches(sd.class))
			
			sd = within_package_hierarchy(".*tests")
			assertTrue("within_package_hierarchy(String) true", sd.matches(this.class))
			assertFalse("within_package_hierarchy(String) false", sd.matches(sd.class))
			
			sd = within_package_hierarchy(~/de\.tud\.stg\.popart/)
			assertFalse("within_package_hierarchy(Pattern) false", sd.matches(this.class))
			assertTrue("within_package_hierarchy(Pattern) true", sd.matches(sd.class))
			
			// === Fully Qualified Name Matching ===
			
			sd = canonical_name(".*tud.*Tests")
			assertTrue("canonical_name(String) true", sd.matches(this.class))
			assertFalse("canonical_name(String) false", sd.matches(sd.class))
			
			sd = canonical_name(~/de\.tud\.stg.*CanonicalNamePatternSD/)
			assertFalse("canonical_name(Pattern) false", sd.matches(this.class))
			assertTrue("canonical_name(Pattern) true", sd.matches(sd.class))
			
			// === Sub Fully Qualified Name Matching ===
			
			sd = inherits_canonical_name(".*junit.*TestCase")
			assertTrue("inherits_canonical_name(String) true", sd.matches(this.class))
			assertFalse("inherits_canonical_name(String) false", sd.matches(sd.class))
			
			sd = inherits_canonical_name(~/de\.tud\.stg.*StructureDesignator/)
			assertFalse("inherits_canonical_name(Pattern) false", sd.matches(this.class))
			assertTrue("inherits_canonical_name(Pattern) true", sd.matches(sd.class))
			
			// === AND tests ===
			
			// false false -> false
			sd = is_class(Integer) & is_class(Double)
			assertFalse("AND false false", sd.matches(this.class))
			
			// false true -> false
			sd = is_class(Integer) & is_type(TestCase)
			assertFalse("AND false true", sd.matches(this.class))
			
			// true false -> false
			sd = within_package(~/de.*/) & is_class(Double)
			assertFalse("AND true false", sd.matches(this.class))
			
			//true true -> true
			sd = within_package(~/de.*/) & is_type(TestCase)
			assertTrue("AND true true", sd.matches(this.class))

			// === OR tests ===
			
			// false false -> false
			sd = is_class(Integer) | is_class(Double)
			assertFalse("OR false false", sd.matches(this.class))
			
			// false true -> true
			sd = is_class(Integer) | is_type(TestCase)
			assertTrue("OR false true", sd.matches(this.class))
			
			// true false -> true
			sd = within_package(~/de.*/) | is_type(Double)
			assertTrue("OR true false", sd.matches(this.class))
			
			//true true -> true
			sd = within_package(~/de.*/) | is_class(TestCase)
			assertTrue("OR true true", sd.matches(this.class))

			// === NOT tests ===
				
			sd = not(is_type(TestCase))
			assertFalse("NOT true", sd.matches(this.class))
			assertTrue("NOT false", sd.matches(sd.class))
		} 
	}
	
	public void testInterfaces(){
		println "Now testing Designator matches through class/interface hierarchy"

		StructureDesignator typeIface, typeSubIface, subTypeIface, subTypeSubIface;

		/*
		 * Object a:
		 *  - directly implements Interface
		 *  - does not implement Subinterface
		 * Object b:
		 *  - indirectly implements Interface (extends implementing class)
		 *  - does not implement Subinterface
		 * Object c:
		 *  - indirectly implements Interface (implements extending interface)
		 *  - directly implements Subinterface
		 */
		Object a = new StructureDesignatorsTestClass();
		Object b = new StructureDesignatorsTestSubClass();
		Object c = new StructureDesignatorsTestOtherClass();
		
		// String on simpleName
		StructuralPointcutDSL.eval{
			typeIface = is_class("StructureDesignatorsTestInterface");
			typeSubIface = is_class("StructureDesignatorsTestSubInterface");
			subTypeIface = is_type("StructureDesignatorsTestInterface");
			subTypeSubIface= is_type("StructureDesignatorsTestSubInterface");
		}
		
		//First check: _directly_ implements interface?
		assertTrue("typeIface simpleName a", typeIface.matches(a.class))
		assertFalse("typeIface simpleName b", typeIface.matches(b.class))
		assertFalse("typeIface simpleName c", typeIface.matches(c.class))
		assertFalse("typeIface simpleName this", typeIface.matches(this.class))
		
		//Second check: _directly_ implements subinterface?
		assertFalse("typeSubIface simpleName a", typeSubIface.matches(a.class))
		assertFalse("typeSubIface simpleName b", typeSubIface.matches(b.class))
		assertTrue("typeSubIface simpleName c", typeSubIface.matches(c.class))
		assertFalse("typeSubIface simpleName this", typeSubIface.matches(this.class))
		
		//Thrid check: implements interface?
		assertTrue("subTypeIface simpleName a", subTypeIface.matches(a.class))
		assertTrue("subTypeIface simpleName b", subTypeIface.matches(b.class))
		assertTrue("subTypeIface simpleName c", subTypeIface.matches(c.class))
		assertFalse("subTypeIface simpleName this", subTypeIface.matches(this.class))
		
		//Fourth check: implements subinterface?
		assertFalse("subTypeSubIface simpleName a", subTypeSubIface.matches(a.class))
		assertFalse("subTypeSubIface simpleName b", subTypeSubIface.matches(b.class))
		assertTrue("subTypeSubIface simpleName c", subTypeSubIface.matches(c.class))
		assertFalse("subTypeSubIface simpleName this", subTypeSubIface.matches(this.class))
		
		// Pattern on canonicalName
		StructuralPointcutDSL.eval{
			typeIface = canonical_name(~/.*\.StructureDesignatorsTestInterface/);
			typeSubIface = canonical_name(~/.*\.StructureDesignatorsTestSubInterface/);
			subTypeIface = inherits_canonical_name(~/.*\.StructureDesignatorsTestInterface/);
			subTypeSubIface= inherits_canonical_name(~/.*\.StructureDesignatorsTestSubInterface/);
		}
		
		//First check: _directly_ implements interface?
		assertTrue("typeIface canonicalName a", typeIface.matches(a.class))
		assertFalse("typeIface canonicalName b", typeIface.matches(b.class))
		assertFalse("typeIface canonicalName c", typeIface.matches(c.class))
		assertFalse("typeIface canonicalName this", typeIface.matches(this.class))
		
		//Second check: _directly_ implements subinterface?
		assertFalse("typeSubIface canonicalName a", typeSubIface.matches(a.class))
		assertFalse("typeSubIface canonicalName b", typeSubIface.matches(b.class))
		assertTrue("typeSubIface canonicalName c", typeSubIface.matches(c.class))
		assertFalse("typeSubIface canonicalName this", typeSubIface.matches(this.class))
		
		//Thrid check: implements interface?
		assertTrue("subTypeIface canonicalName a", subTypeIface.matches(a.class))
		assertTrue("subTypeIface canonicalName b", subTypeIface.matches(b.class))
		assertTrue("subTypeIface canonicalName c", subTypeIface.matches(c.class))
		assertFalse("subTypeIface canonicalName this", subTypeIface.matches(this.class))
		
		//Fourth check: implements subinterface?
		assertFalse("subTypeSubIface canonicalName a", subTypeSubIface.matches(a.class))
		assertFalse("subTypeSubIface canonicalName b", subTypeSubIface.matches(b.class))
		assertTrue("subTypeSubIface canonicalName c", subTypeSubIface.matches(c.class))
		assertFalse("subTypeSubIface canonicalName this", subTypeSubIface.matches(this.class))
		
		// ClassID
		StructuralPointcutDSL.eval{
			typeIface = is_class(StructureDesignatorsTestInterface);
			typeSubIface = is_class(StructureDesignatorsTestSubInterface);
			subTypeIface = is_type(StructureDesignatorsTestInterface);
			subTypeSubIface= is_type(StructureDesignatorsTestSubInterface);
		}
		
		//First check: _directly_ implements interface?
		assertTrue("typeIface class a", typeIface.matches(a.class))
		assertFalse("typeIface class b", typeIface.matches(b.class))
		assertFalse("typeIface class c", typeIface.matches(c.class))
		assertFalse("typeIface class this", typeIface.matches(this.class))
		
		//Second check: _directly_ implements subinterface?
		assertFalse("typeSubIface class a", typeSubIface.matches(a.class))
		assertFalse("typeSubIface class b", typeSubIface.matches(b.class))
		assertTrue("typeSubIface class c", typeSubIface.matches(c.class))
		assertFalse("typeSubIface class this", typeSubIface.matches(this.class))
		
		//Thrid check: implements interface?
		assertTrue("subTypeIface class a", subTypeIface.matches(a.class))
		assertTrue("subTypeIface class b", subTypeIface.matches(b.class))
		assertTrue("subTypeIface class c", subTypeIface.matches(c.class))
		assertFalse("subTypeIface class this", subTypeIface.matches(this.class))
		
		//Fourth check: implements subinterface?
		assertFalse("subTypeSubIface class a", subTypeSubIface.matches(a.class))
		assertFalse("subTypeSubIface class b", subTypeSubIface.matches(b.class))
		assertTrue("subTypeSubIface class c", subTypeSubIface.matches(c.class))
		assertFalse("subTypeSubIface class this", subTypeSubIface.matches(this.class))
	}
}
