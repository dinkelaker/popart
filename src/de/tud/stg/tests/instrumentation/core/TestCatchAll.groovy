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
public class TestCatchAll extends GroovyTestCase {
	
	def oldClassHistory
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		oldClassHistory = Victim.classHistory.clone()
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations()
	}
	
	void testInstanceOnlyCatchAll() {
		InstrumentationRegistry.instrumentClass(Victim) {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(1.01)}
		def victim = new Victim()
		victim.putOne()
		victim.putTwo()
		assertEquals([0, 0.99, 1, 1.01, 0.99, 2, 1.01], victim.instanceHistory)
	}
	
	void testStaticOnlyCatchAll() {
		InstrumentationRegistry.instrumentClass(Victim) {context -> context.receiver.classHistory.add(0.99); context.proceed(); context.receiver.classHistory.add(1.01)}
		Victim.putThree()
		Victim.putFour()
		assertEquals(oldClassHistory + [0.99, 3, 1.01, 0.99, 4, 1.01], Victim.classHistory)
	}
	
	void testInstanceCatchAllAndCatchSpecific() {
		InstrumentationRegistry.instrumentClass(Victim)				{context -> context.receiver.instanceHistory.add(0.97); context.proceed(); context.receiver.instanceHistory.add(1.03)}
		InstrumentationRegistry.instrumentMethod(Victim, 'putOne')	{context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(1.01)}
		InstrumentationRegistry.instrumentClass(Victim)				{context -> context.receiver.instanceHistory.add(0.96); context.proceed(); context.receiver.instanceHistory.add(1.04)}
		InstrumentationRegistry.instrumentMethod(Victim, 'putOne')	{context -> context.receiver.instanceHistory.add(0.98); context.proceed(); context.receiver.instanceHistory.add(1.02)}
		def victim = new Victim()
		victim.putOne()
		victim.putTwo()
		assertEquals([0, 0.96, 0.97, 0.98, 0.99, 1, 1.01, 1.02, 1.03, 1.04, 0.96, 0.97, 2, 1.03, 1.04], victim.instanceHistory)
	}
	
	void testStaticCatchAllAndCatchSpecific() {
		InstrumentationRegistry.instrumentClass(Victim)				{context -> context.receiver.classHistory.add(0.97); context.proceed(); context.receiver.classHistory.add(1.03)}
		InstrumentationRegistry.instrumentMethod(Victim, 'putThree'){context -> context.receiver.classHistory.add(0.99); context.proceed(); context.receiver.classHistory.add(1.01)}
		InstrumentationRegistry.instrumentClass(Victim)				{context -> context.receiver.classHistory.add(0.96); context.proceed(); context.receiver.classHistory.add(1.04)}
		InstrumentationRegistry.instrumentMethod(Victim, 'putThree'){context -> context.receiver.classHistory.add(0.98); context.proceed(); context.receiver.classHistory.add(1.02)}
		Victim.putThree()
		Victim.putFour()
		assertEquals(oldClassHistory + [0.96, 0.97, 0.98, 0.99, 3, 1.01, 1.02, 1.03, 1.04, 0.96, 0.97, 4, 1.03, 1.04], Victim.classHistory)
	}
	
}
