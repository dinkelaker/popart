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
public class TestParameterConversion extends GroovyTestCase {
	
	void testStringParameter() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(VictimForParameterConversion)
		def flag = false
		InstrumentationRegistry.instrumentMethod(VictimForParameterConversion, 'expectString') {context -> flag = true; context.proceed()}
		def victim = new VictimForParameterConversion()
		assertEquals('expectedString: Hallo 2', victim.expectString("Hallo ${1+1}"))
		assert flag
	}
}

class VictimForParameterConversion {
	public String expectString(String string) {
		return ('expectedString: ' + string)
	}
}