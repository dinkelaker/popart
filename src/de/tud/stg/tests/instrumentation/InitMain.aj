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
package de.tud.stg.tests.instrumentation;

import de.tud.stg.example.interpreter.metamodel.Process;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.Booter;
import junit.framework.TestSuite;



/**
 * @author Tom Dinkelaker 
 */
public aspect InitMain {
	
	private final boolean DEBUG = false;

	pointcut inExamples() : (
			within(de.tud.stg.tests..*) && 
			!within(de.tud.stg.tests.instrumentation..*) &&
		    !within(de.tud.stg.tests.dslsupport..*) &&
		    !within(de.tud.stg.tests.interactions.aspectj..*) &&
		    !within(de.tud.stg.tests.interactions.popart.itd..*));
	
	/** @todo Consider Scheduling */ 
	before() : (execution(public static void *.main(..)) || execution(public static void *.init(..))) && inExamples() {
		Booter.initialize();
	}

    before() : (execution(public static * TestSuite+.suite(..))) && inExamples() {
		Booter.initialize();
	}
    
}
