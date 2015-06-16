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
public class TestStaticMethods extends GroovyTestCase {
	
	def oldClassHistory
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		oldClassHistory = Victim.classHistory.clone()
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations();
	}
	
	void testWithoutProceed() {
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.99); context.receiver.classHistory.add(3.01); return}
		def victim = new Victim()
		victim.putOne()
		victim.putTwo()
		Victim.putThree()
		Victim.putFour()
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [2.99, 3.01, 4], Victim.classHistory)
		//Just comparing the last to elements is not enough.
		//We have to make sure, only these two elements were added and not more.
	}
	
	void testAdviceOnce() {
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.99); context.proceed(); context.receiver.classHistory.add(3.01)}
		def victim = new Victim()
		victim.putOne()
		victim.putTwo()
		Victim.putThree()
		Victim.putFour()
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [2.99, 3, 3.01, 4], Victim.classHistory)
		//Just comparing the last to elements is not enough.
		//We have to make sure, only these two elements were added and not more.
	}
	
	void testAdviceTwice() {
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.99); context.proceed(); context.receiver.classHistory.add(3.01)}
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.98); context.proceed(); context.receiver.classHistory.add(3.02)}
		def victim = new Victim()
		victim.putOne()
		victim.putTwo()
		Victim.putThree()
		Victim.putFour()
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [2.98, 2.99, 3, 3.01, 3.02, 4], Victim.classHistory)
	}
	
	void testAdviceThrice() {
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.99); context.proceed(); context.receiver.classHistory.add(3.01)}
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.98); context.proceed(); context.receiver.classHistory.add(3.02)}
		InstrumentationRegistry.instrumentMethod(Victim,'putThree') {context -> context.receiver.classHistory.add(2.97); context.proceed(); context.receiver.classHistory.add(3.03)}
		def victim = new Victim()
		victim.putOne()
		victim.putTwo()
		Victim.putThree()
		Victim.putFour()
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [2.97, 2.98, 2.99, 3, 3.01, 3.02, 3.03, 4], Victim.classHistory)
	}
	
}
