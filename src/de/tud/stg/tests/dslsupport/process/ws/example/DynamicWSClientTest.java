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

import java.util.LinkedHashMap;

import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.tud.stg.example.aosd2010.casestudy1.DynamicWebServiceClient.DynamicWSClient;

/**
 * @author Sara
 *
 */
public class DynamicWSClientTest {

	public static DynamicWSClient dynamicWsClient;
	public static ParametersUsedByTestClasses parametersUsedByTestClasses;
	public static PortType port;
	
	
	@BeforeClass
	public static void setUp() 
    {
		dynamicWsClient = new DynamicWSClient("http://130.83.165.21:8080/axis2/services/BankWebServiceService?wsdl");
  	    parametersUsedByTestClasses = new ParametersUsedByTestClasses();
  		port = parametersUsedByTestClasses.port;
    }

	
	/**
	
	/**
	 * Test method for {@link ws.example.DynamicWSClient#getPortTypeList()}.
	 * @throws WSDLException 
	 */
	@Test
	public void testGetPortTypeList() throws WSDLException {		
		assertEquals("The nomber of PortTypes in WSDL file", 1, dynamicWsClient.getPortTypeList().size());
	}

	/**
	 * Test method for {@link ws.example.DynamicWSClient#getOperationsList(javax.wsdl.PortType)}.
	 */
	@Test
	public void testGetOperationsList() {
		assertEquals("The nomber of Methods for the selected PortType in WSDL file", 6, dynamicWsClient.getOperationsList().size());
	}

	/**
	 * Test method for {@link ws.example.DynamicWSClient#getService()}.
	 */
	@Test
	public void testGetService() {
		assertEquals("BankWebServiceService", dynamicWsClient.getService().getQName().getLocalPart());
		assertEquals("The number of Port in the service", 3,dynamicWsClient.getService().getPorts().size());
	}

	/**
	 * Test method for {@link ws.example.DynamicWSClient#getPortName(javax.wsdl.PortType)}.
	 */
	@Test
	public void testGetPortName() {
		assertEquals("BankWebServiceServiceHttpSoap11Endpoint", parametersUsedByTestClasses.portName);
	}

	/**
	 * Test method for {@link ws.example.DynamicWSClient#getParameters(javax.wsdl.PortType, javax.wsdl.Operation)}.
	 */
	@Test
	public void testGetParameters() {
		for(int i=0;i< dynamicWsClient.getOperationsList().size(); i++){
			Operation op = (Operation)dynamicWsClient.getOperationsList().get(i);
			if(op.getName().equals("addClientToBankList")){
				LinkedHashMap<String,String> parameters = dynamicWsClient.getParameters(op);
				assertEquals("The number of parameters of the method 'addClientToBankList' ", 7,parameters.size());
			}
			
		}
		
	}

	/**
	 * Test method for {@link ws.example.DynamicWSClient#invokeService(java.lang.String, javax.wsdl.PortType)}.
	 */
	@Test
	public void testInvokeService() {
		dynamicWsClient.paramNamesToTypes = parametersUsedByTestClasses.parameterNamesToTypes;
		LinkedHashMap <String, String>paramNamesToValues = parametersUsedByTestClasses.parameterNamesToValues;
		assertEquals("Test for the response LinkedHashMap", 6, dynamicWsClient.invokeService("addClientToBankList", paramNamesToValues).size());
	}

}
