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
package de.tud.stg.example.aosd2010.process.domainmodel

public class Process extends ProcessElement {
	
	protected static int lastId = 0
	
	def String id = "Process${lastId++}"
	
	def Set tasks = new HashSet()
	
	def List sequence = new LinkedList()
	
	public Process(String id) { 
		super()
		this.id = id
	}
	
	public String toString() { return "process:$id" }

	public void execute() {
		if (DEBUG) println("INTERPRETER: \t Process.execute id=$id ... ")
		Iterator it = sequence.iterator()
		while (it.hasNext()) {
			Task task = (Task)it.next()
			task.execute()
		}
		if (DEBUG) println("INTERPRETER: \t Process.execute id=$id ... finished. ")
	}
	
	/**
	* Returns an neutral public key.
	* @return
	*/
   public int getPublicKey() {
	   return 0; //TODO The methods are needed also in the Process class since otherwise the Groovy MOP does not recognize them for SecureProcess instances
   }
	   
   /**
	* Returns an neutral private key.
	* @return
	*/
   public int getPrivateKey() {
	   return 0; //TODO The methods are needed also in the Process class since otherwise the Groovy MOP does not recognize them for SecureProcess instances
   }
}
