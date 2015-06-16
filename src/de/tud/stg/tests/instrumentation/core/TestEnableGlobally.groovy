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

import groovy.lang.GroovySystem;

import groovy.lang.MetaClassRegistry.MetaClassCreationHandle;

import org.codehaus.groovy.runtime.InvokerHelper
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationRegistry
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClass
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationMetaClassCreationHandleimport de.tud.stg.tests.instrumentation.Victim

/**
 * @author Jan Stolzenburg
 */
public class TestEnableGlobally extends GroovyTestCase {
	
	MetaClassCreationHandle oldCreationHandle
	
	void setUp() {
		oldCreationHandle = GroovySystem.getMetaClassRegistry().getMetaClassCreationHandler();
	}
	
	void tearDown() {
		GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(oldCreationHandle);
	}
	
	void testEnableInstrumentationGlobally() {
		GroovySystem.metaClassRegistry.removeMetaClass(Victim)
		assert !Util.isMetaclassInstanceOf(Victim.metaClass, InstrumentationMetaClass)
		assert !Util.isMetaclassInstanceOf(new Victim().class.metaClass, InstrumentationMetaClass)
		assert !Util.isMetaclassInstanceOf(new Victim().metaClass, InstrumentationMetaClass)
		
		def old = InstrumentationRegistry.replaceMetaClassCreationHandle()
		GroovySystem.metaClassRegistry.removeMetaClass(Victim)
		assert Util.isMetaclassInstanceOf(Victim.metaClass, InstrumentationMetaClass)
		assert Util.isMetaclassInstanceOf(new Victim().class.metaClass, InstrumentationMetaClass)
		assert Util.isMetaclassInstanceOf(new Victim().metaClass, InstrumentationMetaClass)
		
		InstrumentationRegistry.replaceMetaClassCreationHandle(old)
		GroovySystem.metaClassRegistry.removeMetaClass(Victim)
		assert !Util.isMetaclassInstanceOf(Victim.metaClass, InstrumentationMetaClass)
		assert !Util.isMetaclassInstanceOf(new Victim().class.metaClass, InstrumentationMetaClass)
		assert !Util.isMetaclassInstanceOf(new Victim().metaClass, InstrumentationMetaClass)
	}
	
	void testEnableAndDisableGlobally() {
		def old = InstrumentationRegistry.replaceMetaClassCreationHandle()()
		assert InstrumentationRegistry.isMetaClassCreationHandleInstalled()
		InstrumentationRegistry.replaceMetaClassCreationHandle(old)
		assert (!InstrumentationRegistry.isMetaClassCreationHandleInstalled())
	}
	
	void testMultipleEnableAndDisableGlobally() {
		def old = InstrumentationRegistry.replaceMetaClassCreationHandle()
		InstrumentationRegistry.replaceMetaClassCreationHandle()
		assert InstrumentationRegistry.isMetaClassCreationHandleInstalled()
		InstrumentationRegistry.replaceMetaClassCreationHandle(old)
		InstrumentationRegistry.replaceMetaClassCreationHandle(old)
		assert InstrumentationRegistry.isMetaClassCreationHandleInstalled()
	}
}