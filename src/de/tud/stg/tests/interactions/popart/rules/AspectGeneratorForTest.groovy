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

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.aspect.extensions.definers.Relation;

/**
 * @author Olga Gusyeva
 **/
public class AspectGeneratorForTest {
	
	
	/*
	 * Template for automatic generation of aspects, total amount of @numberOfAspects,
	 * names based on @aspectBasicName string. 
	 */
	//TODO: Detailierte Beschreibung der Methode: Input- bzw. Outputdaten!
	public AspectGeneratorForTest(){
		super();
	}
	
	def aspectTemplateTestNoRules = {int numberOfAspects, String aspectBasicName, TestObject testObject ->
		for ( i in fromAspectNumber..toAspectNumber ) {
			def aspectName = "$aspectBasicName$i"
			ef id = i
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
	def aspectTemplateTest = {int numberOfAspects, String aspectBasicName, TestObject testObject, HashMap<String, HashMap>... someMapsOfRules ->
		aspectTemplateTestFromTo(1, numberOfAspects, aspectBasicName, testObject, someMapsOfRules);
	}
	def aspectTemplateTestFromTo = {int fromAspectNumber, int toAspectNumber, String aspectBasicName, TestObject testObject, HashMap<String, HashMap>... someMapsOfRules ->
		def aspect = { map, definition ->
			
			def result = new RuleBasedCCCombiner().eval(map,definition);
			InteractionAwareAspectManager.getInstance().register(result);
			//println "Aspect after generating: " + result.getPointcutAndAdviceSize();
			return result;
		}
		
		for ( i in fromAspectNumber..toAspectNumber ) {
			def aspectName = "$aspectBasicName$i"
			//println "Initializing aspect $aspectName"
			//println "generating $aspectName..."
			aspect(name:aspectName) {
				for (int j = 0; j < someMapsOfRules.size(); j++) {
					HashMap<String, HashMap> mapOfRules = someMapsOfRules[j];
					if (mapOfRules.keySet().contains(Relation.DEPENDENCY)) {
						HashMap params = (mapOfRules.get(Relation.DEPENDENCY))
						String from = (params.get("from")).toString();			
						if (params.keySet().contains("to")) {
							String to = (params.get("to")).toString();
							ArrayList<String> toListOfAspectNames = new ArrayList<String>(
							Arrays.asList(to));
							declare_dependency (from: from, to: to);
							
						} else if (params.keySet().contains("toArray")) {
							String to = (params.get("toArray")).toString();
							ArrayList<String> toListOfAspectNames = turnStringIntoArray(to);
							declare_dependency (from: from, toArray: toListOfAspectNames);
							
						}
					}
					if (mapOfRules.keySet().contains(Relation.PRECEDENCE)) {
						HashMap params = (mapOfRules.get(Relation.PRECEDENCE))
						String precedenceListString = (params.get("list")).toString();
						ArrayList<String> precedenceList = turnStringIntoArray(precedenceListString);
						declare_precedence(precedenceList)
					}
					if (mapOfRules.keySet().contains(Relation.MUTEX)) {
						HashMap params = (mapOfRules.get(Relation.MUTEX))
						String mutexListString = (params.get("list")).toString();
						ArrayList<String> mutexList = turnStringIntoArray(mutexListString);
						declare_mutex(mutexList)
					}
					
					if (mapOfRules.keySet().contains(Relation.INDEPENDENCY)) {
						
						HashMap params = (mapOfRules.get(Relation.INDEPENDENCY))
						String from = (params.get("from")).toString();			
						if (params.keySet().contains("to")) {
							String to = (params.get("to")).toString();
							ArrayList<String> toListOfAspectNames = new ArrayList<String>(
							Arrays.asList(to));
							declare_independency (from: from, to: to);
							
						} else if (params.keySet().contains("toArray")) {
							String to = (params.get("toArray")).toString();
							ArrayList<String> toListOfAspectNames = turnStringIntoArray(to);
							declare_independency (from: from, toArray: toListOfAspectNames);
							
						}
						else if (params.keySet().contains("list")) {
							String listString = (params.get("list")).toString();
							ArrayList<String> list = turnStringIntoArray(listString);
							declare_independency(list);
						}
					}
					
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
	}
	
	
	private ArrayList<String> turnStringIntoArray(String s) {
		ArrayList<String> list = new ArrayList<String>();
		String coma;
		int comaIndex = 0;
		boolean isComa = false;
		int i = 0;
		int j = 0;
		String aspectName = "";
		
		s = s.substring(1, s.length() - 1);
		s = s.replaceAll(" ", "");
		
		while (i < s.length()) {
			while (((!isComa) && (j < s.length()))) {
				coma = Character.toString(s.charAt(j));
				isComa = coma.equals(",");
				comaIndex = j;
				j++;
			}
			if (comaIndex < s.length() - 1)
				aspectName = s.substring(i, comaIndex);
			
			else
				aspectName = s.substring(i);
			list.add(aspectName);
			
			isComa = false;
			i = comaIndex + 1;
			j = i;
		}
		return list;
	}
	
	
	void generateAspects (int numberOfAspects, String aspectBasicName, TestObject testObject, HashMap<String, HashMap>... mapOfRules) {
		aspectTemplateTest(numberOfAspects, aspectBasicName, testObject, mapOfRules)
	}
	
	void generateAspects (int fromAspectNumber, int toAspectNumber, String aspectBasicName, TestObject testObject, HashMap<String, HashMap>... mapOfRules) {
		aspectTemplateTestFromTo(fromAspectNumber, toAspectNumber, aspectBasicName, testObject, mapOfRules)
	}
	
	
	
	
}