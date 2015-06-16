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
package de.tud.stg.example.aosd2009.x

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.pointcuts.*;
import de.tud.stg.popart.aspect.extensions.*;

Main.main((String[])[""].toArray());	

System.out.println("Defining Bootstrap Closure");
def aspect = { map, definition ->
  return new CCCombiner().eval(map,definition)
}

//An Aspect Script
Aspect myAspect = aspect(name:"AspectWithBrokenPointcut") { 
  Pointcut pc = method_call("foo.*") & 
	not(cflow(method_call("bar.*"))); 
  after ( pc ) { println "foo() called from outside bar()."; } 
} 
AspectManager.getInstance().register(myAspect)

Main.main((String[])[""].toArray());	

myAspect.metaAspect = new DebugMetaAspect(myAspect.class); 

Main.main((String[])[""].toArray()); //call baz() with debugging support for pointcut evaluation	
