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

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;
import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;

//BEGIN-JD-2010-02-01 instrumentation compatibility
import static org.junit.Assert.*;
//END-JD-2010-02-01 instrumentation compatibility

/**
 * @author Olga Gusyeva
 **/
public class TestPatternRules extends TestCase {
	
	String separationLine = "--------------------------";
	
	def ArrayList<Integer> expectedResults;
	def ArrayList<Integer> alternativeExpectedResults;
	def ArrayList<Integer> actualResults;
	def TestObject testObject;
	def HashMap<String, HashMap> mapOfRules = new HashMap<String, HashMap>();
	def HashMap specificRule = new HashMap();
	def AspectGeneratorForTest generator = new AspectGeneratorForTest();;
	def String aspectBasicName = "a";
	def int maxNumberOfAspects = 10;
	def String testName="";
	protected final static String EXCLUSION = "exclusion";
	protected final static String MUTEX = "mutex";
	protected final static String DEPENDENCY = "dependency";
	protected final static String CHOICE = "choice";
	protected final static String PRECEDENCE = "precedence";
	
	void mediatorToString() {
		System.out.println ("MEDIATOR:");
		System.out.println (AspectFactory.createMediator().toString());
		System.out.println(AspectFactory.getDefaultComparator().toString());
		System.out.println(separationLine);
	}
	
	void initializeAspectManager() {
//		Booter.initialize();
		AspectFactory.setDefaultComparator(new PrecedenceComparator());
		AspectFactory.setDefaultMediator(new RelationDefinerFacade());
	}
	
	void generateAspects(int numberOfAspects,
	HashMap<String, HashMap>... mapOfRules) {
		generator.generateAspects(numberOfAspects, aspectBasicName, testObject,
		mapOfRules);
	}
	
	void generateAspects(int numberOfAspects) {
				generator.generateAspects(numberOfAspects, aspectBasicName, testObject);
			}
	
	void generateAspects(int fromAspectNumber, int toAspectNumber,
	HashMap<String, HashMap>... mapOfRules) {
		generator.generateAspects(fromAspectNumber, toAspectNumber, aspectBasicName, testObject,
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
	
	void unregister(int n) {
		Aspect a;
		for (int i = 1; i <= n; i++) {
			a = InteractionAwareAspectManager.getInstance().getAspect(
					aspectBasicName + i);
			InteractionAwareAspectManager.getInstance().unregister(a);
		}
	}
	
	void printResults() {
		System.out.println("Expected results: " + expectedResults);
		System.out.println("Actual results: " + actualResults);
		
	}
	
	void printResultsWithAlternative() {
		System.out.println("Expected results: " + expectedResults + " or " + alternativeExpectedResults);
		System.out.println("Actual results: " + actualResults);
		
	}
	
	def initialize = {
		System.out.println(separationLine);
		initializeAspectManager();
		
		def actualResults = new ArrayList<Integer>();
		def expectedResults = new ArrayList<Integer>();
		def alternativeExpectedResults = new ArrayList<Integer>();
		testObject = new TestObject();
		def testName="";
		
	}
	
	/*
	 * Invokes a method based on a substring of the actual method name of the
	 * TestObject-Class.
	 */
	
	public ArrayList<Integer> makeMethodCall(String advice, TestObject to) {
		
		ArrayList<Integer> ar = new ArrayList<Integer>();
		
//		String cname = to.getClass().getName();
//		Class cls = Class.forName(cname);
		String methodName = "testMethod" + advice;
//		Method meth = cls.getMethod(methodName);
//		meth.invoke(to);
		to."$methodName"();
		ar = to.results;
		return ar;
		
	}
	
	/*
	 * A pattern for further unit test, dependent on the substring of the method
	 * name to be invoked as well as the expected results of the test.
	 */
	
	void testPattern(String s, ArrayList<Integer> expectedResults, TestObject to) {
		actualResults = makeMethodCall(s, to);
		printResults();
		if (expectedResults != null) {
			assertTrue(actualResults.equals(expectedResults));
		} else {
			fail();
		}
		
	}
	
	void testPatternNoAssertion(String s, TestObject to) {
		actualResults=makeMethodCall(s, to);
		println "Interaction order: " + actualResults;
		
	}
	
	void testPatternForTwoPossibleResults(String s, ArrayList<Integer> expectedResults1, ArrayList<Integer> expectedResults2, TestObject to) {
		actualResults = makeMethodCall(s, to);
		printResultsWithAlternative();
		if (expectedResults != null) {
			assertTrue((actualResults.equals(expectedResults1))||(actualResults.equals(expectedResults2)));
		} else {
			fail();
		}
	}
}
