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

import de.tud.stg.popart.dslsupport.policy.PolicyDSL;
import de.tud.stg.popart.dslsupport.listsets.ListSetsDSL;
import de.tud.stg.popart.dslsupport.DSLCreator;

aspect(name:"CombinedDSL",deployed:false) {
	
	before (service_selection("Banking")) {
		println "ASPECT(${thisAspect.name}): \t before service selection advice in $application"
		println "ASPECT(${thisAspect.name}): \t jp=$thisJoinPoint"
		println "ASPECT(${thisAspect.name}): \t category=$category"

		def policyDSL = new PolicyDSL();
		def listSetsDSL = new ListSetsDSL();
		def combinedDSL = DSLCreator.getCombinedInterpreter(policyDSL,listSetsDSL,thisJoinPoint.context);
		
		String processPolicy;
		combinedDSL.eval {
			List choices = [confidentiality,integrity(SAML)];
	        //println "ASPECT(${thisAspect.name}): \t choices=${choices}"
			def allPossibilities = powerSet(choices) 
            //println "ASPECT(${thisAspect.name}): \t allPossibilities=${allPossibilities}"
			def valids = difference(allPossibilities,SET_CONTAINING_EMPTY_SET) //with out empty set
            //println "ASPECT(${thisAspect.name}): \t valids=${valids}"
			def processAssertions = convertToDNF(valids) 
            //println "ASPECT(${thisAspect.name}): \t processAssertions=${processAssertions}"
			processPolicy = convertToPolicy(processAssertions)
		}
		println "ASPECT(${thisAspect.name}): \t processPolicy=${processPolicy}"
	}
	
	after (service_selection("Banking")) {
		println "ASPECT(${thisAspect.name}): \t after service selection advice in $application"
		println "ASPECT(${thisAspect.name}): \t selectedServices=$selectedServices"
	}
	
	before (service_call("getRate(.)*") & if_pcd { external }) {
		
		println "ASPECT(${thisAspect.name}): \t before service call advice"
		def applicationName = application
		def process = system.applications[applicationName].process
		println "ASPECT(${thisAspect.name}): \t process=$process.id "
		def tasks=process.sequence;
		println "ASPECT(${thisAspect.name}): \t tasks $tasks"		
	}

	after (service_call('getRate(.)*') & if_pcd { external }) {
		println "ASPECT(${thisAspect.name}): \t after service call advice" 
	}

}

