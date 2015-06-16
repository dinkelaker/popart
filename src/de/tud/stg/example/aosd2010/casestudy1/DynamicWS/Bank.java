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
package de.tud.stg.example.aosd2010.casestudy1.DynamicWS;

import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Bank {

	protected String bankName = "";
	protected ArrayList<Client> clientSet = new ArrayList<Client>();
	protected double interestRate = 0;
	
	
	
	public Bank() {
		super();
	}

	public String getBankName() {
		return bankName;
	}

	public ArrayList<Client> getClientSet() {
		return clientSet;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public void setClientSet(ArrayList<Client> clientSet) {
		this.clientSet = clientSet;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public Bank(String bankName,ArrayList<Client> clientSet, double interestRate) {
		super();
		this.bankName = bankName;
		this.clientSet = clientSet;
		this.interestRate = interestRate;
	}





}