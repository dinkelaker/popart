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
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.NodeList;

/**
 * This class creates the Soap request message.
 * @author Sara
 *
 */
public class CreatRequest {
/**
 * Creating the SOAPRequestMessage that will be sent to the Service Web.
 * @param targetNSpace        : the targetNameSpace is extracted from WDSL file and used to add the NameSpaceDeclaration to a SOAPRequest envelope as an URI for the prefix 'ws'.
 * @param operationName       : is the name of the selected operation that will be invoked in the Web Service.
 * @param paramNamesToTypes   : is the list of the selected operation's parameters and it contains as key the path of the qualifiedParameter and the type of this parameter is stored in the value part the LinkedHashMap.
 * 								for example :
 							  * <p> 
 							  * <table border="1">
							  *   <tr>
							  *     <td><B>key (qual. name)</B></td>
							  *     <td><B>value (type)</B></td>
							  *   </tr>
							  *   <tr>
							  *     <td>Xpath:/clientFirstName</td>
							  *     <td> xs:string</td>
							  *   </tr>
							  *   <tr>
							  *     <td>xs:complexType::Xpath:/client</td>
							  *     <td>ax24:Client</td>
							  *   </tr>
							  * </table>
							  * </p> 
 * @param paramNamesToValues  : is the list of the selected operation's parameters, it contains the name of the qualifiedParameter as key and the value is stored in the value part of the LinkedHashMap.
 							  for example :
 							  * <p> 
 							  * <table border="1">
							  *   <tr>
							  *     <td><B>key (qual. name)</B></td>
							  *     <td><B>value (qual. Value)</B></td>
							  *   </tr>
							  *   <tr>
							  *     <td>Xpath:/clientFirstName</td>
							  *     <td> Sara</td>
							  *   </tr>
							  *   <tr>
							  *     <td>xs:complexType::Xpath:/client</td>
							  *     <td> null </td>
							  *   </tr>
							  * </table>
							  * </p> 
 * @return					  : is the value of the SOAPRequest message 
 * @throws SOAPFaultException
 * @throws SOAPException
 */
	public  SOAPMessage creatSoapRequestMessageWithParamValues(String targetNSpace, String operationName, LinkedHashMap<String,String> paramNamesToTypes, LinkedHashMap<String,String> paramNamesToValues) throws SOAPException, SOAPFaultException {
		    SOAPMessage request = MessageFactory.newInstance().createMessage();
			SOAPPart part = request.getSOAPPart();
			SOAPEnvelope env = part.getEnvelope();
			env.addNamespaceDeclaration("ws", targetNSpace);
			SOAPBody body = env.getBody();
			SOAPFactory soapfactory =  SOAPFactory.newInstance();
			Name name = soapfactory.createName(operationName, "ws", targetNSpace);
			SOAPBodyElement payload = body.addBodyElement( name );
			 if(paramNamesToTypes.size()!=0){ // Check if the selected operation has parameters of not by verifying the size of 'paramNamesToTypes' LinkedHachMap. 
				 for(Iterator iterator = paramNamesToTypes.keySet().iterator();iterator.hasNext();){
					boolean exist = false;
					String qualifiedParamName = (String)iterator.next();
					String paramNamePart = qualifiedParamName.substring(qualifiedParamName.indexOf("/"));
					String [] qualifiedChain = paramNamePart.split("/");
					qualifiedChain[0] = qualifiedParamName.substring(0, qualifiedParamName.indexOf(":")); // Value is either 'Xpath' or 'ws_complexType'.
					NodeList payloadChildNodes = payload.getChildNodes();
					if (payloadChildNodes.getLength()!=0){
						for (Iterator<SOAPElement>  elementIter =  payload.getChildElements(); elementIter.hasNext();) {
							SOAPElement bodyElementChild = elementIter.next();
							if (bodyElementChild.getLocalName().equals(qualifiedChain[1])) {
								addSoapElementWithParamValues(bodyElementChild,qualifiedChain,2,paramNamesToValues, qualifiedParamName);
								exist = true;
								break;
							} 
						}
						if(exist == false) {	
							SOAPElement newElement = payload.addChildElement(qualifiedChain[1]);
							if(qualifiedChain.length == 2) {
								newElement.addTextNode(paramNamesToValues.get(qualifiedParamName));
							}
							addSoapElementWithParamValues(newElement,qualifiedChain,2,paramNamesToValues, qualifiedParamName);
						}
					}else{
						 SOAPElement newElement = payload.addChildElement(qualifiedChain[1]);
						if((qualifiedChain.length == 2) && (qualifiedChain[0].equals("Xpath"))){
							newElement.addTextNode(paramNamesToValues.get(qualifiedParamName));
						}
						addSoapElementWithParamValues(newElement,qualifiedChain,2,paramNamesToValues, qualifiedParamName);
					}
				}
			}
			 request.saveChanges();	
		     
		     return request;
	}
/**
 * Adding parameter Tags and their values into the created SOAPRequestMessage.
 * @param currentElement : the pointed SOAPElement  
 * @param qualifiersChain : the table that contains the element's name of qualified parameter Path
 * @param index : the index of current element of qualifiersChain table from which we have to start
 * @param paramNamesToValues : the LinkedHashMap that contains the values of the parameters 
 * @param currentQualifiedParamName : the current parameter that we are adding to the SOAPRequestMessage (we need this attribute as a key to get the corresponding value of it from the currentQualifiedParamName)
 * @throws SOAPException
 */
	private  void addSoapElementWithParamValues(SOAPElement currentElement, String [] qualifiersChain, int index, LinkedHashMap<String,String> paramNamesToValues, String currentQualifiedParamName) throws SOAPException{
		Boolean exist= false;
		for(int i=index;i<qualifiersChain.length;i++) {
			String elementName = qualifiersChain[index];
			if (currentElement.getChildNodes().getLength()!=0){
				for (Iterator<SOAPElement>  ElementIter = currentElement.getChildElements(); ElementIter.hasNext();) {
					SOAPElement element = ElementIter.next();
					if(element.getElementQName().getLocalPart().equals(elementName))
					{
						exist = true;
						addSoapElementWithParamValues(element,qualifiersChain,i+1,paramNamesToValues,  currentQualifiedParamName);
						break;
					}
				}
		    }	
		   if(exist == false){
			   SOAPElement newElement = currentElement.addChildElement(elementName);
			   if((i == (qualifiersChain.length-1)) && (qualifiersChain[0].equals("Xpath")))
			   {
			   	   newElement.addTextNode(paramNamesToValues.get(currentQualifiedParamName));
			   }
			   addSoapElementWithParamValues(newElement,qualifiersChain,i+1,paramNamesToValues,  currentQualifiedParamName);
			   break;
		   }
		}

	}	
	
	
	
}
