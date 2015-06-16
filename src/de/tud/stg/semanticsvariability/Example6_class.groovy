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




class Tartle6 {
        
    void forward() {
        System.out.println "fw1"
    }
    
    void right() {
        System.out.println "rt1"
    }
    
}




println "example #6: we modify two methods of the Turtle class in a closure"
def t2 = new Tartle6();
t2.forward();
t2.right();

Closure semanticMixin = { aClass ->
   aClass.metaClass.forward = {-> println "fw3"  }
   aClass.metaClass.right = {-> println "rt3"  }
}

semanticMixin(Tartle6);

t2.forward();
t2.right();
println "but existing instances keep the old semantics"
def t3 = new Tartle6();
t3.forward();
t3.right();
