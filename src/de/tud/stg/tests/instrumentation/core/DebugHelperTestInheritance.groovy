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
public class UninstrumentedParentDebug {
	def instanceHistory = []
	public UninstrumentedParentDebug() {
		instanceHistory.add(0)
	}
	public putTwoThreeFour() {
		instanceHistory.add(2)
		putPrivateThree()
		instanceHistory.add(4)
	}
	private putPrivateThree() {
		instanceHistory.add(3)
	}
}
public class InstrumentedChildDebug extends UninstrumentedParentDebug {
	public putOneTillFive() {
		instanceHistory.add(1)
		putTwoThreeFour()
		instanceHistory.add(5)
	}
}
GroovySystem.metaClassRegistry.removeMetaClass(UninstrumentedParentDebug)
assert !Util.isMetaclassInstanceOf(UninstrumentedParentDebug.metaClass, InstrumentationMetaClass)
InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(InstrumentedChildDebug)
assert Util.isMetaclassInstanceOf(InstrumentedChildDebug.metaClass, InstrumentationMetaClass)
assert !Util.isMetaclassInstanceOf(UninstrumentedParentDebug.metaClass, InstrumentationMetaClass)

InstrumentationRegistry.instrumentMethod(InstrumentedChildDebug, 'putOneTillFive') {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(5.01)}
def child = new InstrumentedChildDebug()
child.putOneTillFive()
assert([0, 0.99, 1, 2, 3, 4, 5, 5.01] == child.instanceHistory)

assert Util.isMetaclassInstanceOf(InstrumentedChildDebug.metaClass, InstrumentationMetaClass)
assert !Util.isMetaclassInstanceOf(UninstrumentedParentDebug.metaClass, InstrumentationMetaClass)
