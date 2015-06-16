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
import de.tud.stg.popart.aspect.CCCombiner;
import de.tud.stg.popart.aspect.extensions.OrderedAspectManager;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManagerFactoryImpl;
import de.tud.stg.popart.aspect.extensions.OrderedAspectManagerFactoryImpl;
import de.tud.stg.popart.aspect.extensions.RuleBasedCCCombiner;
import de.tud.stg.popart.aspect.extensions.definers.IRelationExaminer;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;

/**
 * @author olga
 */
public class OverheadNoRules{
	
	double getTaskTime(int n, String aspectBasicName) {	
		AspectFactory.setInstance(new OrderedAspectFactoryImpl());
		AspectManagerFactory.setInstance(new OrderedAspectManagerFactoryImpl());
		def testObject = new TestObject();
		
		def aspect = { map, definition ->
			def result = new CCCombiner().eval(map,definition);
			InteractionAwareAspectManager.getInstance().register(result);
			return result;
		}
		for ( i in 1..n ) {
			String aspectName = aspectBasicName + i
			aspect(name:aspectName) {
				
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
			Aspect a = OrderedAspectManager.getInstance().getAspect(
					aspectBasicName + i);
			OrderedAspectManager.getInstance().unregister(a);
		}
		return taskTimeMs;
	}
	
	
}
