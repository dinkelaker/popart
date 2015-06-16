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
 * This interface defines a proceed-element
 * @author Joscha Drechsler
 */
public interface Proceed {
	/**
	 * the proceed call. Every proceed call must a result, which may
	 * either be the subsequent proceed calls result, or a modification
	 * of that.
	 * @param context the context which lead to this proceeds invocation
	 * @return the return value
	 */
	public Object proceed(InstrumentationContextParameter context);
}
