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
import java.util.HashMap;

public class LBHBankWS extends Bank{
	
	public LBHBankWS(){
		Client bankClient11 = new Client("Bettinastrasse,Francfurt",7000, "CASSICI", "Luca", "015775398730");
		 Client bankClient12 = new Client("Landstrasse,Darmstadt",2000,  "NAJAM", "Sara", "015775398730");
		 ArrayList<Client> clientSet1 = new ArrayList<Client>();
		 clientSet1.add(bankClient11);
		 clientSet1.add(bankClient12);
		 this.bankName = "Landesbank Berlin Holding";
		 this.clientSet = clientSet1;
		 this.interestRate =0.2;
	}
	
	public int getRate(int amount){
		int interest = (int)Math.round(amount * (interestRate/100)); 
		int result = amount + interest; 
		return new Integer(result);
	}
	
	public int book(int amount){
		int interest = (int)Math.round(amount * (interestRate/100)); 
		int result = amount + interest;
		return new Integer(interest);
		//System.out.println(bankName+" \t book credit with "+result+" at an interest of "+interestRate+"%");
	}
	
}
