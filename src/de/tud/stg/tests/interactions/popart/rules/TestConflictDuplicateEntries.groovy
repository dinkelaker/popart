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
package de.tud.stg.tests.interactions.popart.rules

import de.tud.stg.popart.aspect.extensions.definers.Relation;

//BEGIN-JD-2010-02-01 instrumentation compatibility
import static org.junit.Assert.*;
//END-JD-2010-02-01 instrumentation compatibility

/**
 * @author olga
 */
public class TestConflictDuplicateEntries extends TestPatternRules {
	public void tearDown(){
		unregister();
	}
	
	public void testBefore_DEIndependency() {
		
		initialize();
		testName="Duplicate entries:"
		def kindOfRule = Relation.INDEPENDENCY;
		specificRule.put("list", "[a1, a3, a1]");
		mapOfRules.put(kindOfRule, specificRule);
		println "Test $testName. $kindOfRule $specificRule"
		try {
			generateAspects(3, mapOfRules);
			
			testPattern("Before", expectedResults, testObject);
			fail("The test expected the testPattern method to throw an Exception.")
			
		}
		catch (Exception e) {
			println e.message;
			assertTrue(e.message.contains("duplicate entries"));
		}
	}
	
	public void testBefore_DEMutex() {
		
		initialize();
		testName="Duplicate entries:"
		def kindOfRule = Relation.MUTEX;
		specificRule.put("list", "[a1, a3, a1]");
		mapOfRules.put(kindOfRule, specificRule);
		println "Test $testName. $kindOfRule $specificRule"
		try {
			generateAspects(3, mapOfRules);
			
			testPattern("Before", expectedResults, testObject);
			fail("The test expected the testPattern method to throw an Exception.")
			
		}
		catch (Exception e) {
			println e.message;
			assertTrue(e.message.contains("duplicate entries"));
		}
	}
	
	public void testBefore_DEDependency() {
		
		initialize();
		testName="Duplicate entries:"
		def kindOfRule = Relation.DEPENDENCY;
		specificRule.put("from", "a1");
		specificRule.put("toArray", "[a1, a3, a2]");
		mapOfRules.put(kindOfRule, specificRule);
		println "Test $testName. $kindOfRule $specificRule"
		try {
			generateAspects(3, mapOfRules);
			
			testPattern("Before", expectedResults, testObject);
			fail("The test expected the testPattern method to throw an Exception.")
			
		}
		catch (Exception e) {
			println e.message;
			assertTrue(e.message.contains("duplicate entries"));
		}
	}
	
	public void testBefore_DEDependency2() {
		
		initialize();
		testName="Duplicate entries:"
		def kindOfRule = Relation.DEPENDENCY;
		specificRule.put("from", "a1");
		specificRule.put("toArray", "[a2, a3, a3]");
		mapOfRules.put(kindOfRule, specificRule);
		println "Test $testName. $kindOfRule $specificRule"
		try {
			generateAspects(3, mapOfRules);
			
			testPattern("Before", expectedResults, testObject);
			fail("The test expected the testPattern method to throw an Exception.")
			
		}
		catch (Exception e) {
			println e.message;
			assertTrue(e.message.contains("duplicate entries"));
		}
	}
	
	
}
