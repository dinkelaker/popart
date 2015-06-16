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

Closure aspectTemplateClosure = { list ->
  assert list instanceof ArrayList
  aspect(name:"MyEnumAspectTemplate",deployed:false) {
    list.each{ entry ->
	  //println "define service_call advice $entry"
	  before (service_call("getRate(.)*") & if_pcd { service.name.equals(entry) } )  { 
		  println "ASPECT(${thisAspect.name}): \t before advice $entry"
	  }
    }
  }
} 

List services = ["BankOfTheWeb","BankOfTomorrow","TransEuropean"]
myAspect = aspectTemplateClosure.call(services);
return myAspect



