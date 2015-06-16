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
package de.tud.stg.example.aop;

aspect(name:"SecurityDSL",deployed:false) {
	
	before (service_selection("Banking")) {
		println "ASPECT(${thisAspect.name}): \t before service selection advice in $application"
		println "ASPECT(${thisAspect.name}): \t jp=$thisJoinPoint"
		println "ASPECT(${thisAspect.name}): \t category=$category"
		println "ASPECT(${thisAspect.name}): -----------"

		def policyDSL = de.tud.stg.popart.dslsupport.policy.PolicyDSL.getInterpreter(thisJoinPoint.context);
		
		policyDSL.eval {
			def processAssertions = confidentiality & integrity(SAML)
			def processPolicy = convertToPolicy(processAssertions)
			println "processPolicy=${processPolicy}"
		}
	}
	
//	after (service_selection("Banking")) {
//		println "ASPECT(${thisAspect.name}): \t after service selection advice in $application"
//		println "ASPECT(${thisAspect.name}): \t selectedServices=$selectedServices"
//	}
//	
//	before (service_call("getRate(.)*") & if_pcd { external }) {
//		
//		println "ASPECT(${thisAspect.name}): \t before service call advice"
//		println "ASPECT(${thisAspect.name}): \t calling (${external? "external":"internal"}) service $serviceName"
//
//		def applicationName = application
//		def process = system.applications[applicationName].process
//		println "ASPECT(${thisAspect.name}): \t applicationName=$applicationName "
//		println "ASPECT(${thisAspect.name}): \t system.applications=$system.applications "
//		println "ASPECT(${thisAspect.name}): \t external=$external "
//		println "ASPECT(${thisAspect.name}): \t process=$process.id "
//		def tasks=process.sequence;
//		println "ASPECT(${thisAspect.name}): \t tasks $tasks"		
//	}
//
//	after (service_call('getRate(.)*') & if_pcd { external }) {
//		println "ASPECT(${thisAspect.name}): \t after service call advice" 
//	}

}

