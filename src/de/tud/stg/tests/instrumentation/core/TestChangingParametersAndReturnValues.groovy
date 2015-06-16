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
public class TestChangingParametersAndReturnValues extends GroovyTestCase {
	
	def oldClassHistory
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		oldClassHistory = Victim.classHistory.clone()
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations()
	}
	
	void testChangeTheParameter() {
		InstrumentationRegistry.instrumentMethod(Victim,'putEleven') {context -> context.receiver.instanceHistory.add(0.99); context.args = context.args.collect {it + 'f'}; def result = context.proceed(); context.receiver.instanceHistory.add(1.01); result[1] = result[1] +  'g'; return result}
		InstrumentationRegistry.instrumentMethod(Victim,'putEleven') {context -> context.receiver.instanceHistory.add(0.98); context.args = context.args.collect {it + 'e'}; def result = context.proceed(); context.receiver.instanceHistory.add(1.02); result[1] = result[1] +  'h'; return result}
		InstrumentationRegistry.instrumentMethod(Victim,'putEleven') {context -> context.receiver.instanceHistory.add(0.97); context.args = context.args.collect {it + 'd'}; def result = context.proceed(); context.receiver.instanceHistory.add(1.03); result[1] = result[1] +  'i'; return result}
		def victim = new Victim()
		assertEquals([11, '!defghi'], victim.putEleven('!'))
		assertEquals([0, 0.97, 0.98, 0.99, [11, '!def'], 1.01, 1.02, 1.03], victim.instanceHistory)
	}
	
	void testInstanceChangeNumberOfParameters() {
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(0.99); context.args = context.args.reverse(); def result = context.proceed(); context.args = context.args.collect {it + 'XXX'}; context.receiver.instanceHistory.add(1.01); return result.collect {it + '1'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(0.98); context.args = [*context.args, 'z']; def result = context.proceed(); context.args = context.args.collect {it + 'YYY'}; context.receiver.instanceHistory.add(1.02); return result.collect {it + '2'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putFive') {context -> context.receiver.instanceHistory.add(0.97); context.args = [*context.args, 'y']; def result = context.proceed(); context.args = context.args.collect {it + 'ZZZ'}; context.receiver.instanceHistory.add(1.03); return result.collect {it + '3'}}
		def victim = new Victim()
		assertEquals(['5123', 'z123', 'y123', 'x123'], victim.putFive('x'))
		assertEquals([0, 0.97, 0.98, 0.99, [5, 'z', 'y', 'x'], 1.01, 1.02, 1.03], victim.instanceHistory)
	}
	
	void testStaticChangeNumberOfParameters() {
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(0.99); context.args = context.args.reverse(); def result = context.proceed(); context.args = context.args.collect {it + 'XXX'}; context.receiver.classHistory.add(1.01); return result.collect {it + '1'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(0.98); context.args = [*context.args, 'z']; def result = context.proceed(); context.args = context.args.collect {it + 'YYY'}; context.receiver.classHistory.add(1.02); return result.collect {it + '2'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putSix') {context -> context.receiver.classHistory.add(0.97); context.args = [*context.args, 'y']; def result = context.proceed(); context.args = context.args.collect {it + 'ZZZ'}; context.receiver.classHistory.add(1.03); return result.collect {it + '3'}}
		assertEquals(['6123', 'z123', 'y123', 'x123'], Victim.putSix('x'))
		assertEquals(oldClassHistory + [0.97, 0.98, 0.99, [6, 'z', 'y', 'x'], 1.01, 1.02, 1.03], Victim.classHistory)
	}
	
}
