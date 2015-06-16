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
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClass
import de.tud.stg.tests.instrumentation.Victim

/**
 * @author Jan Stolzenburg
 * @see de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClass.invokeMethodWithCatchingDefaultParameterRedirect
 */
public class TestDefaultParameter extends GroovyTestCase {
	
	def oldClassHistory
	
	boolean previousRedirectBehavior;
	
	void setUp() {
		previousRedirectBehavior = InstrumentationMetaClass.defaultEnableInstrumentationForDefaultParameterRedirectCalls;
		InstrumentationMetaClass.defaultEnableInstrumentationForDefaultParameterRedirectCalls = false;
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		oldClassHistory = Victim.classHistory.clone()
	}
	
	void tearDown() {
		InstrumentationMetaClass.defaultEnableInstrumentationForDefaultParameterRedirectCalls = previousRedirectBehavior;
		InstrumentationRegistry.resetAllInstrumentations();
	}
	
	void testNoParameterGiven() {
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(4.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(5.9); context.proceed()}
		def victim = new Victim()
		victim.putFive()
		Victim.putSix()
		assertEquals([0, 4.9, [5, 'a', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [5.9, [6, 'a', 'b', 'c']], Victim.classHistory)
	}
	
	/**
	 * Check if this doesn't interfere with the recursion-detection code.
	 */
	void testNoParameterGivenGivenTwoCalls() {
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(4.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(5.9); context.proceed()}
		def victim = new Victim()
		victim.putFive()
		victim.putFive()
		Victim.putSix()
		Victim.putSix()
		assertEquals([0, 4.9, [5, 'a', 'b', 'c'], 4.9, [5, 'a', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [5.9, [6, 'a', 'b', 'c'], 5.9, [6, 'a', 'b', 'c']], Victim.classHistory)
	}
	
	void testAllThreeParameterGiven() {
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(4.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(5.9); context.proceed()}
		def victim = new Victim()
		victim.putFive('d', 'e', 'f')
		Victim.putSix('d', 'e', 'f')
		assertEquals([0, 4.9, [5, 'd', 'e', 'f']], victim.instanceHistory)
		assertEquals(oldClassHistory + [5.9, [6, 'd', 'e', 'f']], Victim.classHistory)
	}
	
	void testAllThreeParameterGivenTwoCalls() {
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(4.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(5.9); context.proceed()}
		def victim = new Victim()
		victim.putFive('d', 'e', 'f')
		victim.putFive('d', 'e', 'f')
		Victim.putSix('d', 'e', 'f')
		Victim.putSix('d', 'e', 'f')
		assertEquals([0, 4.9, [5, 'd', 'e', 'f'], 4.9, [5, 'd', 'e', 'f']], victim.instanceHistory)
		assertEquals(oldClassHistory + [5.9, [6, 'd', 'e', 'f'], 5.9, [6, 'd', 'e', 'f']], Victim.classHistory)
	}
	
	void testOneOfThreeParameterGiven() {
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(4.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(5.9); context.proceed()}
		def victim = new Victim()
		victim.putFive('g')
		Victim.putSix('g')
		assertEquals([0, 4.9, [5, 'g', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [5.9, [6, 'g', 'b', 'c']], Victim.classHistory)
	}
	
	void testOneOfThreeParameterGivenTwoCalls() {
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(4.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(5.9); context.proceed()}
		def victim = new Victim()
		victim.putFive('g')
		victim.putFive('g')
		Victim.putSix('g')
		Victim.putSix('g')
		assertEquals([0, 4.9, [5, 'g', 'b', 'c'], 4.9, [5, 'g', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [5.9, [6, 'g', 'b', 'c'], 5.9, [6, 'g', 'b', 'c']], Victim.classHistory)
	}
}