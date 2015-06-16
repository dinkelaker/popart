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

/**
 * This aspect records a message on an answer machine.
 * 
 */
public aspect Phone_To_AnswerMachine {
		
	pointcut recording(Phone p, String number):
		execution (* receiveCall(*))&&
		target(p)&&
		args(number) && 
		if (PhoneSwitch.isRecordingActiveForPhone(p));

	before(Phone p, String number): recording (p, number) {
		System.out.println("--> Entering receiveCall from Phone_2AM");
	}

	boolean around(Phone p, String number): recording(p, number) {
		//System.out.println("--> Proceeding from Phone_2AM");
		boolean answered = proceed(p, number);
		AnswerMachine am = p.getAnswerMachine();
		// System.out.println("Condition on Phone_2AM: answered " +answered );

		if ((!answered) && (am != null))
			answered = am.receiveCall(number);
		return answered;

	}

}
