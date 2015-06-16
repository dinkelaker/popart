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
package de.tud.stg.example.aosd2009.phone;

public class Main {
	
    public static Phone original; 
	public static Phone secretary;
	public static Phone center; 
	
	public static void main(String[] args) {
	    original = new Phone("Alice");
		secretary = new Phone("Bob");
		center = new Phone("Central");

		//set forwarding
		original.forwardPhone = secretary;
		original.answerMachine.turnOn();
		secretary.forwardPhone = center;
		
		if (args != null) return;
		System.out.println("\nMain.start");
		original.receiveCall("069-66368142");
		System.out.println("Main.end\n");
	}
	
	public static void testAnswerMachineOff() {
		  System.out.println("testAnswerMachineOff");
		  original.answerMachine.turnOff();
		  original.receiveCall("069-60625716");
	}

	public static void testAnswerMachineOn() {
		  System.out.println("testAnswerMachineOn");
		  original.answerMachine.turnOn();
		  original.receiveCall("069-123445");
	}

	public static void testSecretaryAnswerMachineOff() {
		  System.out.println("testSecretaryAnswerMachineOff");
		  secretary.answerMachine.turnOff();
		  original.receiveCall("069-60625716");
	}

	public static void testSecretaryAnswerMachineOn() {
		  System.out.println("testSecretaryAnswerMachineOn");
		  secretary.answerMachine.turnOn();
		  original.receiveCall("069-123445");
	}

}
