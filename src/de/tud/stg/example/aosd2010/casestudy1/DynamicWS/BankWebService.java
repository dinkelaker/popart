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
package de.tud.stg.example.aosd2010.casestudy1.DynamicWS;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT,use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)

public class BankWebService {
	
public List <Bank> Banklist = new ArrayList<Bank>();


 private void init(){
	 
	 Client bankClient11 = new Client("Bettinastrasse,Francfurt",7000, "CASSICI", "Luca", "015775398730");
	 Client bankClient12 = new Client("Landstrasse,Darmstadt",2000,  "NAJAM", "Sara", "015775398730");
	 ArrayList<Client> clientSet1 = new ArrayList<Client>();
	 clientSet1.add(bankClient11);
	 clientSet1.add(bankClient12);
	 Banklist.add(new Bank("Landesbank Berlin Holding",clientSet1,0.2));
	 
	 Client bankClient21 = new Client("Rhoden, Diemelstadt", 9800, "JEAN", "Nicola", "015774968730");
	 Client bankClient22 = new Client("Seulberg, Friedrichsdorf",1500,  "ANNA", "Mery", "015775300730");
	 ArrayList<Client> clientSet2 = new ArrayList<Client>();
	 clientSet2.add(bankClient21);
	 clientSet2.add(bankClient22);
	 Banklist.add(new Bank("Deutsche",clientSet2,0.8));
	 
	 Client bankClient31 = new Client("Landstrasse,Darmstadt",9800, "JENY", "Katalena",  "015774968730");
	 Client bankClient32 = new Client("Rhönring, Darmstadt",1500,  "MATIEU", "Marya", "015775300730");
	 ArrayList<Client> clientSet3 = new ArrayList<Client>();
	 clientSet3.add(bankClient31);
	 clientSet3.add(bankClient32);
	 Banklist.add(new Bank("Commerzbank",clientSet3,0.5));
	 
 }
	
 public BankWebService(){
	 super();
	 init();
	}
	
@WebMethod public Bank BanksInformation(int index){
		
				return Banklist.get(index);
		
	      }
	
@WebMethod public double getPrice(float amount, int index ){
				return amount*(1+Banklist.get(index).getInterestRate());
	       }
	
@WebMethod public String getNamee(int index){
				return Banklist.get(index).getBankName();
		   }
/**
 * 	Getting a list of the Banks which rate is less or equal than the input value 'rate'
 * @param rate 
 * @return the list of the Banks  
 */
@WebMethod public List<Bank> book(double rate){
				ArrayList<Bank> res = new ArrayList<Bank>();
				for(int i=0; i<Banklist.size();i++){
					if (Banklist.get(i).getInterestRate()<=rate)
						res.add(Banklist.get(i));
				}
				return res;
		  }
/**
 * Adding the new client to the selected bank list of clients
 * Example of method with the complex Type 'Client' given in the input as a set of his attributes
 * @params client's informations
 * @param bankName the selected bank name
 * @return the client (just to test the output complex type is recognized)
 */
@WebMethod public Client addClientToBankList(Client newClient,String bankName){
				for(int k=0; k<Banklist.size();k++){
					if(Banklist.get(k).bankName.equals(bankName)){
						Banklist.get(k).clientSet.add(newClient);
					}
				}
				return newClient;
			}

/**
 * Getting the Client whose name is given as input 
 * @param clientName the name of the client
 * @param bankName the name of the selected Bank
 * @return the client that we are searching for
 */
@WebMethod public  Client findClientByLastName(String clientLastName, String bankName) {
				for(int k=0; k<Banklist.size();k++){
					if(Banklist.get(k).bankName.equals(bankName)){
						for (Iterator<Client> clientIter = Banklist.get(k).clientSet.iterator();clientIter.hasNext();){
							Client currentClient = clientIter.next();
							if(currentClient.getClientLastName().equals(clientLastName)){
								return currentClient;
							}
						}
					}
				}
				return null;
			}

}
