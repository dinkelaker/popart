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

import org.apache.log4j.Logger;
import static org.apache.log4j.Priority.DEBUG;

/**
 * @author Jan Stolzenburg
 */
public class TestInheritance extends GroovyTestCase {
	
	private static Logger logger;
	
	static {
		logger = Logger.getLogger("de.tud.stg.tests");
	}
	
	void setUp() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(Victim)
	}
	
	void tearDown() {
		InstrumentationRegistry.resetAllInstrumentations();
	}
	
	void testMetaClassInherited() {
		assert Util.isMetaclassInstanceOf(Victim.metaClass, InstrumentationMetaClass)
		logger.log(DEBUG, "test log4j");
		assert Util.isMetaclassInstanceOf(ChildVictim.metaClass, InstrumentationMetaClass)
	}
	
	void testInstanceMethodsInherited() {
		InstrumentationRegistry.instrumentMethod(Victim,'putOne') {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(1.01)}
		def childVictim = new ChildVictim()
		assert (childVictim instanceof ChildVictim)
		childVictim.putOne()
		assertEquals([0, 0.99, 1, 1.01], childVictim.instanceHistory)
	}
	
	void testNewInstanceMethodsWithInheritedAdvice() {
		InstrumentationRegistry.instrumentClass(Victim) {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(1.01)}
		def childVictim = new ChildVictim()
		assert (childVictim instanceof ChildVictim)
		childVictim.putOne()
		childVictim.childPutOne()
		childVictim.putTwo()
		assertEquals([0, 0.99, 1, 1.01, 0.99, 1.1111, 1.01, 0.99, 2.2222, 1.01], childVictim.instanceHistory)
	}
	void testAbstractInstanceMethods() {
		GroovySystem.metaClassRegistry.removeMetaClass(Victim)
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(AbstractVictim)
		InstrumentationRegistry.instrumentMethod(AbstractVictim,'abstractPutOne') {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(1.01)}
		def childVictim = new ConcreteChildVictim()
		childVictim.abstractPutOne()
		assertEquals([0, 0.99, 1.1111, 1.01], childVictim.instanceHistory)
	}
	void testPrivateMethod() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(VictimWithPrivateMethod)
		assert Util.isMetaclassInstanceOf(VictimWithPrivateMethod.metaClass, InstrumentationMetaClass)
		
		InstrumentationRegistry.instrumentMethod(VictimWithPrivateMethod,'putTwoThreeFour') {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(3.01)}
		def victim = new VictimWithPrivateMethod()
		victim.putTwoThreeFour()
		assertEquals([0, 0.99, 1, 2, 3, 3.01], victim.instanceHistory)
		
		assert Util.isMetaclassInstanceOf(VictimWithPrivateMethod.metaClass, InstrumentationMetaClass)
	}
	void testUninstrumentedParent() {
		InstrumentationMetaClassCreationHandle.decorateDefaultMetaClassForInstrumentation(InstrumentedChild)
		assert Util.isMetaclassInstanceOf(InstrumentedChild.metaClass, InstrumentationMetaClass)
		
		InstrumentationRegistry.instrumentMethod(InstrumentedChild,'putOneTillFive') {context -> context.receiver.instanceHistory.add(0.99); context.proceed(); context.receiver.instanceHistory.add(5.01)}
		def child = new InstrumentedChild()
		child.putOneTillFive()
		assertEquals([0, 0.99, 1, 2, 3, 4, 5, 5.01], child.instanceHistory)
		
		assert Util.isMetaclassInstanceOf(InstrumentedChild.metaClass, InstrumentationMetaClass)
	}
}

public class ChildVictim extends Victim {
	public childPutOne() {
		instanceHistory.add(1.1111)
		1.1111
	}
	public putTwo() {
		instanceHistory.add(2.2222)
		2.2222
	}
}
public abstract class AbstractVictim {
	def instanceHistory = []
	public AbstractVictim() {
		instanceHistory.add(0)
	}
	public abstract abstractPutOne();
}
public class ConcreteChildVictim extends AbstractVictim {
	public abstractPutOne() {
		instanceHistory.add(1.1111)
		1.1111
	}
}
public class VictimWithPrivateMethod {
	def instanceHistory = []
	public VictimWithPrivateMethod() {
		instanceHistory.add(0)
	}
	public putTwoThreeFour() {
		instanceHistory.add(1)
		putPrivateThree()
		instanceHistory.add(3)
	}
	private putPrivateThree() {
		instanceHistory.add(2)
	}
}
public class UninstrumentedParent {
	def instanceHistory = []
	public UninstrumentedParent() {
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
public class InstrumentedChild extends UninstrumentedParent {
	public putOneTillFive() {
		instanceHistory.add(1)
		putTwoThreeFour()
		instanceHistory.add(5)
	}
}