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

public class AnswerMachine extends Phone { //AnswerMachine.java
  public AnswerMachine(String name) {
	this.name = name;
	forwardPhone = null;
	answerMachine = null;
  }
  
  boolean on = false;
  public boolean isOn() { return on; } 
  public void turnOn() { on = true; } 
  public void turnOff() { on = false; } 
  
  public boolean receiveCall(String number) {
	if (on) System.err.println(name+" received the call from "+number);
	return on;
  }
}