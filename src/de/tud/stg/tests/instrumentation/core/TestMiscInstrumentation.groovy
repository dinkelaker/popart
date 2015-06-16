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
public class TestMiscInstrumentation extends GroovyTestCase {
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations();
	}
	
	void testEnableInstrumentation() {
		GroovySystem.metaClassRegistry.removeMetaClass(Victim)
		assert !Util.isMetaclassInstanceOf(Victim.metaClass, InstrumentationMetaClass)
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		assert Util.isMetaclassInstanceOf(Victim.metaClass, InstrumentationMetaClass)
	}
	
	void testMetaClassOfClassPassedOnToInstances() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		assert Util.isMetaclassInstanceOf(Victim.metaClass, InstrumentationMetaClass)
		assert Util.isMetaclassInstanceOf(new Victim().class.metaClass, InstrumentationMetaClass)
		assert Util.isMetaclassInstanceOf(new Victim().metaClass, InstrumentationMetaClass)
	}
	
	void testNoIndividualInstancesOfMetaClasses() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
		assert Util.isMetaclassInstanceOf(Victim.metaClass, InstrumentationMetaClass)
		def victimMetaClass = new Victim().metaClass
//		assert Util.isMetaclassInstanceOf(victimMetaClass, Victim.metaClass.class)
		assert (victimMetaClass == Victim.metaClass)
	}
	
}
