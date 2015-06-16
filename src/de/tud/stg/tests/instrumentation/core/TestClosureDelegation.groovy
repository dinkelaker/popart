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
package de.tud.stg.tests.instrumentation.core

import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClassCreationHandle;
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationRegistry
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClassimport de.tud.stg.tests.instrumentation.Victim


/**
 * @author Jan Stolzenburg
 */
public class TestClosureDelegation extends GroovyTestCase {
	
	def oldClassHistory
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		oldClassHistory = Victim.classHistory.clone()
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations()
	}
	
	void testClosureDelegates() {
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(1.01)}
		def victim = new Victim()
		victim.putOne()
		victim.putTwo()
		Victim.putThree()
		Victim.putFour()
		assertEquals([0, 0.99, 1, 1.01, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4], Victim.classHistory)

		def closure = {->
			putOne()
			putTwo()
			putThree()
			putFour()
		}
		closure.delegate = victim
		closure()
		assertEquals([0, 0.99, 1, 1.01, 2, 0.99, 1, 1.01, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, 3, 4], Victim.classHistory)
	}
}
