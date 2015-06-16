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
package de.tud.stg.tests.instrumentation.core;

import junit.framework.TestSuite;
import de.tud.stg.tests.instrumentation.TestVictim;

/**
 * @author jansto
 */
public class AllTests extends TestSuite {
	
	public static TestSuite suite() throws Exception {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TestVictim.class);
		suite.addTestSuite(TestProxyVsExpandoMetaClass.class);
		suite.addTestSuite(TestMiscInstrumentation.class);
		suite.addTestSuite(TestInstanceMisc.class);
		suite.addTestSuite(TestInstanceMethods.class);
		suite.addTestSuite(TestStaticMethods.class);
		suite.addTestSuite(TestStaticMisc.class);
		suite.addTestSuite(TestDefaultParameter.class);
		suite.addTestSuite(TestStackUtils.class);
		suite.addTestSuite(TestReturnValues.class);
		suite.addTestSuite(TestParameter.class);
		suite.addTestSuite(TestChangingParametersAndReturnValues.class);
		suite.addTestSuite(TestCatchAll.class);
		suite.addTestSuite(TestCatchGlobal.class);
		suite.addTestSuite(TestInheritance.class);
		suite.addTestSuite(TestParameterConversion.class);
		suite.addTestSuite(TestClosureDelegation.class);
		suite.addTestSuite(TestWhyProxyMetaClass.class);
		suite.addTestSuite(TestPerInstance.class);
		suite.addTestSuite(TestEnableGlobally.class);
		suite.addTestSuite(TestRecursionAndDefaultParameter.class);
		return suite;
	}
}
