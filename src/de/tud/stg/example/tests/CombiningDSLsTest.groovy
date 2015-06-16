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
package de.tud.stg.example.tests

import de.tud.stg.popart.dslsupport.policy.PolicyDSL;
import de.tud.stg.popart.dslsupport.listsets.ListSetsDSL;
import de.tud.stg.popart.dslsupport.DSLCreator;

def policyDSL = new PolicyDSL();
def listSetsDSL = new ListSetsDSL();
def combinedDSL = DSLCreator.getCombinedInterpreter(policyDSL,listSetsDSL,[:]);

String processPolicy;
combinedDSL.eval {
	List choices = [confidentiality,integrity(SAML)];
    //println "\t possible choices=${choices}"
	def allPossibilities = powerSet(choices) 
    //println "\t all possibilities=${allPossibilities}"
	def valids = difference(allPossibilities,SET_CONTAINING_EMPTY_SET) //with out empty set
    //println "\t only the valid possibilities (without null-policy)=${valids}"
	def processAssertions = convertToDNF(valids) 
    //println "\t processAssertions=${processAssertions}"
	processPolicy = convertToPolicy(processAssertions)
}
println " \t processPolicy=${processPolicy}"
