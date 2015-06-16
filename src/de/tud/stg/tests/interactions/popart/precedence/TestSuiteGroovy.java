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

import junit.framework.TestSuite;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.Booter;

/**
 * @author olga
 */
public class TestSuiteGroovy extends TestSuite {
    private final static boolean BEFORE = true;
    private final static boolean AROUND = false;

	public static TestSuite suite() throws Exception {
        //		Booter.initialize();

		TestSuite suite = new TestSuite();
		// BEFORE- and AFTER-Tests
		if (BEFORE) {
		suite.addTestSuite(TestNoPrecedence.class);
		suite.addTestSuite(TestPrecedence.class); 
		suite.addTestSuite(TestPrecedence2Lists.class);
		suite.addTestSuite(TestPrecedenceError.class);
		//Test disabled since expectedResult=false always leads to fail.
		//suite.addTestSuite(TestPrecedence2ListsError.class);
		}

		// AROUND-Tests
		if (AROUND) {
		suite.addTestSuite(TestAround.class);
		}
		
		return suite;
	}
}
