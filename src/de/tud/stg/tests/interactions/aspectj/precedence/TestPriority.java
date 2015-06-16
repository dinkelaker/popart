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
package de.tud.stg.tests.interactions.aspectj.precedence;

import java.lang.reflect.Method;
import java.util.ArrayList;

import junit.framework.TestCase;
import de.tud.stg.tests.interactions.aspectj.precedence.TestObject;

import org.junit.Test;

public class TestPriority extends TestCase {

	private String separationLine = "--------";
	ArrayList<Integer> actualResults;
	ArrayList<Integer> expectedResults;

	void initialize() {
		actualResults = new ArrayList<Integer>();
		expectedResults = new ArrayList<Integer>();
	}

	void printResults() {
		System.out.print ("Expected results: [");
		int i=0;
		for (i=0; i < expectedResults.size()-1; i++)
			System.out.print(expectedResults.get(i) + ", ");
		System.out.println(expectedResults.get(i) + "]");
		
		
		System.out.print ("Actual results: [");
		int j=0;
		for (j=0; j < actualResults.size()-1; j++)
			System.out.print(actualResults.get(j) + ", ");
		System.out.println(actualResults.get(j) + "]");
		

	}
	
	
	/*
	 * Invokes a method based on a substring of the actual 
	 * method name of the TestObject-Class.
	 */

	public ArrayList<Integer> makeMethodCall(String advice) {
		try {
			TestObject to = new TestObject();
			ArrayList<Integer> ar = new ArrayList<Integer>();
			String cname = to.getClass().getName();
			Class cls = Class.forName(cname);
			String methodName = "testMethod" + advice;
			Method meth = cls.getMethod(methodName);
			meth.invoke(to);
			ar = to.results;
			return ar;
			
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}
	/*
	 * A pattern for further unit test, dependent on the substring of the method name
	 * to be invoked as well as the expected results of the test.
	 */
	
	public void testPattern(String s, ArrayList<Integer> expectedResults) {
		actualResults=makeMethodCall(s);
		printResults();
		System.out.println(separationLine);
		//assertTrue(trueResults.size() == results.size());
		for (int i = 0; i < actualResults.size(); i++)
			assertTrue(expectedResults.get(i) == actualResults.get(i));
	}

	@Test
	public void testBefore() {
		initialize();
		expectedResults.add(0, 2);
		expectedResults.add(1, 1);
		expectedResults.add(2, 3);
		expectedResults.add(3, 0);
		System.out.println("Test Before. Precedence");
		testPattern("Before", expectedResults);
	}

	@Test
	public void testAfter() {
		initialize();
		expectedResults.add(0, 0);
		expectedResults.add(1, 3);
		expectedResults.add(2, 1);
		expectedResults.add(3, 2);
		System.out.println("Test After. Precedence");
		testPattern("After", expectedResults);
	}
	
	@Test
	public void testAroundNoProceed() {
		initialize();
		expectedResults.add(0, 2);
		System.out.println("Test Around, no proceed. Precedence");
		testPattern("Around", expectedResults);
	}
	
	@Test
	public void testAroundProccedAfter() {
		initialize();
		expectedResults.add(0, 2);
		expectedResults.add(1, 1);
		expectedResults.add(2, 3);
		expectedResults.add(3, 0);
		System.out.println("Test AroundProceedAfter. Precedence");
		testPattern("AroundProceedAfter", expectedResults);
	}
	
	@Test
	public void testAroundProccedBefore() {
		initialize();
		expectedResults.add(0, 0);
		expectedResults.add(1, 3);
		expectedResults.add(2, 1);
		expectedResults.add(3, 2);
		System.out.println("Test AroundProceedBefore. Precedence");
		testPattern("AroundProceedBefore", expectedResults);
	}
	
	@Test
	public void testAroundProcced2() {
		initialize();
		expectedResults.add(0, 2);
		expectedResults.add(1, 1);
		expectedResults.add(2, 3);
		expectedResults.add(3, 0);
		expectedResults.add(4, 3);
		expectedResults.add(5, 1);
		expectedResults.add(6, 2);
		System.out.println("Test AroundProceed2. Precedence");
		testPattern("AroundProceed2", expectedResults);
	}

}