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
package de.tud.stg.tests.interactions.popart.priority;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.AspectFactory;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.Booter;

/**
 * @author Tom Dinkelaker
 **/
public class NewAspectGenerator {
	
	private final boolean DEBUG = true;
	
	/**
	 * Template for automatic generation of aspects, 
	 * @params 		numberOfAspects		Total amount of aspect to be generated.
	 * @params 		aspectBasicName 	Leading prefix in name of generated aspects. 
	 * @params		testObject			Object under test, implements methods that are advised by the generated test aspects..
	 */
	def aspectTemplate = {int numberOfAspects, String aspectBasicName, TestObject testObject, ArrayList<Integer> priorityList ->
		def aspect = { map, definition ->
			def result = new CCCombiner().eval(map,definition);
			AspectManager.getInstance().register(result);
			return result;
		}
		
		if (DEBUG) println "Generating aspect: numberOfAspects=$numberOfAspects, aspectBasicName=$aspectBasicName, testObject=$testObject, priorityList=$priorityList"
		
		for ( i in 1..numberOfAspects ) {
			def aspectName = "$aspectBasicName$i"
			if (DEBUG) print "Initializing aspect $aspectName "
			
			assert priorityList.size()== numberOfAspects
			def p = priorityList[i-1];
			if (DEBUG) println "with priority $p"
			
			aspect(name:aspectName,priority:p) {
				
				def id = i
				
				before(method_execution("testMethodBefore.*")) {
					if (DEBUG) println "$aspectName execution before"
					testObject.results.add(id)
				}
				
				after(method_execution("testMethodAfter.*")) {
					if (DEBUG) println "$aspectName execution after"
					testObject.results.add(id)
				}
				
				/*
				around(method_execution("testMethodAround.*")) {
					testObject.results.add(id)
					//println "$aspectName execution around"
					
				}
				
				around(method_execution("testMethodAroundProceedBefore.*")) {
					proceed()
					testObject.results.add(id)
					//println "$aspectName execution aroundProceedBefore"
				}
				
				around(method_execution("testMethodAroundProceedAfter.*")) {
					testObject.results.add(id)
					proceed()
					//println "$aspectName execution aroundProceedAfter"
				}
				
				around(method_execution("testMethodAroundProceed2.*")) {
					testObject.results.add(id)
					proceed()
					testObject.results.add(id)
					//println "$aspectName execution aroundProceed2"
				}
				*/
				
			}
		}
	}
	
	
	void generateAspects(int numberOfAspects, String aspectBasicName, TestObject testObject, ArrayList<Integer> priorityList) {
		aspectTemplate(numberOfAspects, aspectBasicName, testObject, priorityList);
	}
	

	
}