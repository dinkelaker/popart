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

/**
 * Defining Bootstrap Closure for AOP keywords.
 * POPART's default syntax is extended with a DSAL for the meta-aspect protocol (MAP).
 */  
def aspect = { map, definition ->
  /**
   * Provides declarative primitives to control the behavior of the meta-level.
   * The current implementation supports primitives to control co-advising interactions.
   */
  def advDSL = new MetaAdviceDSL();  
  
  /**
   * Provides declarative primitives to quantify over join points in the meta-level.
   * The current implementation supports only one join point type and PCD: interaction(), 
   * that is fired in the interactionAtJoinPoint meta-method in the MAP.
   */
  def pcDSL = new MetaPointcutDSL(); //Provides declarative primitives to quantify over join point in the meta-aspect protocol execution
  
  def result = new CCCombiner(advDSL,pcDSL).eval(map,definition);
  AspectManager.getInstance().register(result);
  return result;
}

/** 
 * Define an aspect that advises the behavioral meta-level entities. 
 * There is a co-advising interaction at method call foo(),
 * because AspectA, AspectB, and AspectC advise the same join point. 
 * The interaction is resolved using various strategies 
 * Each strategy is implemented in an advice of the aspect that advises the MAP.
 * The selection of the correct resolution strategy is implemented depending on 
 * dynamic control flow the application is in.
 */
aspect(name:"MetaAspect") {
  /** Interactions are resolved by excluding all pointcut-and-advice of Aspect A. */ 
  before(interaction() & cflow(method_call("baz.*")) ) { 
    LinkedList applicablePAs = args[2];
    println "$thisAspect.name: default ordering = ${applicablePAs}";
    exclude("AspectA");
    println "$thisAspect.name: default ordering (after) = ${applicablePAs}";
  }
	
  /** Interactions are resolved by ensuring that pointcut-and-advice of AspectC are executed before pointcut-and-advice of AspectB. */ 
  before(interaction() & cflow(method_call("bar.*")) ) { 
    LinkedList applicablePAs = args[2];
    println "$thisAspect.name: applicablePAs (before) = ${applicablePAs}";
    moveToBefore("AspectC","AspectB");
    println "$thisAspect.name: alternative ordering A = ${applicablePAs} (in cflow of bar)";
  }

  /** Interactions are resolved by ensuring that pointcut-and-advice of AspectA are executed after pointcut-and-advice of AspectC. */ 
  before(interaction() & cflow(method_call("qux.*")) ) { 
    LinkedList applicablePAs = args[2];
    moveToAfter("AspectA","AspectC");
    println "$thisAspect.name: alternative ordering B = ${applicablePAs} (in cflow of qux)";
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