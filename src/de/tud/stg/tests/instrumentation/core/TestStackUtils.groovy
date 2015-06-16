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

import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationRegistry
import de.tud.stg.popart.aspect.extensions.instrumentation.StackUtils


/**
 * @author Jan Stolzenburg
 */
public class TestStackUtils extends GroovyTestCase {
	
	void testFindLastCallOfClass() {
		def stackTraceElement = StackUtils.findLastCallOfClass(this.class)
		assertEquals(stackTraceElement.className, this.class.canonicalName)
		assertEquals(stackTraceElement.methodName, 'testFindLastCallOfClass')
		stackTraceElement = StackUtils.findLastCallOfClass(this.class)
		assertEquals(stackTraceElement.className, this.class.canonicalName)
		assertEquals(stackTraceElement.methodName, 'testFindLastCallOfClass')
		stackTraceElement = StackUtils.findLastCallOfClass(new Object().class)
		assertEquals(stackTraceElement, null)
		stackTraceElement = StackUtils.findLastCallOfClass(Object)
		assertEquals(stackTraceElement, null)
		
		def stackTraceElements = []
		stackTraceElements << utilCallClosure {-> utilCallClosure(this) {foo -> StackUtils.findLastCallOfClass(foo.class)}}
		stackTraceElements << utilCallClosure(this) {foo -> utilCallClosure {-> StackUtils.findLastCallOfClass(foo.class)}}
		stackTraceElements << utilCallClosure {-> utilCallClosure(this.class) {foo -> StackUtils.findLastCallOfClass(foo)}}
		stackTraceElements << utilCallClosure(this.class) {foo -> utilCallClosure {-> StackUtils.findLastCallOfClass(foo)}}
		stackTraceElements << utilCallClosure {-> utilCallClosure(new Object()) {foo -> StackUtils.findLastCallOfClass(foo.class)}}
		stackTraceElements << utilCallClosure(new Object()) {foo -> utilCallClosure {-> StackUtils.findLastCallOfClass(foo.class)}}
		stackTraceElements << utilCallClosure {-> utilCallClosure(Object) {foo -> StackUtils.findLastCallOfClass(foo)}}
		stackTraceElements << utilCallClosure(Object) {foo -> utilCallClosure {-> StackUtils.findLastCallOfClass(foo)}}
		assertEquals(stackTraceElements[0].className, this.class.canonicalName)
		assertEquals(stackTraceElements[0].methodName, 'utilCallClosure')
		assertEquals(stackTraceElements[1].className, this.class.canonicalName)
		assertEquals(stackTraceElements[1].methodName, 'utilCallClosure')
		//I don't want to test concrete line numbers, as they change if I enter new tests or write comments.
		assert (stackTraceElements[0].lineNumber > stackTraceElements[1].lineNumber)
		assertEquals(stackTraceElements[2].className, this.class.canonicalName)
		assertEquals(stackTraceElements[2].methodName, 'utilCallClosure')
		assertEquals(stackTraceElements[3].className, this.class.canonicalName)
		assertEquals(stackTraceElements[3].methodName, 'utilCallClosure')
		assert (stackTraceElements[2].lineNumber > stackTraceElements[3].lineNumber)
		assertEquals(stackTraceElements[0].lineNumber, stackTraceElements[2].lineNumber)
		assertEquals(stackTraceElements[1].lineNumber, stackTraceElements[3].lineNumber)
		assertEquals(stackTraceElements[4], null)
		assertEquals(stackTraceElements[5], null)
		assertEquals(stackTraceElements[6], null)
		assertEquals(stackTraceElements[7], null)
	}
	
	def utilCallClosure(closure) {
		closure()
	}
	
	def utilCallClosure(args, closure) {
		closure.call(args)
	}
}
