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
 * Recursive calls currently ignore the Meta Object Protocol.
 * This means, if you have a method like:
 * <code>def fibonacci(n) {(n < 2) ? 1 : fibonacci(n-1) + fibonacci(n-2)}</code>
 * Only the first call is caught by the instrumentation. The recursive calls are not caught.
 * This seems to be a bug in Groovy.
 * @author Jan Stolzenburg
 */
public class TestRecursionAndDefaultParameter extends GroovyTestCase {
	
	def oldClassHistory
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		oldClassHistory = Victim.classHistory.clone()
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations();
	}
	
	void testRecursionWithoutDefaultParameter() {
		InstrumentationRegistry.instrumentMethod(Victim,'putSeven') {context -> context.receiver.instanceHistory.add(6.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putEight') {context -> context.receiver.classHistory.add(7.9); context.proceed()}
		def victim = new Victim()
		victim.instanceRecursionParameter = [[], []]
		victim.putSeven()
		victim.classRecursionParameter = [[], []]
		Victim.putEight()
		assertEquals([0, 6.9, 7, 6.9, 7, 6.9, 7], victim.instanceHistory)
		assertEquals(oldClassHistory + [7.9, 8, 7.9, 8, 7.9, 8], Victim.classHistory)
	}
	
	void testRecursionWithDefaultParameterNoGiven() {
		InstrumentationRegistry.instrumentMethod(Victim,'putNine') {context -> context.receiver.instanceHistory.add(8.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putTen') {context -> context.receiver.classHistory.add(9.9); context.proceed()}
		def victim = new Victim()
		victim.instanceRecursionParameter = [[], []]
		victim.putNine()
		victim.classRecursionParameter = [[], []]
		Victim.putTen()
		assertEquals([0, 8.9, [9, 'a', 'b', 'c'], 8.9, [9, 'a', 'b', 'c'], 8.9, [9, 'a', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [9.9, [10, 'a', 'b', 'c'], 9.9, [10, 'a', 'b', 'c'], 9.9, [10, 'a', 'b', 'c']], Victim.classHistory)
	}
	
	void testRecursionWithDefaultParameterAllGiven() {
		InstrumentationRegistry.instrumentMethod(Victim,'putNine') {context -> context.receiver.instanceHistory.add(8.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putTen') {context -> context.receiver.classHistory.add(9.9); context.proceed()}
		def victim = new Victim()
		victim.instanceRecursionParameter = [['d', 'e', 'f'], ['d', 'e', 'f']]
		victim.putNine('d', 'e', 'f')
		victim.classRecursionParameter = [['d', 'e', 'f'], ['d', 'e', 'f']]
		Victim.putTen('d', 'e', 'f')
		assertEquals([0, 8.9, [9, 'd', 'e', 'f'], 8.9, [9, 'd', 'e', 'f'], 8.9, [9, 'd', 'e', 'f']], victim.instanceHistory)
		assertEquals(oldClassHistory + [9.9, [10, 'd', 'e', 'f'], 9.9, [10, 'd', 'e', 'f'], 9.9, [10, 'd', 'e', 'f']], Victim.classHistory)
	}
	
	void testRecursionWithDefaultParameterOneOfThreeGiven() {
		InstrumentationRegistry.instrumentMethod(Victim,'putNine') {context -> context.receiver.instanceHistory.add(8.9); context.proceed()}
		InstrumentationRegistry.instrumentMethod(Victim,'putTen') {context -> context.receiver.classHistory.add(9.9); context.proceed()}
		def victim = new Victim()
		victim.instanceRecursionParameter = [['g'], ['g']]
		victim.putNine('g')
		victim.classRecursionParameter = [['g'], ['g']]
		Victim.putTen('g')
		assertEquals([0, 8.9, [9, 'g', 'b', 'c'], 8.9, [9, 'g', 'b', 'c'], 8.9, [9, 'g', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [9.9, [10, 'g', 'b', 'c'], 9.9, [10, 'g', 'b', 'c'], 9.9, [10, 'g', 'b', 'c']], Victim.classHistory)
	}
	
}
