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
public class TestNoPrecedence extends TestPattern{
	
	
	public void testBefore() {
		initialize();
		generateAspects(3,null);
		expectedResults = [1, 2, 3, 0]
		println "Test Before. No precedence"
		testPattern("Before", expectedResults, testObject);
		unregister();
		
	}
	
	public void testAfter() {
		
		initialize();
		generateAspects(3,null);
		expectedResults = [0, 3, 2, 1]
		println "Test After. No precedence"
		testPattern("After", expectedResults, testObject);
		unregister();
	}
	
	
}
