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

/**
 * This aspect forwards calls to colleagues.
 * 
 */
public aspect Phone_To_forwardPhone {
	

	//declare precedence: Phone_To_forwardPhone, Phone_To_AnswerMachine;

	/**
	 * Der Pointcut
	 */
	pointcut calling(Phone p, String number):
	execution (* receiveCall(*))&&
	target(p)&&
	args(number);

	before(Phone p, String number): calling(p, number) {
		System.out.println("--> Entering receiveCall from Phone_2Phone");

	}

	boolean around(Phone p, String number): calling(p, number) {
		boolean answered = false;
		//System.out.println("--> Proceeding from Phone_2Phone");
		answered = proceed(p, number);
		Phone forwardPhone = p.getForwardPhone();
		// System.out.println("Condition on Phone_2Phone: answered " +answered
		// );
		if ((!answered) && (forwardPhone != null)) {
			answered = forwardPhone.receiveCall(number);
		}

		return answered;

	}
}
