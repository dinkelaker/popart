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

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.RuleBasedCCCombiner;
import de.tud.stg.popart.aspect.extensions.definers.IRelationExaminer;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;

/**
 * @author olga
 */
public class TestFuzzy extends TestPatternRules{
	int n = 20;
	String baseName = "a";
	
	
	private List getApplicablePAs() {
		List applicablePAs = new ArrayList();
		for (int i=1; i<=n; i++) {
			String aspectName = baseName+i;
			Aspect a = InteractionAwareAspectManager.getInstance().getAspect(aspectName);
			applicablePAs.add(a.findAllPointcutsAndAdvice().get(0));
		}
		return (List)applicablePAs;
	}
	
	private HashSet getInteractingAspects() {
		Set setOfAspects = new HashSet();
		for (int i=1; i<=n; i++) {
			String aspectName = baseName+i;
			Aspect a = InteractionAwareAspectManager.getInstance().getAspect(aspectName);
			setOfAspects.add(a);
		}
		return setOfAspects;
	}
	
	public void tearDown(){
		unregister(n);
	}
	
	public void testBefore_IndirectDependency() {
		testName="Indirect DEPENDENCY, no PRECEDENCE, $n Aspects"
		initialize();
		
		def aspect = { map, definition ->
			def result = new RuleBasedCCCombiner().eval(map,definition);
			InteractionAwareAspectManager.getInstance().register(result);
			return result;
		}

		
		for ( i in 1..n ) {
			String aspectName = aspectBasicName + i
			//println "Initializing aspect $aspectName"
			//println "generating $aspectName..."
			
			
			if ((i % 3 == 0)&&(i<n-3)) {
				ArrayList<String> dependencyList = new ArrayList<String>();
				int x = i+1;
				int y = i+2;
				int z=i+3;
				dependencyList.add(aspectBasicName + x);
				dependencyList.add(aspectBasicName + y);
				dependencyList.add(aspectBasicName + z);
				
			}
			aspect(name:aspectName) {
				if (((i-1) % 5 == 0)&&(i<n-5)) {
					ArrayList<String> dependencyList = new ArrayList<String>();
					int x = i+1;
					int y = i+2;
					int z=i+3;
					int q = i+4;
					String a1 = aspectName;
					String a2 = aspectBasicName + x
					String a3 = aspectBasicName + y;
					String a4 = aspectBasicName + z;
					String a5 = aspectBasicName + q;
					
					dependencyList.add(a2);
					dependencyList.add(a3);
					dependencyList.add(a4);
					declare_dependency(from: a1, to: a2);
					declare_dependency(from: a2, to: a3);
					declare_dependency(from: a3, to: a4);
					
					ArrayList<String> mutexList = new ArrayList<String>();
					mutexList.add(a2);
					mutexList.add(a3);
					//declare_mutex(mutexList);
					
					ArrayList<String> choiceList = new ArrayList<String>();
					choiceList.add(a4);
					choiceList.add(a5);
					declare_independency(choiceList);
				}
				
				def id = i
				before(method_execution("testMethodBefore.*")) {
					testObject.results.add(id)
				}
				
				
				around(method_execution("testMethodAround.*")) {
					testObject.results.add(id)
				}
				after(method_execution("testMethodAfter.*")) {
					testObject.results.add(id)
				}
			}
		}
		
		expectedResults = [4, 3, 2, 1, 5, 9, 8, 7, 6, 10, 14, 13, 12, 11, 15, 16, 17, 18, 19, 20, 0];		
		println "Test $testName."
		mediatorToString();
		testPattern("Before", expectedResults, testObject);
	} 
	
	public void testBefore_IndirectDependencyMutexPrecedence() {
		testName="Indirect DEPENDENCY, MUTEX, PRECEDENCE, $n Aspects"
		initialize();
		def kindOfRule = Relation.PRECEDENCE;
		
		String list = "[";
		for (int i=n; i>1; i--) {
			list = list + " " + baseName + i + ",";	
		}
		list = list + " " + baseName + "1" + "]";
		mapOfRules = new HashMap<String, HashMap>();
		specificRule.put("list", list);
		mapOfRules.put(kindOfRule, specificRule);
		
		
		ArrayList<String> precedenceList = new ArrayList<String>();
		for (int i=n; i>=1; i--) {
			precedenceList.add(baseName+i);
		}
		
		def aspect = { map, definition ->
			
			def result = new RuleBasedCCCombiner().eval(map,definition);
			InteractionAwareAspectManager.getInstance().register(result);
			//println "Aspect after generating: " + result.getPointcutAndAdviceSize();
			return result;
		}
		//precedence aspect
		aspect(name: "a1") {
			declare_precedence(precedenceList);
			def id = 1
			before(method_execution("testMethodBefore.*")) {
				testObject.results.add(id)
			}
			
			
			around(method_execution("testMethodAround.*")) {
				testObject.results.add(id)
			}
			after(method_execution("testMethodAfter.*")) {
				testObject.results.add(id)
			}
		}
		
		for ( i in 2..n ) {
			String aspectName = aspectBasicName + i
			//println "Initializing aspect $aspectName"
			//println "generating $aspectName..."
			
			
			if ((i % 3 == 0)&&(i<n-3)) {
				ArrayList<String> dependencyList = new ArrayList<String>();
				int x = i+1;
				int y = i+2;
				int z=i+3;
				dependencyList.add(aspectBasicName + x);
				dependencyList.add(aspectBasicName + y);
				dependencyList.add(aspectBasicName + z);
				
			}
			aspect(name:aspectName) {
				if (((i-1) % 5 == 0)&&(i<n-5)) {
					ArrayList<String> dependencyList = new ArrayList<String>();
					int x = i+1;
					int y = i+2;
					int z=i+3;
					int q = i+4;
					String a1 = aspectName;
					String a2 = aspectBasicName + x
					String a3 = aspectBasicName + y;
					String a4 = aspectBasicName + z;
					String a5 = aspectBasicName + q;
					
					dependencyList.add(a2);
					dependencyList.add(a3);
					dependencyList.add(a4);
					declare_dependency(from: a2, to: a1);
					declare_dependency(from: a3, to: a2);
					
					
					ArrayList<String> mutexList = new ArrayList<String>();
					mutexList.add(a3);
					mutexList.add(a4);
					declare_mutex(mutexList);
					
					ArrayList<String> choiceList = new ArrayList<String>();
					choiceList.add(a4);
					choiceList.add(a5);
					declare_independency(choiceList);
				}
				
				def id = i
				before(method_execution("testMethodBefore.*")) {
					testObject.results.add(id)
				}
				
				
				around(method_execution("testMethodAround.*")) {
					testObject.results.add(id)
				}
				after(method_execution("testMethodAfter.*")) {
					testObject.results.add(id)
				}
			}
		}
		
		expectedResults = [20, 19, 18, 17, 16, 15, 14, 12, 11, 10, 9, 7, 6, 5, 4, 3, 2, 1, 0];		
		println "Test $testName"
		mediatorToString();
		testPattern("Before", expectedResults, testObject);
	} 
}
