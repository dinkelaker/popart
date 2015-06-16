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
package de.tud.stg.example.aosd2010.casestudy1;

import java.util.LinkedHashMap

import javax.wsdl.Operation

import de.tud.stg.example.aosd2010.casestudy1.DynamicWebServiceClient.DynamicWSClient
import de.tud.stg.example.aosd2010.process.domainmodel.ServiceProxy
import de.tud.stg.example.aosd2010.process.domainmodel.DynamicRegistry;


public class DynamicWebServiceProxy extends ServiceProxy {
	
	private static final boolean DEBUG = true;
	public def client;
	 static registry = DynamicRegistry.getInstance();
	
	
	
	public static void init() {
		
		System.out.println("Registering services");
		registry.clear();
		registry.register(new DynamicWebServiceProxy("LBHBank","http://130.83.165.21:8080/axis2/services/LBHBank?wsdl","Banking"));
		registry.register(new DynamicWebServiceProxy("DeutscheBank","http://130.83.165.21:8080/axis2/services/DeutscheBank?wsdl","Banking"));
		registry.register(new DynamicWebServiceProxy("CommerzBank","http://130.83.165.21:8080/axis2/services/CommerzBank?wsdl","Banking"));
		registry.register(new DynamicWebServiceProxy("BankOnline","http://130.83.165.21:8080/axis2/services/BankWebServiceService?wsdl","ComplexTypeBanking"));
		registry.register(new DynamicWebServiceProxy("BookOfTheWeb","http://130.83.165.21:8080/axis2/services/Book?wsdl","Shopping"));

	}
		
	public DynamicWebServiceProxy(String name, String endPoint,String category) {
		super(name,endPoint,category);
		client = new DynamicWSClient(endPoint);
	}

	public  Object call(String operation, ArrayList args){
	System.out.println("Empty method");
	}
	
	public Object call(String operation, LinkedHashMap<String,String> args) {
		//if(DEBUG)
		//System.out.println(name+" \t call op="+operation+" args="+args);
		return client."$operation"(args);
	}
	
	public List getServiceOperations() {
		List operationList = client.getOperationsList();
		if(DEBUG){
			int i =1;
			System.out.println("**********the list of "+getName() +" operations: ");
			for(op in operationList){
				Operation operation = (Operation)op;
				System.out.println(i+")"+operation.getName());
				i++;
			}
		}
		return operationList;
	}
	
	public LinkedHashMap<String,String> getParameters(String operationName){
		Operation operation = client.getOperationByName(operationName);
		LinkedHashMap<String,String> parameters =client.getParameters(operation);
		if(DEBUG == true){
			System.out.println("****** The list of "+operationName+" parameters: ");
			Iterator iterator = parameters.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				if(DEBUG){
				System.out.println(key + " = " + value);
				}
			}
		}
		return parameters;
	}
	
	public Object methodMissing(String name, Object args){
		Object[] argsArray = (Object[])args;
		LinkedHashMap<String, String> paramNamesToValues = (LinkedHashMap)argsArray[0];
		call(name, paramNamesToValues);
	}
}
