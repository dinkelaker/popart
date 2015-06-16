///////////////////////////////////////////////////////////////////////////////
// Copyright 2009-2015, Technische Universitaet Darmstadt (TUD), Germany
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
package de.tud.stg.example.aop;

aspect(name:"ClosureConversation1",deployed:false) {
	
	//pa_cc1_0
	before (service_call("getRate(.)*") & if_pcd { external }) {
		println "ASPECT(${thisAspect.name}): \t >Receiving closure cl2 over transferClosure"
		def calculation = cl2.call(5)
		println "ASPECT(${thisAspect.name}): \t >Executed closure cl2 calculation=$calculation"
	}
	
	//pa_cc1_1
	before (service_call("getRate(.)*") & if_pcd { external }) {
		println "ASPECT(${thisAspect.name}): \t >transfering closure cl1"
		cl1 = { x -> x * x } 
	}	
}

