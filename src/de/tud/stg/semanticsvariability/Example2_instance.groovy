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




class Tartle2 {
        
    void forward() {
        System.out.println "fw1"
    }
    
}


println "example #2: we can change the semantics just for a given instance"
def t4 = new Tartle2();
t4.forward();
def mc = new ExpandoMetaClass(Tartle2.class,false);
mc.forward = { -> println "fw4"  }
mc.initialize();
t4.metaClass = mc; 
t4.forward();
println "then even new instances have the default semantics"
def t5 = new Tartle2();
t5.forward();
