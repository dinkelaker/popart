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

public class CommerzbankBankWS extends Bank{
	
	public CommerzbankBankWS(){
		 Client bankClient31 = new Client("Landstrasse,Darmstadt",9800, "JENY", "Katalena",  "015774968730");
		 Client bankClient32 = new Client("Rhönring, Darmstadt",1500,  "MATIEU", "Marya", "015775300730");
		 ArrayList<Client> clientSet3 = new ArrayList<Client>();
		 clientSet3.add(bankClient31);
		 clientSet3.add(bankClient32);
		 this.bankName = "Commerzbank";
		 this.clientSet = clientSet3;
		 this.interestRate =0.5;
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
	//	System.out.println(bankName+" \t book credit with "+bankName+" at an interest of "+interestRate+"%");
	}
	
}
