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
package de.tud.stg.semanticsvariability

import org.codehaus.groovy.runtime.InvokerHelper;
import groovy.lang.ExpandoMetaClass;





def t2 = new Tartle4();
t2.forward();

println "solution 1"
println "it works at the instance level"
println "the DSL interpreter class is impacted to create Tartle with TartleNewSemantics"
println "instead of Tartle"
println ""
println "solution 2"
// this does not work
// you have to use the registry
//Tartle.metaClass = new  TartleNewSemantics() 

def reg = InvokerHelper.metaRegistry
reg.setMetaClass(Tartle, new  TartleNewSemantics4() );
def t3 = new Tartle4();
t3.forward();

