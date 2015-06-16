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
 * @author Jan Stolzenburg * 
 */
public class TestParameter extends GroovyTestCase {
	
	def oldClassHistory
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		oldClassHistory = Victim.classHistory.clone()
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations();
	}
	
	void testOneParameter() {
		InstrumentationRegistry.instrumentMethod(Victim,'putEleven') {context -> context.args = context.args.collect {it + 'f'}; def result = context.proceed(); context.args = context.args.collect {it + 'g'}; return result.collect {it + 'j'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putEleven') {context -> context.args = context.args.collect {it + 'e'}; def result = context.proceed(); context.args = context.args.collect {it + 'h'}; return result.collect {it + 'k'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putEleven') {context -> context.args = context.args.collect {it + 'd'}; def result = context.proceed(); context.args = context.args.collect {it + 'i'}; return result.collect {it + 'l'}}
		def victim = new Victim()
		assertEquals(['11jkl', 'A!defjkl'], victim.putEleven('A!'))
		assertEquals([0, [11, 'A!def']], victim.instanceHistory)
	}
	
	void testTwoParameters() {
		InstrumentationRegistry.instrumentMethod(Victim,'putTwelve') {context -> context.args = context.args.collect {it + 'f'}; def result = context.proceed(); context.args = context.args.collect {it + 'g'}; return result.collect {it + 'j'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putTwelve') {context -> context.args = context.args.collect {it + 'e'}; def result = context.proceed(); context.args = context.args.collect {it + 'h'}; return result.collect {it + 'k'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putTwelve') {context -> context.args = context.args.collect {it + 'd'}; def result = context.proceed(); context.args = context.args.collect {it + 'i'}; return result.collect {it + 'l'}}
		def victim = new Victim()
		assertEquals(['12jkl', 'A!defjkl', 'B!defjkl'], victim.putTwelve('A!', 'B!'))
		assertEquals([0, [12, 'A!def', 'B!def']], victim.instanceHistory)
	}
	
	void testThreeParameters() {
		InstrumentationRegistry.instrumentMethod(Victim,'putThirteen') {context -> context.args = context.args.collect {it + 'f'}; def result = context.proceed(); context.args = context.args.collect {it + 'g'}; return result.collect {it + 'j'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putThirteen') {context -> context.args = context.args.collect {it + 'e'}; def result = context.proceed(); context.args = context.args.collect {it + 'h'}; return result.collect {it + 'k'}}
		InstrumentationRegistry.instrumentMethod(Victim,'putThirteen') {context -> context.args = context.args.collect {it + 'd'}; def result = context.proceed(); context.args = context.args.collect {it + 'i'}; return result.collect {it + 'l'}}
		def victim = new Victim()
		assertEquals(['13jkl', 'A!defjkl', 'B!defjkl', 'C!defjkl'], victim.putThirteen('A!', 'B!', 'C!'))
		assertEquals([0, [13, 'A!def', 'B!def', 'C!def']], victim.instanceHistory)
	}
	
}
