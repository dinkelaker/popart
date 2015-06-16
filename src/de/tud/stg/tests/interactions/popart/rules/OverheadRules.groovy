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

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectManagerFactory;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.OrderedAspectFactoryImpl;
import de.tud.stg.popart.aspect.extensions.OrderedAspectManager;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManagerFactoryImpl;
import de.tud.stg.popart.aspect.extensions.OrderedAspectManagerFactoryImpl;
import de.tud.stg.popart.aspect.extensions.RuleBasedCCCombiner;
import de.tud.stg.popart.aspect.extensions.definers.IRelationExaminer;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;
import de.tud.stg.popart.aspect.extensions.definers.Relation;
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;

/**
 * @author olga
 */
public class OverheadRules {

	
	
	double getTaskTime(int n, String aspectBasicName) {
	def testObject = new TestObject();
		
		def kindOfRule = Relation.PRECEDENCE;
		
		String list = "[";
		for (int i=n; i>1; i--) {
			list = list + " " + aspectBasicName + i + ",";	
		}
		list = list + " " + aspectBasicName + "1" + "]";
		
		
		ArrayList<String> precedenceList = new ArrayList<String>();
		for (int i=n; i>=1; i--) {
			precedenceList.add(aspectBasicName+i);
		}
		
		def aspect = { map, definition ->
			
			def result = new RuleBasedCCCombiner().eval(map,definition);
			InteractionAwareAspectManager.getInstance().register(result);
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
		
		def startTimeMs = System.currentTimeMillis( );
		testObject.testMethodBefore();
		def taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
		
		
		def results = testObject.results;
		println "Interaction order: " + results;
		for (int i = 1; i <= n; i++) {
			Aspect a = InteractionAwareAspectManager.getInstance().getAspect(
					aspectBasicName + i);
			InteractionAwareAspectManager.getInstance().unregister(a);
		}
		return taskTimeMs;
		
	}
}
