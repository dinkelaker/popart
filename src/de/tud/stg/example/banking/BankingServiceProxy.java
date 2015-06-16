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
package de.tud.stg.example.banking;

import java.util.ArrayList;

import de.tud.stg.example.interpreter.metamodel.ServiceProxy;
import de.tud.stg.example.interpreter.metamodel.Registry;
public class BankingServiceProxy extends ServiceProxy {
	
	private static boolean DEBUG = false;
	
	private double interestInPercent;
	
	public static void initThree() {
		Registry.getInstance().register(new BankingServiceProxy("BankOnline", 6.1));
		Registry.getInstance().register(new BankingServiceProxy("BankOfTomorrow", 4.0));
		Registry.getInstance().register(new BankingServiceProxy("BankOfTheWeb", 5.5));
	}
	
	public static void init(int nBanks) {
		int a = 'a';
		for(int i = 0; i < nBanks; i++){
			int nameSize = 3 + (int)(13 * Math.random());
			char[] nameChars = new char[nameSize];
			for(int n = 0; n < nameSize; n++){
				nameChars[n] = (char)(a + (int)(26 * Math.random()));
			}
			String name = new String(nameChars);
			Registry.getInstance().register(new BankingServiceProxy("Bank"+name, 4 + (3 * Math.random())));
			if (DEBUG) System.out.println("Created Bank: '"+name+"'");
		}
	}
	
	public BankingServiceProxy(String name, double interestInPercent) {
		super(name, "http://www."+name+".com:3000/banking", "Banking");
		this.interestInPercent = interestInPercent;
	}
	
	@Override
	public Object call(String operation, ArrayList<Object> args) {
		if (DEBUG) System.out.println("$name \t call op="+operation+" args="+args);
		
		if ("getRate".equals(operation)) {
			int amount = (Integer)args.get(0);
			int interest = (int)Math.round(amount * (interestInPercent / 100));
			int result = amount + interest;
			if (DEBUG) System.out.println(name+" \t call result="+result);
			return result;
		}
		
		if ("book".equals(operation)) {
			int amount = (Integer)args.get(0);
			int interest = (int)Math.round(amount * (interestInPercent / 100));
			int result = amount + interest;
			//This whole method could be ignored (and deleted/ignored by the jit), if "debug" is "false" and the line below wouldn't exist.
			if (DEBUG) System.out.print(name+" \t book credit with $name at an interest of "+interest+"%; result: "+result);
			return null;
		}
		
		return null;
	}

}
