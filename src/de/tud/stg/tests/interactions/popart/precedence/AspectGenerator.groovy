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
package de.tud.stg.tests.interactions.popart.precedence;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.AspectFactory;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.Booter;

/**
 * @author Olga Gusyeva
 **/
public class AspectGenerator {
	
	
	/*
	 * Template for automatic generation of aspects, total amount of @numberOfAspects,
	 * names based on @aspectBasicName string. 
	 */
	//TODO: More details about description of the method: input and output data!
	
	def aspectTemplate = {int numberOfAspects, String aspectBasicName, TestObject testObject, ArrayList<String>... precedenceList ->
		def aspect = { map, definition ->
			def result = new RuleBasedCCCombiner().eval(map,definition);
			AspectManager.getInstance().register(result);
			return result;
		}	
		
		for ( i in 1..numberOfAspects ) {
			def aspectName = "$aspectBasicName$i"
			//println "Initializing aspect $aspectName"
			aspect(name:aspectName) {
				
				if (precedenceList!=null) {
					for ( j in 0..precedenceList.size()-1 )
					declare_precedence precedenceList[j];
				}

				def id = i
				before(method_execution("testMethodBefore.*")) {
					//println "$aspectName execution before"
					testObject.results.add(id)
				}
				after(method_execution("testMethodAfter.*")) {
					testObject.results.add(id)
					//println "$aspectName execution after"
				}
				
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
				
			}
		}
	}
	
	
	void generateAspects(int numberOfAspects, String aspectBasicName, TestObject testObject, ArrayList<String>... precedenceList) {
		aspectTemplate(numberOfAspects, aspectBasicName, testObject, precedenceList);
	}
	

	
	
	
	//  Declaration of aspect precedence 
	//def precedenceList = ["Aspect2", "Aspect1"]
	//def precedenceList2 = ["Aspect1", "Aspect3"]
	//def precedenceList3 = ["Aspect1", "Aspect2"]
	//PrecedenceDSL.declare_precedence(precedenceList)
	//PrecedenceDSL.declare_precedence(precedenceList2)
	//PresedenceDSL.declare_precedence(precedenceList3)
	
}