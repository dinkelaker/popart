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

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Phone { // Phone.java
	InputStreamReader isr = new InputStreamReader(System.in);
	BufferedReader stdin = new BufferedReader(isr);

	String name;
	Phone forwardPhone;
	AnswerMachine answerMachine;
	boolean present = true;
	boolean busy = false;
	boolean answered = false;

	public Phone() {
	}

	public Phone(String name) {
		this.name = name;
		answerMachine = new AnswerMachine("AnswerMachine" + name);
	}

	/*
	 * Alice answer the phone if she is here and not busy. Otherwise the call
	 * will be forwarded to Bob.
	 */
	public boolean receiveCall(String number) {
		System.out.println("Calling " + name);
		if (present && !busy) {
			pickUp();

		} 
//			else if (forwardPhone != null) {
//			forwardPhone.receiveCall(number);
//			System.out.println("Call was forwarded");
//
//		} 
			else {
			System.out.println("Call was not answered");
		}
		return this.answered;

	}

	private void pickUp() {
		System.out.println(this.name + " answered the phone");
		this.answered = true;

	}
	
	// GETTERS AND SETTERS

	public String getName() {
		return name;
	}

	public Phone getForwardPhone() {
		return forwardPhone;
	}

	public void setForwardPhone(Phone forwardPhone) {
		this.forwardPhone = forwardPhone;
	}

	public AnswerMachine getAnswerMachine() {
		return answerMachine;
	}

	public String toString() {
		return "Phone(" + name + ")";
	}

	public boolean isAnswered() {
		return answered;
	}

	public void setAnswered(boolean answered) {
		this.answered = answered;
	}

	public boolean isPresent() {
		return present;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

}
