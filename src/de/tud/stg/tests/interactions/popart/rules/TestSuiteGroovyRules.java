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

/**
 * @author Olga Gusyeva
 */
import junit.framework.TestSuite;

public class TestSuiteGroovyRules extends TestSuite {
	
	private final static boolean CONFLICT = true;
	private final static boolean RULES_APP = true;
	private final static boolean FUZZY = true;

	public static TestSuite suite() throws Exception {
		TestSuite suite = new TestSuite();

		if (CONFLICT) {
			suite.addTestSuite(TestConflictDuplicateEntries.class);
			suite.addTestSuite(TestConflictIndirectDiffRules.class);
			suite.addTestSuite(TestConflictDirectDiffRules.class);
			suite.addTestSuite(TestCycle.class);
		}
		if (RULES_APP) {
			suite.addTestSuite(TestRulesApplication.class);
		}
		if (FUZZY) {
			suite.addTestSuite(TestFuzzy.class);
		}
		return suite;
	}
}
