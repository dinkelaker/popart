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

aspect(name:"Change",deployed:false) {
	before (service_call("getRate(.)*") & if_pcd { external }) {
		println "ASPECT(Change): \t before advice in $application"
		println "ASPECT(Change): \t args={$args}"
		println "ASPECT(Change): \t jp=$thisJoinPoint"
		println "ASPECT(Change): \t location=$thisJoinPoint.location"
		args[0]=3
	}

	after (service_call("getRate(.)*") & if_pcd { external }) {
		println "ASPECT(Change): \t after advice"
		println "ASPECT(Change): \t result={$result}"
		newVar="MyNewString";
	}

}

