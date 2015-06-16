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

import junit.framework.TestCase
import java.lang.reflect.Method;
import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.AspectFactory;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.dslsupport.DSLException; 

public class TestPrecedence2Lists extends TestPattern {
	
	public void testBefore_Precedence_2Lists() {
		initialize();
		ArrayList<String> precedenceList1 = ["Aspect2", "Aspect1"];
		ArrayList<String> precedenceList2 = ["Aspect1", "Aspect3"];
		generateAspects(3,precedenceList1, precedenceList2);
		System.out.println(separationLine);
		expectedResults = [2, 1, 3, 0]
		println "Test Before_2Lists. Precedence $precedenceList1 and $precedenceList2"
		testPattern("Before", expectedResults, testObject);
		unregister();
		
	}
	
	public void testAfter_Precedence_2Lists() {
		initialize();
		ArrayList<String> precedenceList1 = ["Aspect2", "Aspect1"];
		ArrayList<String> precedenceList2 = ["Aspect1", "Aspect3"];
		generateAspects(3,precedenceList1, precedenceList2);
		expectedResults = [0, 3, 1, 2]
		println "Test After_2Lists. Precedence $precedenceList1 and $precedenceList2"
		testPattern("After", expectedResults, testObject);
		unregister();
	}
	
	
	//	------ Tests with 2 correct precedence lists resulting into incorrect precedence map (no shared JP)
	/**
	 * This test is expected to pass because the aspects that have a circle precedence (a1>a2>a1) in the precedence list 
	 * have no shared jont point  
	 */	
	public void testBefore_FalsePrecedence_2Lists_noSharedJP() {
		initialize();
		ArrayList<String> precedenceList1 = ["Aspect3", "Aspect1"];
		ArrayList<String> precedenceList2 = ["Aspect1", "Aspect3"];
		ArrayList<String> precedenceList3 = ["Aspect2", "Aspect1"];
		generateAspects(2,precedenceList1, precedenceList2, precedenceList3);
		System.out.println(separationLine);
		println "Test Before, incorrect precedence but no shared join point. Precedence $precedenceList1 and $precedenceList2 and $precedenceList3"
		expectedResults = [2, 1, 0];
		testPattern("Before", expectedResults, testObject);
		unregister();

		
	}

	public void testAfter_FalsePrecedence_2Lists_noSharedJP() {
		initialize();
		ArrayList<String> precedenceList1 = ["Aspect3", "Aspect1"];
		ArrayList<String> precedenceList2 = ["Aspect1", "Aspect3"];
		ArrayList<String> precedenceList3 = ["Aspect2", "Aspect1"];
		generateAspects(2,precedenceList1, precedenceList2, precedenceList3);
		System.out.println(separationLine);
		println "Test After, incorrect precedence but no shared join point. Precedence $precedenceList1 and $precedenceList2 and $precedenceList3"
		expectedResults = [0, 1, 2];
		testPattern("After", expectedResults, testObject);
		unregister();
	}

	
	
}




