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

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Before;
import org.junit.Test;

import de.tud.stg.example.aosd2010.casestudy1.DynamicWebServiceClient.CreatRequest;



public class CreatRequestTest {

	public DynamicWSClientTest dynamicWsClientTest = new DynamicWSClientTest();
	public CreatRequest request = new CreatRequest();
	public  SOAPElement payload = null;
	public SOAPMessage soapmsg;
	public SOAPBody body;
	/**
	 * In the method setUp(), we create and initialize the elements that we need to create the Test.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		   ParametersUsedByTestClasses parameterNamesToTypes = new ParametersUsedByTestClasses();	
	       soapmsg = request.creatSoapRequestMessageWithParamValues("http://DynamicWS.casestudy1.aosd2010.example.stg.tud.de", "addClientToBankList", 
	    		   	                	parameterNamesToTypes.parameterNamesToTypes,parameterNamesToTypes.parameterNamesToValues);
	       System.out.println("********** The request message :*************");
	       soapmsg.writeTo(System.out);
	       System.out.println("");
	       System.out.println("*********************************************");
	       body = soapmsg.getSOAPBody();
	       QName qName = new QName("http://DynamicWS.casestudy1.aosd2010.example.stg.tud.de", "addClientToBankList");
	       payload = (SOAPElement)body.getChildElements(qName).next();
	}


/**
 * Test method for {@link ws.example.CreatRequest#creatSoapRequestMessage(java.lang.String, java.lang.String,java.util.List)}.
 * <p>
 * The SOAPRequest generated in the method "CreatRequest" should be created as follow :
 * <pre>{@code
	  * <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:exam="http://example.ws">
		  * <SOAP-ENV:Header/>
			  * <SOAP-ENV:Body>
				  * <exam:addClientToBankList>
				  * <clientFirstName>AA</clientFirstName>
				  * <clientLastName>AA</clientLastName>
				  * <amount>000</amount>
				  * <address>AA</address>
				  * <phone>000</phone>
				  * <bankName>AAA</bankName>
				  * </exam:addClientToBankList>
			  * </SOAP-ENV:Body>
	  * </SOAP-ENV:Envelope>
	  * }</pre> 
	  *<p> 
 * @throws SOAPFaultException
 * @throws SOAPException
 * @throws IOException
 * 
 */
	@Test
	public void testCreatSoapRequestMessage() throws SOAPFaultException, SOAPException, IOException {
		//assertEquals(message, request.creatSoapRequestMessage("http://example.ws", "addClientToBankList", dynamicWsClientTest.parameters));
		assertEquals("Test for the name of the method is ", "addClientToBankList", payload.getElementName().getLocalName());
		assertEquals("Test for the number of SOAPElements (the attributes of the selected method) ", 2, payload.getChildNodes().getLength());
		assertEquals("Test for the corresponding URI for the prefix 'ws' which is declared in the namespace ","http://DynamicWS.casestudy1.aosd2010.example.stg.tud.de",soapmsg.getSOAPPart().getEnvelope().getNamespaceURI("ws"));
	}

}
