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

import java.util.ArrayList;

/**
 * @author Olga Gusyeva
 **/
public class TestObject {
	final int id = 0;
	ArrayList<Integer> results = new ArrayList<Integer>();
	
	public TestObject() {
		results = new ArrayList<Integer>();
	}
		
	void trace() {
		results.add(id);
	}
	
	public void testMethodBefore() {
		//System.out.println("Native method Before");
		trace();
	}
	
	public void testMethodAfter() {
		//System.out.println("Native method After");
		trace();
	}
	
	public void testMethodAround() {
		//System.out.println("Native method Around");
		trace();
	}
	
	public void testMethodAroundProceedAfter() {
		trace();
	}
	
	public void testMethodAroundProceedBefore() {
		trace();
	}
	
	public void testMethodAroundProceed2() {
		trace();
	}
	
	
	public ArrayList<Integer> getResults() {
		return results;
	}
	
	
}
