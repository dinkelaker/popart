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

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tud.stg.example.aosd2010.casestudy1.DynamicWS.BankWebService;
import de.tud.stg.example.aosd2010.casestudy1.DynamicWebServiceClient.DispatchDynamicInvocation;

/**
 * @author Sara
 *
 */
public class DispachDynamicInvocationTest {
	public DispatchDynamicInvocation dispachDynamicInvocation = new DispatchDynamicInvocation();
	public LinkedHashMap<String, String> responseInformations;
	public QName ServiceName, portName;
	QName responseName;
	public int compter;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
        ParametersUsedByTestClasses parametersUsedByTestClasses = new ParametersUsedByTestClasses();
        responseInformations = dispachDynamicInvocation.invoke
				("http://130.83.165.21:8080/axis2/services/BankWebServiceService?wsdl", 
						"http://DynamicWS.casestudy1.aosd2010.example.stg.tud.de", "BankWebServiceService", parametersUsedByTestClasses.portName,
						"addClientToBankList",parametersUsedByTestClasses.parameterNamesToTypes, parametersUsedByTestClasses.parameterNamesToValues);
		  responseName = new QName("http://DynamicWS.casestudy1.aosd2010.example.stg.tud.de", "addClientToBankListResponse");
          BankWebService bankWebService = new BankWebService();
          compter = bankWebService.Banklist.get(2).getClientSet().size();
	}
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ws.example.DispatchDynamicInvocation#invoke(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)}.
	 * @throws SOAPException 
	 */
	@Test
	public void testInvoke() throws SOAPException {
		SOAPMessage responseRequest =  dispachDynamicInvocation.responseMessage;
		SOAPElement operationElement = 
						(SOAPElement)responseRequest.getSOAPBody().getChildElements(responseName).next();
		SOAPElement returnElement = 
			        	(SOAPElement)operationElement.getChildElements().next();
		assertEquals("Test for the response soapRequest",5 ,returnElement.getChildNodes().getLength());
		// the LinkedHashMap contains (5) responseInformations + (1) value is the name of the method
		assertEquals("Test for the LinkedHashMap that contains the response informations", 6, responseInformations.size());
	}
	

}