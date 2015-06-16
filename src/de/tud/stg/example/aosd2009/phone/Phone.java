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

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Phone { //Phone.java
  InputStreamReader isr = new InputStreamReader( System.in );
  BufferedReader stdin = new BufferedReader( isr );

  String name;
  Phone forwardPhone;
  AnswerMachine answerMachine;

  public Phone() {
  }
	  
  public Phone(String name) {
	this.name = name;
	//forwardPhone = new Phone();
	//forwardPhone.name = "Secretary of "+name;
	answerMachine = new AnswerMachine("AnswerMachine"+name.toString());
  }
  
  boolean receiveCall(String number) {
	System.out.print("Phone ringing for "+name+" from "+number+" answer [Y,n]: ");
	String input=null;
	try {
	  input = stdin.readLine();
    } catch (Exception ex) {
            
    }
    boolean answered = !input.equals("n");
	System.out.println("Phone was "+(answered? "" : "not ")+"answered.");
	return answered;
  }
  
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
	return "Phone("+name+")";  
  }
}

