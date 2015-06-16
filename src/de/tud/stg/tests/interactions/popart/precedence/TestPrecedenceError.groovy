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

public class TestPrecedenceError extends TestPattern {
	public void testBefore_FalsePrecedence() {
		try {
			initialize();
			ArrayList<String> precedenceList = ["Aspect2", "Aspect1", "Aspect2"];
			generateAspects(3,precedenceList);
			System.out.println(separationLine);
			println "Test Before, incorrecte precedence. Precedence $precedenceList"
			testPattern("Before", expectedResults, testObject);
			//fail("The test expected the testPattern method to throw an Exception.")
		}
		catch (Exception e) {
			assertTrue(e.message.contains("duplicate entries"));
		}
		unregister();
	}
	
	
}




