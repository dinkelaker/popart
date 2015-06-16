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
package de.tud.stg.example.aosd2010.casestudy1.DynamicWebServiceClient;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
/**
 * This Class creates a Dispatch dynamic client in order to do the following tasks  :
 * 	- Sending the Soap request message to the service web
 * 	- Receiving the Soap response message from the service and treat the informations extracted from it  
 * @author Sara
 *
 */

public class DispatchDynamicInvocation {
	
	public SOAPMessage responseMessage;
	LinkedHashMap<String,String> invokationResponseValues = new LinkedHashMap<String,String>();

/**
 * Invoking the Web Service and treating the SOAPResponseMessage in order to get the response informations
 * @param wsdluri : 	 the URI of the WSDL file (needed to specify the location of the WSDL file)
 * @param targetNSpace : retrieving from the WSDL file, the targetNameSpace is used to create the Qname of the Service and the port .  
 * @param serviceName :  the name of the Service Web that we want to invoke  
 * @param endPoint :  	 the name of the port used to create the Dispatch
 * @param operationName: the name of the selected operation that we want to call in the web service
 * @param paramNamesToTypes : List of the selected operation's parameters with their Types
 * @param paramNamesToValues: List of the selected operation's parameters with their values
 * @return : the SOAPResponseMessage that contains the response information after invoking operation in the service web
 * @throws IOException
 */
	public  LinkedHashMap<String, String> invoke (String wsdluri, String targetNSpace, String serviceName,String endPoint, String operationName,  LinkedHashMap<String,String> paramNamesToTypes, LinkedHashMap<String,String> paramNamesToValues) throws IOException {
		try{
			URL wsdlLocation = new URL(wsdluri);
			QName dispatchServiceQName = new QName(targetNSpace, serviceName);
			QName dispatchPortName = 
					new QName(targetNSpace,endPoint);
			Service service = Service.create(wsdlLocation, dispatchServiceQName);
			Dispatch<SOAPMessage> dispatch = service.createDispatch(dispatchPortName, SOAPMessage.class,
			        Service.Mode.MESSAGE);
			CreatRequest messageRequest = new CreatRequest();
			SOAPMessage request = messageRequest.creatSoapRequestMessageWithParamValues(targetNSpace, operationName, paramNamesToTypes, paramNamesToValues);
			try {
				//request.writeTo(System.out);
			    responseMessage = dispatch.invoke(request);
			   // responseMessage.writeTo(System.out);
			} catch (javax.xml.ws.soap.SOAPFaultException sfe) {
				sfe.printStackTrace();
				System.out.println("cause: "+sfe.getCause());
				System.out.println("fault: "+sfe.getFault());
				System.out.println("msg: "+sfe.getMessage());
				throw sfe;
			}
			SOAPBody bodyResponse = responseMessage.getSOAPBody();
			QName responseName =
			new QName(targetNSpace, operationName+"Response");
			SOAPElement operationNameElement = (SOAPElement)bodyResponse.getChildElements(responseName).next();
			SOAPElement Return = (SOAPElement)operationNameElement.getChildElements().next();
			return addAttributes(operationNameElement);
		}catch(SOAPException exception){
			exception.printStackTrace();
		}catch(MalformedURLException excep){
			excep.printStackTrace();
		}
		return null;
	}
/**
 * Getting the response informations from SOAPRsponseMessage and print them 
 * @param element : the current SOAPElement from which we get the informations (name , value) .
 */
	private  LinkedHashMap<String, String> addAttributes(SOAPElement element) {
		System.out.println(" *** "+element.getLocalName() +" : ");
		for (Iterator<SOAPElement>  ElementIter = element.getChildElements(); ElementIter.hasNext();) {
			SOAPElement Res = ElementIter.next();
			String NameResult = Res.getLocalName();
			String Value = Res.getValue();
			invokationResponseValues.put(NameResult, Value);
			if(Res.getChildNodes().getLength()>1){
				addAttributes(Res);
				System.out.println(" *** "+NameResult +" ***");
			}else{
				System.out.println("          The value of "+NameResult +" is  "+Value);
			}	
		}
		
		return invokationResponseValues;
		
	}

}
