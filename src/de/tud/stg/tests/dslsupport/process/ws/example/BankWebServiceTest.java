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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tud.stg.example.aosd2010.casestudy1.DynamicWS.BankWebService;
import de.tud.stg.example.aosd2010.casestudy1.DynamicWS.Client;


public class BankWebServiceTest {

	public BankWebService bankWebService = new BankWebService();
	public static int compter;
	@Before
	public void setUp() throws Exception {
		compter = bankWebService.Banklist.get(0).getClientSet().size();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBook() {
		assertEquals("Test for the list of banks which rate is less than 0.6", 2, bankWebService.book(0.6).size());
	}

	@Test
	public void testAddClientToBankList() {
		Client newClient = new Client("Dar", 1988,"Sara", "MM", "091889");
		bankWebService.addClientToBankList(newClient, "Landesbank Berlin Holding");
		assertEquals("Test for adding client to a selected bank's clientList", compter+1, bankWebService.Banklist.get(0).getClientSet().size());
	}

	@Test
	public void testFindClientByLastName() {
		assertEquals("Test for finding client in a selected bank list with his last name", "NAJAM", bankWebService.findClientByLastName("Sara", "Landesbank Berlin Holding").getClientFirstName());
	}

}
