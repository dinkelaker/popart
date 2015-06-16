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

import java.util.Iterator;

/**
 * This is the Context parameter object passed to every closure
 * in a invoked instrumentation chain.
 */
public class InstrumentationContextParameter {
	/**
	 * the original method calls receiver object
	 */
	private Object receiver;
	/**
	 * the originally invoked methods name
	 */
	private String methodName;
	/**
	 * the originally invocations arguments
	 */
	public Object[] args;
	/**
	 * the proceed chains iterator
	 */
	private Iterator<Proceed> proceedIterator;
	/**
	 * the final proceed call returning into the base code execution
	 */
	private Proceed finalProceed;
	
	/**
	 * Default constructor.
	 * @param receiver the method invocations target object
	 * @param methodName the invoked methods name
	 * @param args the invoked 
	 * @param proceedIterator the proceed chains iterator
	 * @param finalProceed the final proceed
	 */
	InstrumentationContextParameter(Object receiver, String methodName, Object[] args, Iterator<Proceed> proceedIterator, Proceed finalProceed) {
		this.receiver = receiver;
		this.methodName = methodName;
		this.args = args;
		this.proceedIterator = proceedIterator;
		this.finalProceed = finalProceed;
	}
	
	/**
	 * Getter for receiver object
	 * @return the original method calls receiver object
	 */
	public Object getReceiver() {
		return receiver;
	}
	
	/**
	 * Getter for method name
	 * @return the originally called methods name
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * Getter for arguments
	 * @return the original calls arguments
	 */
	public Object[] getArgs() {
		return args;
	}
	
	/**
	 * Setter for arguments
	 * @param args the new set of arguments
	 */
	public void setArgs(Object[] args){
		this.args = args;
	}

	/**
	 * The proceed() call. This will invoke the proceed chains
	 * next element and return its result. Note, that the actual
	 * method invocations result will be stored in this context
	 * object (see {@link #getResult()}, {@link #setResult(Object)}
	 * and {@link #result}}. A proceeds return value is not defined
	 * by any contract in this implementation.
	 * @return the next proceed chain calls returned value
	 */
	public Object proceed(){
		if(proceedIterator.hasNext()){
			return proceedIterator.next().proceed(this);
		} else {
			return finalProceed.proceed(this);
		}
	}
}
