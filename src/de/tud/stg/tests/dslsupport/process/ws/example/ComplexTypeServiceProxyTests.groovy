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
package de.tud.stg.tests.dslsupport.process.ws.example

import static org.junit.Assert.assertEquals

import org.junit.BeforeClass
import org.junit.Test

import de.tud.stg.example.aosd2010.casestudy1.DynamicWebServiceProxy
import de.tud.stg.example.aosd2010.process.domainmodel.DynamicRegistry
import de.tud.stg.example.aosd2010.process.domainmodel.ServiceProxy



class ComplexTypeServiceProxyTests {
	
	 def static services;
	 def static DynamicRegistry registry;
	 def operations;
	 def service;
	 
	@BeforeClass
	public static void setUp() throws Exception{
		DynamicWebServiceProxy.init();
		registry = DynamicRegistry.getInstance();
		services = registry.find("ComplexTypeBanking");
	}
	
	@Test
	public void testServiceList(){
		assert services.size() > 0;
		assertEquals("Test for the number of services",1,services.size());
		Iterator iter = services.iterator();
		while(iter.hasNext()){
			service = (ServiceProxy)iter.next();
			System.out.println("The name of the service is : "+service.getName());
		}
	}
	
	@Test
	public void testOperationsList(){
		for(service in services){
			if(service.getName().equals("BookOfTheWeb")){
			operations = service.getServiceOperations();
			assertEquals("Test for the list of operations",4,operations.size());
			}
		    if(service.getName().equals("BankOnline")){
				operations = service.getServiceOperations();
				assertEquals("Test for the list of operations",7,operations.size());
			}
		}
		
	}
		
	@Test
	public void testGetParameters (){
		for(service in services){
			if(service.getName().equals("BookOfTheWeb")){
				assertEquals("Test for the selected method's parameters", 1,service.getParameters("findBooksByAuthorLastName").size());
			}
			if(service.getName().equals("BankOnline")){
				assertEquals("Test for the selected method's parameters", 7,service.getParameters("addClientToBankList").size());
			}
		}
	}
	
	@Test
	public void testMissingMethodAndCallOperations(){
		for(service in services){
			if(service.getName().equals("BookOfTheWeb")){
				LinkedHashMap<String,String> paramMap = new LinkedHashMap<String,String>();
				paramMap.put("Xpath:/authorname", "Dearle");
				LinkedHashMap responseMap = service.call("findBooksByAuthorLastName", paramMap);
			}
			if(service.getName().equals("BankOnline")){
				LinkedHashMap<String,String> paramMap = new LinkedHashMap<String,String>();
				paramMap.put("ws_complexType:/newClient", " ");
		        paramMap.put("Xpath:/newClient/address" , "Darmstadt");
		        paramMap.put("Xpath:/newClient/amount" , "3444");
		        paramMap.put("Xpath:/newClient/clientFirstName" , "Sara");
		        paramMap.put("Xpath:/newClient/clientLastName" , "NAJAM");
		        paramMap.put("Xpath:/newClient/phone" , "0157738");
		        paramMap.put("Xpath:/bankName" , "Commerzbank");
		
				//LinkedHashMap responseMap = service.call("addClientToBankList", paramMap);
				LinkedHashMap responseMap = service.addClientToBankList(paramMap);
			}
		}
	}
	
}

