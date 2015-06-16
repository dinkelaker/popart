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

public aspect tracing {
	private String AMstatus = "off";
	private String phoneOwnerStatus = "here";

	pointcut createPhone(String s):
	(call (Phone.new(String)) ||
	call (AnswerMachine.new(String)))&&
	args(s);
	
	pointcut changeOwnerPresence(Phone p) :
		call (* setPresent(*)) &&
		target(p);



	pointcut changeStatus(AnswerMachine am):
		  call(* turn*(..))&&
		  target(am);

/*	pointcut makeCall():
		  call(* receiveCall(..));
	
	before(): makeCall () {
		System.out.println("Calling");
	}*/

//	before(String s): createPhone (s) {
//		// System.out.println(thisJoinPoint.getSignature());
//		System.out.println("New " + s);
//	}

/*	before(AnswerMachine am): changeStatus (am) {
		System.out.println(am.getName() + " is about to change status");
	}*/

	after(AnswerMachine am): changeStatus (am) {

		if (am.isOn())
			AMstatus = "on";
		else
			AMstatus = "off";
		System.out.println(am.getName() + " is turned " + AMstatus);
	}
	
/*	before(Phone p): changeOwnerPresence (p)  {
		System.out.println(p.getName() + " is about to change owner status");
	}*/
	
	after(Phone p): changeOwnerPresence (p) {

		if (p.present)
			phoneOwnerStatus = "here";
		else
			phoneOwnerStatus = "not here";
		System.out.println(p.getName() + " is " + phoneOwnerStatus);
	}
}
