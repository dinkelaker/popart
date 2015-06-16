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
package de.tud.stg.tests;

import junit.framework.TestSuite;

import org.junit.runners.Suite;

import de.tud.stg.tests.dslsupport.TestDSLSupport;
import de.tud.stg.tests.dslsupport.TestInterpreter;
import de.tud.stg.tests.dslsupport.TestSyntaxAnalysis;
import de.tud.stg.tests.instrumentation.TestVictim;
import de.tud.stg.tests.instrumentation.core.TestCatchAll;
import de.tud.stg.tests.instrumentation.core.TestCatchGlobal;
import de.tud.stg.tests.instrumentation.core.TestChangingParametersAndReturnValues;
import de.tud.stg.tests.instrumentation.core.TestClosureDelegation;
import de.tud.stg.tests.instrumentation.core.TestDefaultParameter;
import de.tud.stg.tests.instrumentation.core.TestEnableGlobally;
import de.tud.stg.tests.instrumentation.core.TestInheritance;
import de.tud.stg.tests.instrumentation.core.TestInstanceMethods;
import de.tud.stg.tests.instrumentation.core.TestInstanceMisc;
import de.tud.stg.tests.instrumentation.core.TestMiscInstrumentation;
import de.tud.stg.tests.instrumentation.core.TestParameter;
import de.tud.stg.tests.instrumentation.core.TestParameterConversion;
import de.tud.stg.tests.instrumentation.core.TestPerInstance;
import de.tud.stg.tests.instrumentation.core.TestProxyVsExpandoMetaClass;
import de.tud.stg.tests.instrumentation.core.TestRecursionAndDefaultParameter;
import de.tud.stg.tests.instrumentation.core.TestReturnValues;
import de.tud.stg.tests.instrumentation.core.TestStackUtils;
import de.tud.stg.tests.instrumentation.core.TestStaticMethods;
import de.tud.stg.tests.instrumentation.core.TestStaticMisc;
import de.tud.stg.tests.instrumentation.core.TestWhyProxyMetaClass;

/**
 * @author Oliver Rehor
 **/
public class RegressionTests extends TestSuite {
	
	public static TestSuite suite() throws Exception {
	    
	    TestSuite suite = new TestSuite();

	    //DSL TESTS
	    suite.addTestSuite(TestInterpreter.class);
	    suite.addTestSuite(TestSyntaxAnalysis.class);
	    suite.addTestSuite(de.tud.stg.tests.dslsupport.process.TestProcessDSL.class);
	   
	   
	    //BASE TESTS
	    //suite.addTestSuite(TestVictim.class);
		//suite.addTestSuite(TestProxyVsExpandoMetaClass.class);
		//suite.addTestSuite(TestMiscInstrumentation.class);
		suite.addTestSuite(TestInstanceMisc.class);
		suite.addTestSuite(TestInstanceMethods.class);
		suite.addTestSuite(TestStaticMethods.class);
		suite.addTestSuite(TestStaticMisc.class);
		//suite.addTestSuite(TestDefaultParameter.class);
		suite.addTestSuite(TestStackUtils.class);
		suite.addTestSuite(TestReturnValues.class);
		suite.addTestSuite(TestParameter.class);
		suite.addTestSuite(TestChangingParametersAndReturnValues.class);
		suite.addTestSuite(TestCatchAll.class);
		//suite.addTestSuite(TestCatchGlobal.class);
		//suite.addTestSuite(TestInheritance.class);
		//suite.addTestSuite(TestParameterConversion.class);
		//suite.addTestSuite(TestClosureDelegation.class);
		//suite.addTestSuite(TestWhyProxyMetaClass.class);
		
		
	    return suite;
	}
	  
}
