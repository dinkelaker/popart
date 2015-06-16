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
package de.tud.stg.popart.dslsupport.sql.dsjpm

import de.tud.stg.popart.aspect.extensions.instrumentation.JoinPointInstrumentation;

class SelectJoinPointInstrumentation extends JoinPointInstrumentation {

	public SelectJoinPointInstrumentation() {
		super();
	}
	
	@Override
	protected void prolog() {
		joinPointContext = new HashMap()
		joinPoint = new SelectJoinPoint("pselect", joinPointContext)
		joinPoint.query = instrumentationContext.args[0]
		joinPointContext.thisJoinPoint = joinPoint
	}
	
	@Override
	protected void prologForAround() {
		joinPointContext.proceed = {
			instrumentationContext.args = [joinPointContext.query];
			instrumentationContext.proceed()
		};
	}
}
