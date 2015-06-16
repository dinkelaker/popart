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

import java.util.LinkedHashMap;

import javax.wsdl.PortType;

import de.tud.stg.example.aosd2010.casestudy1.DynamicWebServiceClient.DynamicWSClient;

public class ParametersUsedByTestClasses {
	
	public static String portName;
	public static PortType port;
	public static LinkedHashMap<String,String> parameterNamesToTypes = new LinkedHashMap<String,String>();
	public static LinkedHashMap<String,String> parameterNamesToValues = new LinkedHashMap<String,String>();

	public ParametersUsedByTestClasses() {
		DynamicWSClient dynamicWsClient = new DynamicWSClient("http://130.83.165.21:8080/axis2/services/BankWebServiceService?wsdl");
	    port = (PortType)dynamicWsClient.getPortTypeList().get(0);
		portName = dynamicWsClient.getPortName(port);
		parameterNamesToTypes.put("ws_complexType:/newClient", "xs24:Client");
		parameterNamesToTypes.put("Xpath:/newClient/address" , "xs:string");
		parameterNamesToTypes.put("Xpath:/newClient/amount" , "xs:int");
        parameterNamesToTypes.put("Xpath:/newClient/clientFirstName" , "xs:string");
        parameterNamesToTypes.put("Xpath:/newClient/clientLastName" , "xs:string");
        parameterNamesToTypes.put("Xpath:/newClient/phone" , "xs:string");
        parameterNamesToTypes.put("Xpath:/bankName" , "xs:string");
        
        parameterNamesToValues.put("ws_complexType:/newClient", null);
        parameterNamesToValues.put("Xpath:/newClient/address" , "Darmstadt");
        parameterNamesToValues.put("Xpath:/newClient/amount" , "3444");
        parameterNamesToValues.put("Xpath:/newClient/clientFirstName" , "Sara");
        parameterNamesToValues.put("Xpath:/newClient/clientLastName" , "NAJAM");
        parameterNamesToValues.put("Xpath:/newClient/phone" , "0157739");
        parameterNamesToValues.put("Xpath:/bankName" , "Commerzbank");
		
	}
	
}
