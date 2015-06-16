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
package de.tud.stg.tests.interactions.popart.rules;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.definers.IRelationExaminer;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;
import de.tud.stg.popart.aspect.extensions.definers.Relation;

//BEGIN-JD-2010-02-01 instrumentation compatibility
import static org.junit.Assert.*;
//END-JD-2010-02-01 instrumentation compatibility

public class TestConflictDirectDiffRules extends TestPatternRules {
	private List getApplicablePAs() {
		Aspect a1 = InteractionAwareAspectManager.getInstance().getAspect("a1");
		Aspect a2 =InteractionAwareAspectManager.getInstance().getAspect("a2");
		Aspect a3 = InteractionAwareAspectManager.getInstance().getAspect("a3");
		List applicablePAs = new ArrayList();
		applicablePAs.add(a1.findAllPointcutsAndAdvice().get(0));
		applicablePAs.add(a2.findAllPointcutsAndAdvice().get(0));
		applicablePAs.add(a3.findAllPointcutsAndAdvice().get(0));
		return (List)applicablePAs;
	}
	
	private HashSet getInteractingAspects() {
		Aspect a1 = InteractionAwareAspectManager.getInstance().getAspect("a1");
		Aspect a2 =InteractionAwareAspectManager.getInstance().getAspect("a2");
		Aspect a3 = InteractionAwareAspectManager.getInstance().getAspect("a3");
		Set setOfAspects = new HashSet();
		setOfAspects.add(a1);
		setOfAspects.add(a2);
		setOfAspects.add(a3);
		return setOfAspects;
	}
	
	public void tearDown() {
		unregister();
	};
	
	public void testBefore_MutexWithoutPrecedence() {
		testName = "MUTEX without PRECEDENCE"
		initialize();
		def kindOfRule = Relation.MUTEX;
		mapOfRules = new HashMap<String, HashMap>();
		specificRule = new HashMap();
		specificRule.put("list", "[a1, a2]");
		mapOfRules.put(kindOfRule, specificRule);
		generateAspects(3, mapOfRules);
		println "Test $testName. $kindOfRule $specificRule"
		List applicablePAs = getApplicablePAs();
		
		try {
			InteractionAwareAspectManager.getInstance().applyExclusionRules(applicablePAs);
			fail("Expected exception: RuleInconsistencyException");
		} catch (RuleInconsistencyException e) {
			unregister();
			println e;
			assertTrue(e.message.contains("inconsistency"));
			assertTrue(e.message.contains("Mutual"));
			assertTrue(e.message.contains("Precedence"));
		}
	}
	
	public void testBefore_ExclusionDependencyConflict() {
		testName = "Direct MUTEX-DEP conflict"
		initialize();
		def kindOfRule = Relation.MUTEX;
		mapOfRules = new HashMap<String, HashMap>();
		specificRule.put("list", "[a1, a2]");
		mapOfRules.put(kindOfRule, specificRule);
		
		def kindOfRule2 = Relation.DEPENDENCY;
		HashMap<String, Set<String>> mapOfRules2 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule2 = new HashMap<String, Set<String>>();
		
		specificRule2.put("from", "a1");
		specificRule2.put("to", "a2");
		mapOfRules2.put(kindOfRule2, specificRule2);
		generateAspects(3, mapOfRules, mapOfRules2);
		
		println "Test $testName. $kindOfRule $specificRule, $kindOfRule2 $specificRule2" 
		List applicablePAs = getApplicablePAs();
		IRelationExaminer facade = (IRelationExaminer)AspectFactory.createMediator();
		Set aspects = getInteractingAspects();
		try {
			facade.checkRulesConsistency(aspects);
			fail("Expected exception: RuleInconsistencyException");
		} catch (RuleInconsistencyException e) {
			println e;
			assertTrue(e.message.contains("inconsistency"));
			assertTrue(e.message.contains("DEPENDENCY"));
			assertTrue(e.message.contains("MUTEX"));
		}
		
	}
	
	public void testBefore_IndepDependencyConflict() {
		testName = "Direct INDEP-DEP conflict"
		initialize();
		def kindOfRule = Relation.INDEPENDENCY;
		mapOfRules = new HashMap<String, HashMap>();
		specificRule.put("list", "[a1, a2]");
		mapOfRules.put(kindOfRule, specificRule);
		
		def kindOfRule2 = Relation.DEPENDENCY;
		HashMap<String, Set<String>> mapOfRules2 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule2 = new HashMap<String, Set<String>>();
		
		specificRule2.put("from", "a1");
		specificRule2.put("to", "a2");
		mapOfRules2.put(kindOfRule2, specificRule2);
		generateAspects(3, mapOfRules, mapOfRules2);
		
		println "Test $testName. $kindOfRule $specificRule, $kindOfRule2 $specificRule2" 
		List applicablePAs = getApplicablePAs();
		IRelationExaminer facade = (IRelationExaminer)AspectFactory.createMediator();
		Set aspects = getInteractingAspects();
		try {
			facade.checkRulesConsistency(aspects);
			fail("Expected exception: RuleInconsistencyException");
		} catch (RuleInconsistencyException e) {
			println e;
			assertTrue(e.message.contains("inconsistency"));
			assertTrue(e.message.contains("DEPENDENCY"));
			assertTrue(e.message.contains("INDEPENDENCY"));
		}
		
	}
	
	public void testBefore_IdepMutexConflict() {
		testName = "Direct INDEP-MUTEX conflict"
		initialize();
		
		HashMap<String, Set<String>> mapOfRules2 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule2 = new HashMap<String, Set<String>>();
		
		def kindOfRule2 = Relation.INDEPENDENCY;
		specificRule2.put("list", "[a1, a2, a3]");
		mapOfRules2.put(kindOfRule2, specificRule2);
		
		HashMap<String, Set<String>> mapOfRules3 = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> specificRule3 = new HashMap<String, Set<String>>();
		def kindOfRule3 = Relation.MUTEX;
		specificRule3.put("list", "[a1, a2]");
		mapOfRules3.put(kindOfRule3, specificRule3);
		
		generateAspects(3, mapOfRules2, mapOfRules3);
		
		println "Test $testName. $kindOfRule2 $specificRule2 ; $kindOfRule3 $specificRule3"
		List applicablePAs = getApplicablePAs();
		Set aspects = getInteractingAspects();
		IRelationExaminer facade = (IRelationExaminer)AspectFactory.createMediator();
		try {
			facade.checkRulesConsistency(aspects);
			fail("Expected exception: RuleInconsistencyException");
		}
		catch (RuleInconsistencyException e){
			println e;
			assertTrue(e.message.contains("MUTEX"));
			assertTrue(e.message.contains("INDEPENDENCY"));
		}
	}
}
