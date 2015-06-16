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

/**
 * @author olga
 */
public class TestAround extends TestPattern {

//	---------AROUND-TESTS with PRECEDENCE
	public void testAround_Precedence() {
		initialize();
		ArrayList<String> precedenceList = ["Aspect2", "Aspect1", "Aspect3"];
		generateAspects(3,precedenceList);
		expectedResults = [2]
		println "Test Around. Precedence $precedenceList"
		testPattern("Around", expectedResults, testObject);
		unregister();
	}
	
	/**
	 * proceed()-statement is executed before the method on the test object is called
	 */
	public void testAroundProccedBefore_Precedence() {
		initialize();
		ArrayList<String> precedenceList = ["Aspect2", "Aspect1", "Aspect3"];
		generateAspects(3,precedenceList);
		expectedResults = [0, 3, 1, 2]
		println "Test AroundProceedBefore. Precedence $precedenceList"
		testPattern("AroundProceedBefore", expectedResults, testObject);
		unregister();
	}
	
	/**
	 * proceed()-statement is executed after the method on the test object is called
	 */
	public void testAroundProccedAfter_Precedence() {
		initialize();
		ArrayList<String> precedenceList = ["Aspect2", "Aspect1", "Aspect3"];
		generateAspects(3,precedenceList);
		expectedResults = [2, 1, 3, 0]
		println "Test AroundProceedAfter. Precedence $precedenceList"
		testPattern("AroundProceedAfter", expectedResults, testObject);
		unregister();
	}
	
	/**
	 * proceed()-statement is executed before und after the method on the test object is called
	 */	
	public void testAroundProcced2_Precedence() {
		initialize();
		ArrayList<String> precedenceList = ["Aspect2", "Aspect1", "Aspect3"];
		generateAspects(3, precedenceList);
		expectedResults = [2, 1, 3, 0, 3, 1, 2]
		println "Test AroundProceed2. Precedence $precedenceList"
		testPattern("AroundProceed2", expectedResults, testObject);
		unregister();
	}
	//----------AROUND-TESTS with NO PRECEDENCE	
	public void testAround() {
		initialize();
		generateAspects(3, null);
		expectedResults = [1]
		println "Test Around. No precedence"
		testPattern("Around", expectedResults, testObject);
		unregister();
	}
	
	public void testAroundProccedAfter() {
		initialize();
		generateAspects(3,"precedence",null);
		expectedResults = [1, 2, 3, 0]
		println "Test AroundProceedAfter. No precedence"
		testPattern("AroundProceedAfter", expectedResults, testObject);
		unregister();
	}
	
	public void testAroundProccedBefore() {
		initialize();
		generateAspects(3,null);
		expectedResults = [0, 1, 2, 3]
		println "Test AroundProceedBefore. No precedence"
		testPattern("AroundProceedBefore", expectedResults, testObject);
		unregister();
	}
	
	
	
}
