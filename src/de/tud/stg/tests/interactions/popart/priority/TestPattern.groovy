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

import junit.framework.TestCase;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.AspectFactory;

import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.dslsupport.DSLException; 
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.aspect.extensions.definers.Relation;

//BEGIN-JD-2010-02-01 instrumentation compatibility
import static org.junit.Assert.*;
//END-JD-2010-02-01 instrumentation compatibility

/**
 * @author Olga Gusyeva, Tom Dinkelaker
 **/
public class TestPattern extends TestCase {
	
	
	String separationLine = "--------";
	
	ArrayList<Integer> expectedResults;
	ArrayList<Integer> actualResults;
	TestObject testObject;
	AspectGenerator generator = new AspectGenerator();
	String aspectBasicName = "Aspect";
	def maxNumberOfAspects = 10;
	
	
	void initializeAspectManager() {
//		Booter.initialize();
		
		AspectFactory.setDefaultComparator(new PointcutAndAdviceComparator<AspectMember>());
		AspectFactory.setDefaultMediator(new RelationDefinerFacade());
	}
	
	void generateAspects(int numberOfAspects, ArrayList<Integer> priorityList) {
		generator.generateAspects(numberOfAspects, aspectBasicName, testObject, priorityList);
	}
	
	void unregister() {
		Aspect a;
		for (int i = 1; i<=maxNumberOfAspects; i++) {
			a = InteractionAwareAspectManager.getInstance().getAspect(aspectBasicName + i);
			InteractionAwareAspectManager.getInstance().unregister(a);
		}
	}
	
	void printResults() {
		println "Expected results: $expectedResults"
		println "Actual results: $actualResults"
		
	}
	
	def initialize = {
		initializeAspectManager()
		def actualResults = new ArrayList<Integer>();
		def expectedResults = new ArrayList<Integer>();
		testObject = new TestObject();
	}
	
	
	/*
	 * Invokes a method based on a substring of the actual 
	 * method name of the TestObject-Class.
	 */
	
	public ArrayList<Integer> makeMethodCall(String advice, TestObject to) {
		//try {
		def ArrayList<Integer> ar = new ArrayList<Integer>();
		
//		String cname = to.getClass().getName();
//		Class cls = Class.forName(cname);
		String methodName = "testMethod" + advice;
//		Method meth = cls.getMethod(methodName);
//		println "Invoking method $cname.$methodName"
//		meth.invoke(to);
		to."$methodName"();
		ar = to.results;
		return ar;
		
		//		} catch (Exception e) {
		//			System.err.println(e);
		//			return null;
		//		}
		
		
	}
	
	
	
	/*
	 * A pattern for further unit test, dependent on the substring of the method name
	 * to be invoked as well as the expected results of the test.
	 */
	
	void testPattern(String s, ArrayList<Integer> expectedResults, TestObject to) {
		actualResults = makeMethodCall(s, to);
		printResults()
		//System.out.println(separationLine);
		if ((expectedResults!=null)&&(actualResults.size()==expectedResults.size())) {
			for (int i = 0; i < expectedResults.size(); i++) {
				assertTrue(expectedResults.get(i) == actualResults.get(i));
			}
		}
		else {
			fail();	
		}
		
		
	}
}





