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
package de.tud.stg.tests.interactions.popart.itd;

import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.tests.interactions.popart.itd.structuredesignators.StructureDesignatorGroovyTests;
import de.tud.stg.tests.interactions.popart.itd.conflicts.*;
import junit.framework.TestSuite;

public class InterTypeDeclarationTestSuite extends TestSuite{
	public static TestSuite suite() throws Exception {
		Booter.initialize();
		TestSuite suite = new TestSuite();
		suite.addTestSuite(InstrumentationMetaClassClosureMethodInvocationTests.class);
		suite.addTestSuite(StructureDesignatorGroovyTests.class);
		suite.addTestSuite(IntroductionTests.class);
		suite.addTestSuite(IntroductionAdviceInteractionTests.class);
		suite.addTestSuite(IntroductionDeployAndScopeTests.class);
		suite.addTestSuite(InterTypeDeclarationConflictTests.class);
		suite.addTestSuite(PropertyConflictResolvementTests.class);
		suite.addTestSuite(SkippingConflictResolvementTests.class);
		return suite;
	}
}
