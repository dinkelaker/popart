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

import groovy.lang.Closure;
import groovy.lang.MetaClass;

/**
 * This class adapts a Closure to the Proceed interface.
 * @author Joscha Drechsler
 */
public class ClosureProceed implements Proceed {
	/**
	 * The closure to be invoked as a proceed
	 */
	private Closure closure;
	
	/**
	 * Constructor. The closure must be applicable for a
	 * {@link InstrumentationContextParameter} object, and
	 * may or may not invoke the next proceed on the context.
	 * @param closure the adapted closure
	 */
	public ClosureProceed(Closure closure){
		MetaClass mc = closure.getMetaClass();
		Class<?>[] argsArray = new Class<?>[1];
		argsArray[0] = InstrumentationContextParameter.class;
		if(mc.pickMethod("call", argsArray) == null){
			throw new RuntimeException("The Closure "+closure+" is not applicable for an InstrumentationContextParameter object as argument and thus cannot be instrumented.");
		}
		this.closure = closure;		
	}
	
	public Object proceed(InstrumentationContextParameter context) {
		return closure.call(context);
	}
}
