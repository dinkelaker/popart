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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This Class allow us to extract all the informations we need to invoke a method by having just the URI of the WSDL file of the web service.
 * @author Sara
 *
 */
public class DynamicWSClient {
	
	private static final boolean DEBUG = false;
	
	public  String targetNamespace = null;
	public  String WSDLURI;
	public  Definition definition = null;
	public  LinkedHashMap<String,String> paramNamesToTypes = new LinkedHashMap<String,String>();
	
	/**
	 * Get the definition object and the targetNameSpace from WSDL file
	 * @param wsdlURI : the URI of WSDL file
	 * @throws WSDLException
	 */
	public void init(String wsdlURI) {
		
		WSDLURI = wsdlURI;
		try {

		    WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		    definition = reader.readWSDL(WSDLURI);
		} catch(WSDLException e) {
			e.printStackTrace();
		}

		if(definition==null) {
			  System.out.println("No definition is found !!");
		}
		targetNamespace = definition.getTargetNamespace();
		
	}
	public DynamicWSClient(String wsdlURI){
		super();
		init(wsdlURI);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Get the portType that will allow us to find the operations we can invoke
	 * @return all the portType that exist in WSDL
	 * NB : we had chosen the first Port Type in the PortTypeList to work with in 'invokeService' method (as we have just one Port type in our case)
	 */
	public Vector getPortTypeList(){		
	    Map ports = definition.getPortTypes();
    	Vector<PortType> allPorts = new Vector<PortType>();
			for (Iterator<PortType>  ElementIter = ports.keySet().iterator(); ElementIter.hasNext();) {
			    Object object = ElementIter.next();
			    Object obj = ports.get(object);
			    if(obj instanceof PortType) {
				allPorts.add((PortType)obj);
			    }
			}
			return allPorts;
	}
	/**
	 * get the list of operations for the first port Type
	 * @return list of the service's operations
	 */
	public List getOperationsList(){
		return getOperationsList(0);
	}
	/**
	 * Get the list of operations from a specific port Type	
	 * @param port the selected port Type 
	 * @return list of the service's operations we can invoke
	 */
	public List getOperationsList(int portTypeIndex ){
		List operationList = ((PortType)getPortTypeList().get(portTypeIndex)).getOperations();
		return operationList;
	}
	/**
	 * Get the service from WSDL
	 * @return the Service
	 */
	public Service getService(){
		Service serv = null;
		Map services = definition.getServices();
		for (Iterator<Binding>  ElementIter = services.keySet().iterator(); ElementIter.hasNext();) {
			  serv = (Service)services.get(ElementIter.next());
			
		}
		return serv;
	}
	/**
	 * Find the binding Qname with the port type that correspond to our selected one in order to extract the port name that we'll use to invoke the Service
	 * @param port the port Type that contains the operations
	 * @return the name of the port
	 */
	public String getPortName(PortType port){
		
		QName bindingQName = null;
		Map bindings = definition.getBindings();
		for (Iterator<Binding>  ElementIter = bindings.keySet().iterator(); ElementIter.hasNext();) 
		{
			Binding binding = (Binding)bindings.get(ElementIter.next());
		// There are three bindings which portType is equal to 'port'(binding for: soap11, soap12 and http), so we add the second test to specify 
		//which type of protocol we want to use	.
			if((binding.getPortType()== port) && (binding.getQName().getLocalPart().contains("Soap11")))
			{
				bindingQName = binding.getQName();
			    }
		}
		if(bindingQName==null) {
		    System.out.println("the binding is not found for the chosen PortType"); 
		}
		String portName = null;
	    Map portMap = getService().getPorts();
		for (Iterator<Binding>  ElementIter = portMap.keySet().iterator(); ElementIter.hasNext();) {
			Port p = (Port)portMap.get(ElementIter.next());
			if(p.getBinding().getQName().toString().equals(bindingQName.toString())) {
			     portName = p.getName();
			    break;
			}
		    if(portName != null) break;
		}
		return portName;
		
	}
/**
   	 * Find the input parameters for a specific operation  
 * @param operation : the selected operation
 * @return  a LinkedHashMap of parameters that contains as key the name of the parameter and stock in the value the type of this parameter
 */
	public LinkedHashMap<String,String> getParameters(Operation operation){
		Input entree = operation.getInput();
		Map parts =entree.getMessage().getParts();
		String name = "";
		for (Iterator iteratorParts = parts.keySet().iterator();iteratorParts.hasNext();){
            Part part = (Part)parts.get(iteratorParts.next());
            //Check if the parameter's type is a complexType or a simple Type 
            //when the parameter is a complexType, the attribute 'Type' of the part's Tag is null and instead of that, the Part Tag contains an 'element'
            //attribute that contains as value the name of the corresponding element in Type List of WSDL
            if (part.getTypeName() == null){
	            name = part.getElementName().getLocalPart();
	            Types types = definition.getTypes();
	            List extensibleTypeList = types.getExtensibilityElements();
	            for (int i=0; i<extensibleTypeList.size();i++){
	            	ExtensibilityElement extElement = (ExtensibilityElement)extensibleTypeList.get(i);
	            	if ((extElement != null) && (extElement instanceof Schema)){
	            		Element element = ((Schema)extElement).getElement();
	            		NodeList childNodes = element.getChildNodes();
	            		complexType(childNodes,name,paramNamesToTypes,"Xpath:");
	            	}
	            }
            }
            else{
            	String type = part.getTypeName().getLocalPart();
            	name = part.getName();
            	paramNamesToTypes.put(name, type);
            }
        }
		if(DEBUG){
		if(paramNamesToTypes.size()==0)
		{
			System.out.println("There is no parameter for this method");
		}else {
			System.out.println("\nThe parameter(s) of \" "+operation.getName()+"\""+" is(are)");
			for(Iterator iterator = paramNamesToTypes.keySet().iterator();iterator.hasNext();){
	        	System.out.print(iterator.next());
	        	System.out.println("");
	        }
		}
		}
		return paramNamesToTypes;
	}
	 /**
	  * Resolving the names and their complex types from a parameter structure (e.g. request or response).
	  * 
	  * <p>
	  * Input:
	  * <pre>{@code
	  * <xml>
	  * <xs:element name="clientName">
	  *  <xs:complexType>
	  *		<xs:sequence>
	  *		  <xs:element minOccurs="0" name="client" nillable="true" type="ax24:Client"/>
	  *		</xs:sequence>
	  *	 </xs:complexType>
	  *</xs:element>
	  *	.
	  *	.	
	  *	.
	  *<xs:complexType name="Client">
	  *	<xs:sequence>
	  *		<xs:element minOccurs="0" name="address" nillable="true" type="xs:string"/>
      *		<xs:element minOccurs="0" name="amount" type="xs:int"/>
	  *		<xs:element minOccurs="0" name="clientFirstName" nillable="true" type="xs:string"/>
	  *		<xs:element minOccurs="0" name="clientLastName" nillable="true" type="xs:string"/>
	  *		<xs:element minOccurs="0" name="phone" nillable="true" type="xs:string"/>
	  *	</xs:sequence>
	  *</xs:complexType>
	  * </xml>
	  * }</pre>
	  * Output:
	  * 
	  * <table border="1">
	  *   <tr>
	  *     <td><b>key (qual. name)</b></td>
	  *     <td><b>value (type)</b></td>
	  *   </tr>
	  *   <tr>
	  *     <td>xs::complexType:Xpath:/client</td>
	  *     <td>ax24:Client</td>
	  *   </tr>
	  *   <tr>
	  *     <td>xs::complexType:Xpath:/client/address</td>
	  *     <td>xs:string</td>
	  *   </tr>
	  *   <tr>
	  *     <td>xs::complexType:Xpath:/client/amount</td>
	  *     <td>xs:int</td>
	  *   </tr>
	  *   <tr>
	  *     <td>xs::complexType:Xpath:/client/clientFirstName</td>
	  *     <td>xs:string</td>
	  *   </tr>
	  *   <tr>
	  *     <td>xs::complexType:Xpath:/client/clientLastName</td>
	  *     <td>ws:string</td>
	  *   </tr>
	  *   <tr>
	  *     <td>xs::complexType:Xpath:/client/phone</td>
	  *     <td>ws:string</td>
	  *   </tr>   
	  * </table>
	  * </p>
	  * 
	  * @param childNodes List of the childNodes of the current complex Type element
	  * @param complexTypeName the name of the current complex Type parameter
	  * @param params A map that associates the qualified parameter name with its type.
	  * @param parent the Path of qualified parameter's name
	  */
	private void complexType(NodeList childNodes,String complexTypeName, LinkedHashMap<String,String> params, String parent) {
		for (int j=0; j<childNodes.getLength();j++){
			if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE){
				Element elm = (Element)childNodes.item(j);
				if (elm.getLocalName().equals("element"))
	        	{
					if (elm.getAttribute("name").equals(complexTypeName))
					{
						if(elm.getAttribute("type").length() == 0)
						{
							Element complexTypeElm = getFirstElement(elm);
		        			Element sequenceElm = getFirstElement(complexTypeElm);
		        			if (sequenceElm == null)
		        				continue;
		        			NodeList nl2 = sequenceElm.getChildNodes();
		        			for (int k=0;k<nl2.getLength();k++)
		        			{
		        				if (nl2.item(k).getNodeType() == Node.ELEMENT_NODE)
		        	        	{
		        					String eltNom = ((Element)nl2.item(k)).getAttribute("name");
		    						String type2 = ((Element)nl2.item(k)).getAttribute("type");
			        				if (!type2.startsWith("xs:"))
					        		{
			        					params.put("xs_complexType:"+parent +"/"+eltNom,type2);
			        					for (int i=0; i<definition.getTypes().getExtensibilityElements().size();i++){
			        						ExtensibilityElement extElement = (ExtensibilityElement)definition.getTypes().getExtensibilityElements().get(i);
			        						if ((extElement != null) && (extElement instanceof Schema))
			        		            	{
			        		            		Element schemaElement = ((Schema)extElement).getElement();
			        		            		//if(schemaElement.getAttribute(targetNamespace).equals(type2.substring(0, type2.indexOf(":"))))
			        		            		//{
			        		            		//	System.out.println("try to get something benefic !! "+schemaElement.lookupNamespaceURI(type2.substring(0, type2.indexOf(":"))));
			        		            			complexType(schemaElement.getChildNodes(), type2.substring(type2.indexOf(":") + 1), params, parent +"/"+ eltNom);
			        		            		//}
			        		            		
			        		            		}
			        					
			        					}
					        		}
					        		else
					        		{
					        			params.put(parent +"/"+eltNom,type2);
					        		}			
		        	        	}
		        			}
						}
						if (!elm.getAttribute("type").startsWith("xs:"))
		        		{
							complexType(childNodes, elm.getAttribute("type").substring(elm.getAttribute("type").indexOf(":") + 1), params, parent+" : " + elm.getAttribute("name"));
		        		}
		        		else
		        		{	
		        			params.put(parent + elm.getAttribute("name"),elm.getAttribute("type"));
		        		}

					}
		        	
	        	}
				else if (elm.getLocalName().equals("complexType"))
	        	{
	        		if (elm.getAttribute("name").equals(complexTypeName))
		        	{
	        			Element eltSequence = getFirstElement(elm);
	        			if (eltSequence == null)
	        				continue;
	        			NodeList nl2 = eltSequence.getChildNodes();
	        			for (int k=0;k<nl2.getLength();k++)
	        			{
	        				if (nl2.item(k).getNodeType() == Node.ELEMENT_NODE)
	        	        	{
		        				String eltNom = ((Element)nl2.item(k)).getAttribute("name");
		        				String type2 = ((Element)nl2.item(k)).getAttribute("type");
		        				if (!type2.startsWith("xs:"))
				        		{
		        					params.put("xs_complexType:"+parent +"/"+ eltNom,type2);
		        					complexType(childNodes, type2.substring(type2.indexOf(":") + 1), params, parent + "/"+eltNom);
				        		}
				        		else
				        		{
				        			
				        			params.put(parent +"/"+ eltNom,type2);
				        		}
	        	        	}
	        			}	
		        	}
	        	}
			}
			
		}
	}
   	private Element getFirstElement(Element elt)
	{
		NodeList nl = elt.getChildNodes();
		for (int j=0;j<nl.getLength();j++)
        {
        	if (nl.item(j).getNodeType() == Node.ELEMENT_NODE)
        	{
        		return (Element)nl.item(j);
        	}
        }
		return null;
	}
	
   	/**
	 * Call an operation of at a particular endPoint.
	 * @param endPoint The name of the end point from the WSDL (i.e. XPATH:\\service\port@name).
	 * @param selectedOperation The operation name.
	 * @param paramNamesToValues The parameters encoded in a map.
	 */
    public LinkedHashMap<String, String> invokeService(String endPoint, String selectedOperation, LinkedHashMap<String,String> paramNamesToValues){
    	DispatchDynamicInvocation dispachDynamicInvocation = new DispatchDynamicInvocation();
    	try {
			return dispachDynamicInvocation.invoke(WSDLURI, targetNamespace, getService().getQName().getLocalPart(), endPoint,
					   									selectedOperation, paramNamesToTypes, paramNamesToValues);
	   } catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
   	
   	/**
	 * Call the Web service on the first port type whilst specifying the operation	that we want to invoke.
	 * This method should only be used, when there is only one port type. 
	 * @param selectedOperation
	 * @param port
	 */
    public LinkedHashMap<String, String> invokeService(String selectedOperation, LinkedHashMap<String,String> paramNamesToValues){
    	LinkedHashMap<String, String> paramTest = invokeService(0, selectedOperation, paramNamesToValues);
    	return paramTest;
    }
   	
    private LinkedHashMap<String, String> invokeService(int portTypeIndex, String selectedOperation, LinkedHashMap<String,String> paramNamesToValues){
		DispatchDynamicInvocation dispachDynamicInvocation = new DispatchDynamicInvocation();
	    try {
		    return dispachDynamicInvocation.invoke(WSDLURI, targetNamespace, getService().getQName().getLocalPart(), getPortName((PortType)getPortTypeList().get(portTypeIndex)),
				   									selectedOperation, paramNamesToTypes, paramNamesToValues);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return null;
    }
    
    public Operation getOperationByName(String operationName){
    	LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
    	List operations = this.getOperationsList();
    	for(int l=0;l<operations.size();l++){
			Operation op = (Operation)operations.get(l);
			if(op.getName().equalsIgnoreCase(operationName)) {
				return op;
			}
    	}
    	return null;
    }
    
    public Object methodMissing(String name, Object args) {
    	LinkedHashMap<String, String> paramNamesToValues = new LinkedHashMap<String, String>();
    	Object[] argsArray = (Object[])args; 
    	
    	if (DEBUG)  {
    	    for (Object param : argsArray) {
    		    System.out.print(param+", ");
    	    }
    	    System.out.println("");
    	}
    	
    	String selectedOperation = name;
    	Operation operation = getOperationByName(selectedOperation);
    	LinkedHashMap<String, String> paramNamesToTypes =  getParameters(operation);
    	this.paramNamesToTypes=paramNamesToTypes;
        paramNamesToValues = (LinkedHashMap)argsArray[0];
        return invokeService(selectedOperation, paramNamesToValues);
    }

}

