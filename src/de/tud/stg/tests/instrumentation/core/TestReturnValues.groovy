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
 */
public class TestReturnValues extends GroovyTestCase {
	
	def oldClassHistory
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		oldClassHistory = Victim.classHistory.clone()
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations();
	}
	
	void testAdviceThrice() {
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.99); def result = context.proceed(); context.receiver.instanceHistory.add(1.01); return result}
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.98); def result = context.proceed(); context.receiver.instanceHistory.add(1.02); return result}
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.97); def result = context.proceed(); context.receiver.instanceHistory.add(1.03); return result}
		def victim = new Victim()
		assertEquals(1, victim.putOne())
		assertEquals(2, victim.putTwo())
		assertEquals(3, Victim.putThree())
		assertEquals(4, Victim.putFour())
		assertEquals([0, 0.97, 0.98, 0.99, 1, 1.01, 1.02, 1.03, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4], Victim.classHistory)
	}
	
	void testStaticAdviceThrice() {
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.99); def result = context.proceed(); context.receiver.classHistory.add(3.01); return result}
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.98); def result = context.proceed(); context.receiver.classHistory.add(3.02); return result}
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.97); def result = context.proceed(); context.receiver.classHistory.add(3.03); return result}
		def victim = new Victim()
		assertEquals(1, victim.putOne())
		assertEquals(2, victim.putTwo())
		assertEquals(3, Victim.putThree())
		assertEquals(4, Victim.putFour())
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [2.97, 2.98, 2.99, 3, 3.01, 3.02, 3.03, 4], Victim.classHistory)
	}
	
	void testAdviceOnce() {
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.99); def result = context.proceed(); context.receiver.instanceHistory.add(1.01); return result;}
		def victim = new Victim()
		assertEquals(1, victim.putOne())
		assertEquals(2, victim.putTwo())
		assertEquals(3, Victim.putThree())
		assertEquals(4, Victim.putFour())
		assertEquals([0, 0.99, 1, 1.01, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4], Victim.classHistory)
	}
	
	void testStaticAdviceOnce() {
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.99); def result = context.proceed(); context.receiver.classHistory.add(3.01); return result;}
		def victim = new Victim()
		assertEquals(1, victim.putOne())
		assertEquals(2, victim.putTwo())
		assertEquals(3, Victim.putThree())
		assertEquals(4, Victim.putFour())
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [2.99, 3, 3.01, 4], Victim.classHistory)
	}
	
	void testChangingReturnValue() {
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.99); def result = context.proceed(); result = -1; context.receiver.instanceHistory.add(1.01); return result;}
		def victim = new Victim()
		assertEquals(-1, victim.putOne())
		assertEquals([0, 0.99, 1, 1.01], victim.instanceHistory)
	}
	
	void testWithoutProceed() {
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.99); context.receiver.instanceHistory.add(1.01); return -1;}
		def victim = new Victim()
		assertEquals(-1, victim.putOne())
		assertEquals([0, 0.99, 1.01], victim.instanceHistory)
	}
}
