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
package de.tud.stg.tests.interactions.popart.precedence;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.AspectMember;


/**
 * @author Olga Gusyeva
 */

def location = "de.tud.stg.tests.interactions.popart.precedence.TestObject"


Booter.initialize();
AspectFactory.defaultComparator = new PrecedenceComparator();
AspectFactory.defaultMediator = new RelationDefinerFacade();

ArrayList<Integer> expectedResults;
ArrayList<Integer> actualResults;
actualResults = new ArrayList<Integer>();
expectedResults = new ArrayList<Integer>();
TestObject testObject = new TestObject();

AspectGenerator generator = new AspectGenerator();
//AspectGenerator generator = new AspectGenerator();

int numberOfAspects = 3;
String aspectBasicName = "Aspect";



ArrayList<String> precedenceList = ["a2", "a1", "a3"];
generator.generateAspects(3,"a", testObject, precedenceList);
//generator.generateAspects(3, "a", testObject);

testObject.testMethodBefore();
actualResults = testObject.results
//expectedResults = [2, 1, 3, 0]
expectedResults = [2, 1, 3, 0]
println "MEDIATOR:"
println (AspectFactory.defaultMediator.toString());
println "Expected results: $expectedResults"
println "Actual results: $actualResults"

System.out.println("-------");


