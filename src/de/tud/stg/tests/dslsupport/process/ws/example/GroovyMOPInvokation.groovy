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
package de.tud.stg.tests.dslsupport.process.ws.example;

import static org.junit.Assert.assertEquals

import org.junit.Before
import org.junit.Test

import de.tud.stg.example.aosd2010.casestudy1.DynamicWebServiceClient.DynamicWSClient


class GroovyMOPInvokation {

	@Before
	public void setUp() throws Exception {
	}

		
	@Test
	public void testWebServiceOperation() {
		def client = new DynamicWSClient("http://localhost:8080/axis2/services/BankWebServiceService?wsdl");
		def response = client.addClientToBankList(
			"ws_ComplexType:Xpath:/newClient":null,
			"Xpath:/newClient/address":"Darmstadt",
            "Xpath:/newClient/amount":"2000",
			"Xpath:/newClient/clientFirstName":"Client1",
			"Xpath:/newClient/clientLastName":"NN", 
			"Xpath:/newClient/phone":"21888",
			"Xpath:/bankName":"ZBank");
		
		assertEquals("Test for the response LinkedHashMap", 6, response.size());
		assert response.get("address") == "Darmstadt"
		assert response.get("amount") == "2000"
//		def response2 = client.getNamee("Xpath:/index":"1");
	}
	

}
