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
package de.tud.stg.example.aosd2010.casestudy3;

import java.util.ArrayList;
import java.util.Random;

import de.tud.stg.example.aosd2010.casestudy1.BankingServiceProxy;
import de.tud.stg.example.aosd2010.process.domainmodel.Registry;

public class SecureBankingServiceProxy extends BankingServiceProxy implements Identity {
	
	int secret;
	
	public int getPrivateKey() {
	  return secret;	
	}
	
	public int getPublicKey() {
	  return secret;	
	}
	
	private SecureProcess sender;
	
	public static void initSecure(SecureProcess sender) {
		System.out.println("Registering secured services");
		Registry.getInstance().clear();
		Registry.getInstance().register(new SecureBankingServiceProxy(sender,"SecureBankOnline",6.1f));
		Registry.getInstance().register(new SecureBankingServiceProxy(sender,"SecureBankOfTomorrow",4.0f));
		Registry.getInstance().register(new SecureBankingServiceProxy(sender,"SecureBankOfTheWeb",5.5f));
	}
	
	public SecureBankingServiceProxy(SecureProcess sender, String name, float interestInPercent) {
		super(name,interestInPercent);
		this.sender = sender;
		secret = new Random().nextInt(9)+1;
	}
	
	public Object call(String operation, ArrayList args) {
		System.out.println(name+" \t call op="+operation+" args="+args);
		
		//Secured
		if ("getRate".equals(operation)) {
			System.out.println(name+" \t receives secured call op="+operation+" args="+args);
			int enc_amount = ((Integer)args.get(0)).intValue();
			int amount = enc_amount - this.getPrivateKey();
			System.out.println(name+" \t receives encrypted message enc_amount="+enc_amount+" with key="+this.getPrivateKey()+" was decrypted amount="+amount);
			int interest = Math.round(amount * (interestInPercent/100)); 
			int result = amount + interest;
			int enc_result = result + sender.getPublicKey();
			System.out.println(name+" \t response with encrypted message result="+result+" with key="+sender.getPublicKey()+" was decrypted enc_result="+enc_result);
			//System.out.println(name+" \t call result="+result);
			return new Integer(enc_result);
		}
		
		//Not secure operation
		if ("book".equals(operation)) {
			System.out.println(name+" \t receives not secured call op="+operation+" args="+args);
			int amount = ((Integer)args.get(0)).intValue();
			int interest = Math.round(amount * (interestInPercent/100)); 
			int result = amount + interest; 
			System.out.println(name+" \t book credit with "+name+" at an interest of "+interestInPercent+"%");
			return null;
		}	
		return null;
	}	
}
