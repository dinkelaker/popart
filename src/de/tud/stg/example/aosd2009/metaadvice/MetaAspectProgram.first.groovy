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
package de.tud.stg.example.aosd2009.metaadvice

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.AspectFactory;

import de.tud.stg.example.aosd2009.metaadvice.Main;

Main.main((String[])["test"].toArray());	

System.out.println("Defining Bootstrap Closure");
def aspect = { map, definition ->
  def result = new CCCombiner().eval(map,definition);
//  def advDSL = new MetaAdviceDSL(); 
//  def pcDSL = new MetaPointcutDSL();
//  def result = new CCCombiner(advDSL,pcDSL).eval(map,definition);
  AspectManager.getInstance().register(result);
  return result;
}

aspect(name:"MetaAspect") { 
  around(meta_call("interactionAtJoinPoint.*") & cflow(method_call("baz.*")) ) { 
    LinkedList applicablePAs = args[2];
    def pa1 = applicablePAs.get(1);
    applicablePAs.remove(pa1);
    applicablePAs.add(pa1);
    //println "$thisAspect.name: applicablePAs (after) = ${applicablePAs}";
  }
  around(meta_call("interactionAtJoinPoint.*") & cflow(method_call("bar.*")) ) { 
	LinkedList applicablePAs = args[2];
	def pa0 = applicablePAs.get(0);
	applicablePAs.remove(pa0);
	applicablePAs.add(pa0);
	//println "$thisAspect.name: applicablePAs (after) = ${applicablePAs}";
  }
}

aspect(name:"AspectA") { 
  before(method_call("foo.*")) { 
	println "$thisAspect.name: method: $method ";
  }
}

aspect(name:"AspectB") { 
	  before(method_call("foo.*")) { 
		println "$thisAspect.name: method: $method ";
	  }
	}

aspect(name:"AspectC") { 
	  before(method_call("foo.*")) { 
		println "$thisAspect.name: method: $method ";
	  }
	}

Main.run();	





