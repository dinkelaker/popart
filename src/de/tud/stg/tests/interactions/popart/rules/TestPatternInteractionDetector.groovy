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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;
import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.aspect.extensions.definers.Verifier;
import de.tud.stg.popart.aspect.extensions.definers.Relation;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;

/**
 * @author Olga Gusyeva
 **/
public class TestPatternInteractionAware extends TestCase {
	
	String separationLine = "--------------------------";
	
	//	def ArrayList<Integer> expectedResults;
	//	def ArrayList<Integer> alternativeExpectedResults;
	//	def ArrayList<Integer> actualResults;
	def HashMap<String, HashSet<Object>> contextMap;
	def HashMap<Relation, HashMap<String, HashSet<String>>>  mapOfSpecifiedRules;
	def HashMap<String, HashSet<String>> mapOfNonspecifiedRules;
	def HashMap<String, HashSet<Object>> contextMapExp;
	def HashMap<Relation, HashMap<String, HashSet<String>>> mapOfSpecifiedRulesExp;
	def HashMap<String, HashSet<String>> mapOfNonspecifiedRulesExp;
	def TestObject testObject;
	def HashMap<String, HashMap> mapOfRules = new HashMap<String, HashMap>();
	def HashMap specificRule = new HashMap();
	def AspectGeneratorForTest generator = new AspectGeneratorForTest();;
	def String aspectBasicName = "a";
	int maxNumberOfAspects = 10;
	protected final static String EXCLUSION = "exclusion";
	protected final static String MUTEX = "mutex";
	protected final static String DEPENDENCY = "dependency";
	protected final static String CHOICE = "choice";
	protected final static String PRECEDENCE = "precedence";
	
	void mediatorToString() {
		System.out.println ("MEDIATOR:");
		System.out.println (AspectFactory.createMediator().toString());
		System.out.println(separationLine);
	}
	
	void initializeAspectManager() {
		Booter.initialize();
		
		AspectFactory.setDefaultComparator(new PrecedenceComparator());
		AspectFactory.setDefaultMediator(new RelationDefinerFacade());
	}
	
	void generateAspects(int numberOfAspects,
	HashMap<String, HashMap>... mapOfRules) {
		generator.generateAspects(numberOfAspects, aspectBasicName, testObject,
		mapOfRules);
	}
	
	void unregister() {
		Aspect a;
		for (int i = 1; i <= maxNumberOfAspects; i++) {
			a = InteractionAwareAspectManager.getInstance().getAspect(
					aspectBasicName + i);
			InteractionAwareAspectManager.getInstance().unregister(a);
		}
	}
	
	void printResults() {
		println "EXPECTED RESULTS:";
		println "Specified rules:  $mapOfSpecifiedRulesExp";
		println "Nonspecified rules:  $mapOfNonspecifiedRulesExp";
		println "ACTUAL RESULTS:";
		println "Specified rules:  $mapOfSpecifiedRules";
		println "Nonspecified rules:  $mapOfNonspecifiedRules";
		
	}
	

	
	def initialize = {
		System.out.println(separationLine);
		initializeAspectManager();
		
		
		def contextMap = new HashMap<String, HashSet<Object>>();
		def mapOfSpecifiedRules = new HashMap<Relation, HashMap<String, HashSet<String>>> ();
		def mapOfNonspecifiedRules = new HashMap<String, HashSet<String>>();
		def contextMapExp = new HashMap<String, HashSet<Object>>();
		def mapOfSpecifiedRulesExp = new HashMap<Relation, HashMap<String, HashSet<String>>> ();
		def mapOfNonspecifiedRulesExp = new HashMap<String, HashSet<String>>();
		

		testObject = new TestObject();
		
	}
	
	/*
	 * Invokes a method based on a substring of the actual method name of the
	 * TestObject-Class.
	 */
	
	public ArrayList<Integer> makeMethodCall(String advice, TestObject to) {
		
		ArrayList<Integer> ar = new ArrayList<Integer>();
		
		String cname = to.getClass().getName();
		Class cls = Class.forName(cname);
		String methodName = "testMethod" + advice;
		Method meth = cls.getMethod(methodName);
		meth.invoke(to);
		ar = to.results;
		return ar;
		
	}
	
	
void setManagerParameters(HashMap<String, Boolean> parameters) {
		InteractionAwareAspectManager.getInstance().setWarningOn(parameters.get("warn"));
		InteractionAwareAspectManager.getInstance().setReportAll(parameters.get("all"));
		InteractionAwareAspectManager.getInstance().setReportSpecifiedRules(parameters.get("spec"));
		InteractionAwareAspectManager.getInstance().setReportNonspecifiedInteractions(parameters.get("nonspec"));
		InteractionAwareAspectManager.getInstance().setThrowException(parameters.get("exc"));
	
}
	
	/*
	 * A pattern for further unit test, dependent on the substring of the method
	 * name to be invoked as well as the expected results of the test.
	 */
	
	void testPattern(String s, TestObject to, HashMap<String, HashSet<String>> mapOfSpecifiedRules_Exp, HashMap<String, HashSet<String>> mapOfNonspecifiedRules_Exp, HashMap<String, Boolean> parameters) {
		setManagerParameters(parameters);
		makeMethodCall(s, to);
		contextMap = InteractionAwareAspectManager.getInstance().getContextMap();
		mapOfSpecifiedRules = InteractionAwareAspectManager.getInstance().getMapOfSpecifiedRules();
		mapOfNonspecifiedRules = InteractionAwareAspectManager.getInstance().getMapOfNonspecifiedRules();
		mapOfSpecifiedRulesExp = mapOfSpecifiedRules_Exp;
		mapOfNonspecifiedRulesExp = mapOfNonspecifiedRules_Exp;
		printResults();
		
		if ((mapOfSpecifiedRulesExp!=null)&&(mapOfNonspecifiedRulesExp!=null)) {
			assertTrue(mapOfSpecifiedRules == mapOfSpecifiedRulesExp);
			assertTrue(mapOfNonspecifiedRules == mapOfNonspecifiedRulesExp);

		} else {
			fail();
		}
		
	}
	
	void testPattern(String s, TestObject to, HashMap<String, HashSet<String>> mapOfSpecifiedRules_Exp, HashMap<String, HashSet<String>> mapOfNonspecifiedRules_Exp) {
		def aspectManagerDefaultParameters = new HashMap<String, Boolean>();
		aspectManagerDefaultParameters.put("warn", true);
		aspectManagerDefaultParameters.put("all", true);
		aspectManagerDefaultParameters.put("spec", true);
		aspectManagerDefaultParameters.put("nonspec", true);
		aspectManagerDefaultParameters.put("exc", false);
		testPattern(s, to, mapOfSpecifiedRules_Exp, mapOfNonspecifiedRules_Exp, aspectManagerDefaultParameters);
}
}
