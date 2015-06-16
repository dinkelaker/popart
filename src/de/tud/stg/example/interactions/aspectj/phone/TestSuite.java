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
package de.tud.stg.example.interactions.aspectj.phone;

import junit.framework.TestCase;

import org.junit.Test;


public class TestSuite extends TestCase {
	private static Phone alice;
	private static Phone bob;
	
	private boolean alicePickedUp = false;
	private boolean bobPickedUp = false;
	private boolean aliceHasMessage = false;
	private boolean bobHasMessage = false;
	private String separationLine = "--------";  
	  
	  		
	

	private void initialize() {
		alice = new Phone("Alice");
		bob = new Phone("Bob");
		alice.setForwardPhone(bob);
		PhoneSwitch.activateRecordingForPhone(alice);
		//alice.setForwardPhone(null);
		//bob.setForwardPhone(null);
			
	}
	
	private void setConditions(boolean AisHere, boolean AamIsOn, boolean BisHere, boolean BamIsOn) {
		alice.setPresent(AisHere);
		bob.setPresent(BisHere);
		if (AamIsOn)
			alice.getAnswerMachine().turnOn();
		else
			alice.getAnswerMachine().turnOff();
		
		if (BamIsOn)
			bob.getAnswerMachine().turnOn();
		else
			bob.getAnswerMachine().turnOff();
	}
	
	private boolean makeCall() {
		boolean result = alice.receiveCall("06151-165306");
		alicePickedUp = alice.isAnswered();
		bobPickedUp = bob.isAnswered();
		aliceHasMessage = alice.getAnswerMachine().isAnswered();
		bobHasMessage = bob.getAnswerMachine().isAnswered();
		return result;
	}

	@Test
	public void testAliceIsHereAMisOn() {
		
		initialize();
		setConditions(true, true, false, true);
		makeCall();
		System.out.println(separationLine);
		assertEquals(alicePickedUp, true);
		assertEquals(bobPickedUp, false);
		assertEquals(aliceHasMessage, false);
		assertEquals(bobHasMessage, false);
		
	}
	
	@Test
	public void testAliceIsHereAMisOff() {
		
		initialize();
		setConditions(true, false, false, true);
		makeCall();
		System.out.println(separationLine);
		assertEquals(alicePickedUp, true);
		assertEquals(bobPickedUp, false);
		assertEquals(aliceHasMessage, false);
		assertEquals(bobHasMessage, false);
		
	}
	
	@Test
	public void testBobIsHere() {
		initialize();
		setConditions(false, true, true, true);
		makeCall();
		System.out.println(separationLine);
		assertEquals(alicePickedUp, false);
		assertEquals(bobPickedUp, true);
		assertEquals(aliceHasMessage, false);
		assertEquals(bobHasMessage, false);
		}
	
	@Test
	public void testNobodyIsHereAliceAMisOn() {
		initialize();
		setConditions(false, true, false, true);
		makeCall();
		System.out.println(separationLine);
		assertEquals(alicePickedUp, false);
		assertEquals(bobPickedUp, false);
		assertEquals(aliceHasMessage, true);
		assertEquals(bobHasMessage, false);
		
	}

	@Test
	public void testNobodyIsHereBobAMisOn() {
		initialize();
		setConditions(false, false, false, true);
		makeCall();
		System.out.println(separationLine);
		assertEquals(alicePickedUp, false);
		assertEquals(bobPickedUp, false);
		assertEquals(aliceHasMessage, false);
		assertEquals(bobHasMessage, false);
		
	}

	
	@Test
	public void testNobodyIsHereAMisOff() {
		initialize();
		setConditions(false, false, false, false);
		makeCall();
		System.out.println(separationLine);
		assertEquals(alicePickedUp, false);
		assertEquals(bobPickedUp, false);
		assertEquals(aliceHasMessage, false);
		assertEquals(bobHasMessage, false);
		
	}

}