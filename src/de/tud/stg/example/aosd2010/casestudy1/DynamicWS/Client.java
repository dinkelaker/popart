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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Sara
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Client", propOrder = {
    "address",
    "amount",
    "clientFirstName",
    "clientLastName",
    "phone"
})
public class Client{

	/**
	 * 
	 */
	private String address; 
	private int amount;
	private String clientFirstName;
	private String clientLastName;
	private String phone;


	public Client() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Client(String address, int amount,String clientFirstName, String clientLastName, String phone) {
		super();
		this.address = address;
		this.amount = amount;
		this.clientFirstName = clientFirstName;
		this.clientLastName = clientLastName;
		this.phone = phone;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}


	public String getClientFirstName() {
		return clientFirstName;
	}


	public void setClientFirstName(String clientFirstName) {
		this.clientFirstName = clientFirstName;
	}


	public String getClientLastName() {
		return clientLastName;
	}


	public void setClientLastName(String clientLastName) {
		this.clientLastName = clientLastName;
	}

	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	
}
