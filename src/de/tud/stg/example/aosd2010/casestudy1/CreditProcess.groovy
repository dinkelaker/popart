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
package de.tud.stg.example.aosd2010.casestudy1;

import de.tud.stg.example.aosd2010.process.ProcessDSL;
//import de.tud.stg.example.application.MyServiceProxy;

BankingServiceProxy.init();

def p = 

new ProcessDSL().eval(name:"CreditProcess") {

	def banks;
	def offers = new TreeMap();
	
	sequence {

		task (name:"getRates") {
		    log "PROCESS: \t Get banking services from registry."
		    banks = registry.find("Banking");
		    assert banks.size() > 0;
		    log "--> PROCESS: \t Get rates from banks $banks."
		    banks.each { bank -> 
		        log "PROCESS: \t\t Calling service at "+bank.endPoint
		        def costs = bank.call( "getRate", [10000] ) 
		        //log "PROCESS: \t\t result=$result from service at "+bank.endPoint
		        offers[costs] = bank;
		        log "PROCESS: \t\t Received credit offer from $bank.name costs $costs"
		    }
	    }

		task (name:"selectOffer") {
			log "PROCESS: \t select the best credit offer $offers."		    
		    def cheapest = offers.firstKey();
		    def selectedBank = offers[cheapest];
		    log "PROCESS: \t selectedBank.name=$selectedBank.name"
		    selectedBank.call ( "book", [10000]);
		    notify "Booked credit with $selectedBank.name costs $cheapest"
	    }
	}
	
}

//for (i in 1..100) {
p.execute()
//}

