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
package de.tud.stg.tests.dslsupport.process;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestProcessDSL extends TestCase {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExampleProcessesExecuteWithoutErrors() {
		System.out.println("==========================================");
		System.out.println("Case Study 1: executing CreditProcess");
		de.tud.stg.example.aosd2010.casestudy1.DynamicCreditProcess.main(new String[] {});
		System.out.println("==========================================");

		System.out.println("==========================================");
		System.out.println("Case Study 2: executing SafeCreditProcess");
		de.tud.stg.example.aosd2010.casestudy2.SafeCreditProcess.main(new String[] {});
		System.out.println("==========================================");

		System.out.println("==========================================");
		System.out.println("Case Study 3: executing CreditProcess");
		de.tud.stg.example.aosd2010.casestudy3.SecuredCreditProcess.main(new String[] {});
		System.out.println("==========================================");
	}
	
	@Test
	public void testExampleAspectOrientedProcessesExecuteWithoutErrors() {
		System.out.println("Case Study MOP: executing unwoven CreditProcess");
		de.tud.stg.example.aosd2010.casestudyMOP.CreditProcess.main(new String[] {});

		System.out.println("Case Study MOP: executing woven CreditProcess");
		de.tud.stg.example.aosd2010.casestudyMOP.CreditProcessWithLogAspects.main(new String[] {});
}
	
}
