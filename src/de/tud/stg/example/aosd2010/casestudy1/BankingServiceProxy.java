///////////////////////////////////////////////////////////////////////////////
//Copyright 2008-2015, Technische Universitaet Darmstadt (TUD), Germany
//
//The TUD licenses this file to you under the Apache License, Version 2.0 (the
//"License"); you may not use this file except in compliance
//with the License.  You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing,
//software distributed under the License is distributed on an
//"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//KIND, either express or implied.  See the License for the
//specific language governing permissions and limitations
//under the License.
///////////////////////////////////////////////////////////////////////////////
package de.tud.stg.example.aosd2010.casestudy1;

import java.util.ArrayList;
import java.util.HashMap;

import de.tud.stg.example.aosd2010.casestudy1.BankingServiceProxy;
import de.tud.stg.example.aosd2010.process.domainmodel.Registry;
import de.tud.stg.example.aosd2010.process.domainmodel.ServiceProxy;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.Constants;

/**
 * @author Tom Dinkelaker
 **/
public class BankingServiceProxy extends ServiceProxy {

	public static void init() {
		System.out.println("Registering services");
		Registry.getInstance().clear();
		Registry.getInstance().register(new BankingServiceProxy("BankOnline",6.1f));
		Registry.getInstance().register(new BankingServiceProxy("BankOfTomorrow",4.0f));
		Registry.getInstance().register(new BankingServiceProxy("BankOfTheWeb",5.5f));
	}
	
	protected float interestInPercent;
	
	public BankingServiceProxy(String name, float interestInPercent) {
		super(name,"http://www."+name+".com:3000/banking","Banking");
		this.interestInPercent = interestInPercent;
	}

	public Object call(String operation, ArrayList args) {
		System.out.println(name+" \t call op="+operation+" args="+args);
			
		if ("getRate".equals(operation)) {
			int amount = ((Integer)args.get(0)).intValue();
			int interest = Math.round(amount * (interestInPercent/100)); 
			int result = amount + interest; 
			System.out.println(name+" \t call result="+result);
			return new Integer(result);
		}
		
		if ("book".equals(operation)) {
			int amount = ((Integer)args.get(0)).intValue();
			int interest = Math.round(amount * (interestInPercent/100)); 
			int result = amount + interest; 
			System.out.println(name+" \t book credit with "+name+" at an interest of "+interestInPercent+"%");
			return null;
		}
		
		return null;
	}

	/**
	 * Returns an neutral public key.
	 * @return
	 */
	public int getPublicKey() {
	    return 0; //TODO The methods are needed also in the BankingServiceProxy class since otherwise the Groovy MOP does not recognize them for SecureBankingServiceProxy instance 
	}
		
	/**
	 * Returns an neutral private key.
	 * @return
	 */
	public int getPrivateKey() {
	    return 0; //TODO The methods are needed also in the BankingServiceProxy class since otherwise the Groovy MOP does not recognize them for SecureBankingServiceProxy instance 
	}
		
}
