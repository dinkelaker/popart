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

aspect(name:"ClosureConversation2",deployed:false,priority:-1) {
	
	//pa_cc2_0
	before (service_call("getRate(.)*") & if_pcd { external }) {
		println "ASPECT(${thisAspect.name}): \t >transfering closure cl2"
		cl2 = { x -> x * x * x } 
	}

	//pa_cc2_1
	before (service_call("getRate(.)*") & if_pcd { external }) {
		println "ASPECT(${thisAspect.name}): \t >Receiving closure cl1 over transferClosure"
		def calculation = cl1.call(10)
		println "ASPECT(${thisAspect.name}): \t >Executed closure cl1 calculation=$calculation"
	}
}

