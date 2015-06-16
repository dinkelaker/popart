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
package de.tud.stg.example.aosd2010.casestudyMOP

import de.tud.stg.example.aosd2010.process.domainmodel.Registry
import de.tud.stg.example.aosd2010.process.domainmodel.ServiceProxy

public class BankingServiceProxy extends ServiceProxy {
	
	private static boolean DEBUG = false
	
	private double interestInPercent
	
	public static void initThree() {
		Registry.instance.register(new BankingServiceProxy('BankOnline', 6.1))
		Registry.instance.register(new BankingServiceProxy('BankOfTomorrow', 4.0))
		Registry.instance.register(new BankingServiceProxy('BankOfTheWeb', 5.5))
	}
	
	public static void init(int nBanks) {
		int a = 'a'.charAt(0)
		nBanks.times {
			int nameSize = 3 + (13 * Math.random())
			byte[] nameChars = new byte[nameSize]
			nameSize.times { n ->
				nameChars[n] = (a + (26 * Math.random()))
			}
			String name = new String(nameChars)
			Registry.instance.register(new BankingServiceProxy("Bank$name", 4 + (3 * Math.random())))
			if (DEBUG) println("Created Bank: '$name'")
		}
	}
	
	public BankingServiceProxy(String name, double interestInPercent) {
		super(name, "http://www.${name}.com:3000/banking".toString(), 'Banking')
		this.interestInPercent = interestInPercent
	}
	
	@Override
	public Object call(String operation, ArrayList args) {
		if (DEBUG) println("$name \t call op=$operation args=$args")
		
		if ('getRate' == operation) {
			int amount = args[0].toInteger()
			int interest = Math.round(amount * (interestInPercent / 100))
			int result = amount + interest
			if (DEBUG) println("$name \t call result=$result")
			return result
		}
		
		if ('book' == operation) {
			int amount = args[0].toInteger()
			int interest = Math.round(amount * (interestInPercent / 100))
			int result = amount + interest
			//This whole method could be ignored (and deleted/ignored by the jit), if "debug" is "false" and the line below wouldn't exist.
			if (DEBUG) println("$name \t book credit with $name at an interest of ${interestInPercent}%; result: $result")
			return null
		}
		
		return null
	}

}
