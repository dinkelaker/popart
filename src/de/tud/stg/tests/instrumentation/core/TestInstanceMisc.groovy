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
public class TestInstanceMisc extends GroovyTestCase {
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations();
	}
	
	void testResetInstrumentationMetaClass() {
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.991); context.proceed(); context.receiver.instanceHistory.add(1.011)}
		def victim = new Victim()
		victim.putOne()
		InstrumentationRegistry.resetAllInstrumentationsFor(Victim)
		victim.putOne()
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.992); context.proceed(); context.receiver.instanceHistory.add(1.012)}
		victim.putOne()
		assertEquals([0, 0.991, 1, 1.011, 1, 0.992, 1, 1.012], victim.instanceHistory)
	}
	
	/*
	 * Enable Globally makes this test superfluous: Every class is instrumented as soon as it is loaded.
	 * And: Calling GroovySystem.metaClassRegistry.removeMetaClass(Victim) just reenables the
	 * Instrumentation for this class, as the default metaclass is restored.
	 * But this is InstrumentationMetaClass.
	void testMetaclassChanceAffectsOnlyNewInstances() {
		InstrumentationRegistry.disableInstrumentationGlobally()
		GroovySystem.metaClassRegistry.removeMetaClass(Victim)
		def firstVictim = new Victim()
		firstVictim.putOne()
		assertEquals([0, 1], firstVictim.instanceHistory)
		InstrumentationRegistry.enableInstrumentationFor(Victim)
		def secondVictim = new Victim()
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(1.01)}
		def thirdVictim = new Victim()
		firstVictim.putOne()
		assertEquals([0, 1, 1], firstVictim.instanceHistory)
		secondVictim.putOne()
		assertEquals([0, 1, 1], firstVictim.instanceHistory)
		assertEquals([0, 0.99, 1, 1.01], secondVictim.instanceHistory)
		thirdVictim.putOne()
		assertEquals([0, 1, 1], firstVictim.instanceHistory)
		assertEquals([0, 0.99, 1, 1.01], secondVictim.instanceHistory)
		assertEquals([0, 0.99, 1, 1.01], thirdVictim.instanceHistory)
	}
	*/
	
	void testInstanceFailOnMissingMethods() {
		InstrumentationRegistry.instrumentMethod(Victim,'iDontExist') {context -> context.receiver.instanceHistory.add(42); context.proceed()}
		def victim = new Victim()
		shouldFail(MissingMethodException) {victim.iDontExist()}
		assertEquals([0], victim.instanceHistory)
	}
	
}
