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

import java.util.ArrayList;

public class AnswerMachine extends Phone { // AnswerMachine.java
	
	static ArrayList<Phone> lst = new ArrayList<Phone>();
	

	
	
	public AnswerMachine(String name) {
		this.name = name;
		forwardPhone = null;
		answerMachine = null;
		this.answered = false;
		this.present = true;
	}

	public boolean isOn() {
		lst.add(new Phone());
		return present;
	}

	public void turnOn() {
		present = true;
	}

	public void turnOff() {
		present = false;
	}

	/*
	 * If the answermachine is on it will recored a message
	 */
	public boolean receiveCall(String number) {
		if (present) {
			pickUp();
		} else {
			System.out.println("Call was not answered by " + name);
			}
		return this.answered;

	}

	private void pickUp() {
		System.out.println(this.name + " recorded a Message");
		this.answered = true;

	}
}