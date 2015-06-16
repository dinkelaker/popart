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

import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.definers.Relation;

/**
 * @author olga
 */
public class TestInteractionDetector extends TestPatternInteractionAware{
	
	public void testBefore_MutexPrecedence3Aspects() {
		
		initialize();
		def kindOfRule = Relation.MUTEX;
		specificRule.put("list", "[a3, a2]");
		mapOfRules.put(kindOfRule, specificRule);
		
		HashMap<String, Set<String>> mapOfRules2 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule2 = new HashMap<String, Set<String>>();
		
		def kindOfRule2 = Relation.PRECEDENCE;
		specificRule2.put("list", "[a2, a1, a3]");
		mapOfRules2.put(kindOfRule2, specificRule2);
		
		generateAspects(3, mapOfRules, mapOfRules2);
		
		println "Test Before. $kindOfRule $specificRule ; $kindOfRule2 $specificRule2"
		contextMap = new HashMap<String, HashSet<Object>>();
		mapOfSpecifiedRules = new HashMap<String, HashSet<String>>();
		mapOfNonspecifiedRules = new HashMap<String, HashSet<String>>();
		
		def HashSet<String> set1 = ["a1", "a3"]
		def HashSet<String> set2 = ["a3"]
		def HashSet<String> set3 = ["a1", "a2", "a3"];
		
		def mutexMap = new HashMap<String, Set<String>>();
		def precedenceMap = new HashMap<String, Set<String>>();
		mutexMap.put("a3", new HashSet<String>(Arrays.asList("a2")));
		mutexMap.put("a2", new HashSet<String>(Arrays.asList("a3")));
		mapOfSpecifiedRules.put(kindOfRule, mutexMap)
		precedenceMap.put("a2", set1)
		precedenceMap.put("a1", set2)
		mapOfSpecifiedRules.put(kindOfRule2, precedenceMap)
		testPattern("Before", testObject, mapOfSpecifiedRules, mapOfNonspecifiedRules);
				
		unregister();
	}
	
	public void testBefore_MutexPrecedence4Aspects() {
		
		initialize();
		def kindOfRule = Relation.MUTEX;
		specificRule.put("list", "[a3, a2]");
		mapOfRules.put(kindOfRule, specificRule);
		
		HashMap<String, Set<String>> mapOfRules2 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule2 = new HashMap<String, Set<String>>();
		
		def kindOfRule2 = Relation.PRECEDENCE;
		specificRule2.put("list", "[a2, a1, a3]");
		mapOfRules2.put(kindOfRule2, specificRule2);
		
		generateAspects(4, mapOfRules, mapOfRules2);
		
		println "Test Before. $kindOfRule $specificRule ; $kindOfRule2 $specificRule2"
		contextMap = new HashMap<String, HashSet<Object>>();
		mapOfSpecifiedRules = new HashMap<String, HashSet<String>>();
		mapOfNonspecifiedRules = new HashMap<String, HashSet<String>>();
		
		def HashSet<String> set1 = ["a1", "a3"]
		def HashSet<String> set2 = ["a3"]
		def HashSet<String> set3 = ["a1", "a2", "a3"];
		mapOfNonspecifiedRules.put("a4", set3);
		def mutexMap = new HashMap<String, Set<String>>();
		def precedenceMap = new HashMap<String, Set<String>>();
		mutexMap.put("a3", new HashSet<String>(Arrays.asList("a2")));
		mutexMap.put("a2", new HashSet<String>(Arrays.asList("a3")));
		mapOfSpecifiedRules.put(kindOfRule, mutexMap)
		precedenceMap.put("a2", set1)
		precedenceMap.put("a1", set2)
		mapOfSpecifiedRules.put(kindOfRule2, precedenceMap)
		testPattern("Before", testObject, mapOfSpecifiedRules, mapOfNonspecifiedRules);
				
		unregister();
	}
	
	public void testBefore_MutexIndependencyDependencyPrecedence3Aspects() {
		
		initialize();
		def kindOfRule = Relation.MUTEX;
		specificRule.put("list", "[a3, a2]");
		mapOfRules.put(kindOfRule, specificRule);
		
		HashMap<String, Set<String>> mapOfRules2 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule2 = new HashMap<String, Set<String>>();
		
		def kindOfRule2 = Relation.PRECEDENCE;
		specificRule2.put("list", "[a2, a1, a3]");
		mapOfRules2.put(kindOfRule2, specificRule2);
		
		HashMap<String, Set<String>> mapOfRules3 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule3 = new HashMap<String, Set<String>>();
		
		def kindOfRule3 = Relation.DEPENDENCY;
		specificRule3.put("from", "a1");
		specificRule3.put("to", "a2");
		mapOfRules3.put(kindOfRule3, specificRule3);
		
		HashMap<String, Set<String>> mapOfRules4 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule4 = new HashMap<String, Set<String>>();
		
		def kindOfRule4 = Relation.INDEPENDENCY;
		specificRule4.put("list", "[a3, a4]");
		mapOfRules4.put(kindOfRule4, specificRule4);
		generateAspects(3, mapOfRules, mapOfRules2, mapOfRules3, mapOfRules4);
		
		println "Test Before. $kindOfRule $specificRule; $kindOfRule2 $specificRule2; $kindOfRule3 $specificRule3; $kindOfRule4 $specificRule4"
		contextMap = new HashMap<String, HashSet<Object>>();
		mapOfSpecifiedRules = new HashMap<String, HashSet<String>>();
		mapOfNonspecifiedRules = new HashMap<String, HashSet<String>>();
		
		def HashSet<String> set1 = ["a1", "a3"]
		def HashSet<String> set2 = ["a3"]
		def HashSet<String> set3 = ["a1", "a2", "a3"];
		
		def mutexMap = new HashMap<String, Set<String>>();
		def precedenceMap = new HashMap<String, Set<String>>();
		def dependencyMap = new HashMap<String, Set<String>>();
		def independencyMap = new HashMap<String, Set<String>>();
		
		mutexMap.put("a3", new HashSet<String>(Arrays.asList("a2")));
		mutexMap.put("a2", new HashSet<String>(Arrays.asList("a3")));
		precedenceMap.put("a2", set1)
		precedenceMap.put("a1", set2)
		dependencyMap.put("a1", new HashSet<String>(Arrays.asList("a2")));
		independencyMap.put("a3", new HashSet<String>(Arrays.asList("a4")));
		independencyMap.put("a4", new HashSet<String>(Arrays.asList("a3")));
		
		
		mapOfSpecifiedRules.put(kindOfRule, mutexMap)
		mapOfSpecifiedRules.put(kindOfRule2, precedenceMap)
		mapOfSpecifiedRules.put(kindOfRule3, dependencyMap)
		mapOfSpecifiedRules.put(kindOfRule4, independencyMap)
		
		//mapOfNonspecifiedRules.put("a4", set3);
		testPattern("Before", testObject, mapOfSpecifiedRules, mapOfNonspecifiedRules);
				
		unregister();
	}
	
public void testBefore_MutexIndependencyDependencyPrecedence4Aspects() {
		
		initialize();
		def kindOfRule = Relation.MUTEX;
		specificRule.put("list", "[a3, a2]");
		mapOfRules.put(kindOfRule, specificRule);
		
		HashMap<String, Set<String>> mapOfRules2 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule2 = new HashMap<String, Set<String>>();
		
		def kindOfRule2 = Relation.PRECEDENCE;
		specificRule2.put("list", "[a2, a1, a3]");
		mapOfRules2.put(kindOfRule2, specificRule2);
		
		HashMap<String, Set<String>> mapOfRules3 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule3 = new HashMap<String, Set<String>>();
		
		def kindOfRule3 = Relation.DEPENDENCY;
		specificRule3.put("from", "a1");
		specificRule3.put("to", "a2");
		mapOfRules3.put(kindOfRule3, specificRule3);
		
		HashMap<String, Set<String>> mapOfRules4 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule4 = new HashMap<String, Set<String>>();
		
		def kindOfRule4 = Relation.INDEPENDENCY;
		specificRule4.put("list", "[a3, a4]");
		mapOfRules4.put(kindOfRule4, specificRule4);
		generateAspects(4, mapOfRules, mapOfRules2, mapOfRules3, mapOfRules4);
		
		println "Test Before. $kindOfRule $specificRule; $kindOfRule2 $specificRule2; $kindOfRule3 $specificRule3; $kindOfRule4 $specificRule4"
		contextMap = new HashMap<String, HashSet<Object>>();
		mapOfSpecifiedRules = new HashMap<String, HashSet<String>>();
		mapOfNonspecifiedRules = new HashMap<String, HashSet<String>>();
		
		def HashSet<String> set1 = ["a1", "a3"]
		def HashSet<String> set2 = ["a3"]
		def HashSet<String> set3 = ["a1", "a2", "a3"];
		
		def mutexMap = new HashMap<String, Set<String>>();
		def precedenceMap = new HashMap<String, Set<String>>();
		def dependencyMap = new HashMap<String, Set<String>>();
		def independencyMap = new HashMap<String, Set<String>>();
		
		mutexMap.put("a3", new HashSet<String>(Arrays.asList("a2")));
		mutexMap.put("a2", new HashSet<String>(Arrays.asList("a3")));
		precedenceMap.put("a2", set1)
		precedenceMap.put("a1", set2)
		dependencyMap.put("a1", new HashSet<String>(Arrays.asList("a2")));
		independencyMap.put("a3", new HashSet<String>(Arrays.asList("a4")));
		independencyMap.put("a4", new HashSet<String>(Arrays.asList("a3")));
		
		mapOfNonspecifiedRules.put("a4", set3);
		mapOfSpecifiedRules.put(kindOfRule, mutexMap)
		mapOfSpecifiedRules.put(kindOfRule2, precedenceMap)
		mapOfSpecifiedRules.put(kindOfRule3, dependencyMap)
		mapOfSpecifiedRules.put(kindOfRule4, independencyMap)
		testPattern("Before", testObject, mapOfSpecifiedRules, mapOfNonspecifiedRules);
				
		unregister();
	}
	
		
}
