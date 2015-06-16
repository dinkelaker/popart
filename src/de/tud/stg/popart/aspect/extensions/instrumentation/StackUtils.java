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
package de.tud.stg.popart.aspect.extensions.instrumentation;

/**
 * @author Jan Stolzenburg
 */
public class StackUtils {

	/**
	 * Has the last method on the stack called an automatically generated method,
	 * inserted by the Groovy compiler to realize default params?
	 * Attention: This operation is EXTREMELY costly in programs with high recursion
	 * since the complete stack for all threads is dumped by the JVM on each invocation.
	 * Therefore do anything possible to not need it ;)
	 */
	public static boolean isThisCallDefaultParameterRedirect(Class<?> theClass, String methodName) {
		StackTraceElement lastCall = findLastCallOfClass(theClass);
		return isThatMethod(lastCall, methodName) && indicatesDefaultParameterRedirect(lastCall);
	}

	private static boolean indicatesDefaultParameterRedirect(StackTraceElement stackTraceElement) {
		//-1 stands for "No line number available".
		//This indicates that its a synthetic method.
		//I use this as indicator for a "default parameter redirect".
		//I have no idea how reliable this is.
		return stackTraceElement.getLineNumber() == -1;
	}

	/**
	 * Last method on stack has the same name?
	 */
	private static boolean isThatMethod(StackTraceElement stackTraceElement, String methodName) {
		if(stackTraceElement == null) return false;
		return stackTraceElement.getMethodName() == methodName;
	}

	private static StackTraceElement findLastCallOfClass(Class<?> theClass) {
		for(StackTraceElement it : Thread.currentThread().getStackTrace()){
			if(it.getClassName().equals(theClass.getCanonicalName())) return it;
		}
		return null;
	}

}
