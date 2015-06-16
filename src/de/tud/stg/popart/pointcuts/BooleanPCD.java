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
package de.tud.stg.popart.pointcuts;

import de.tud.stg.popart.joinpoints.JoinPoint;

/**
 * @author Jan Stolzenburg
 */
public class BooleanPCD extends Pointcut {
	
	public final static BooleanPCD ALWAYS = new BooleanPCD("always", true);
	public final static BooleanPCD NEVER = new BooleanPCD("never", false);
	
	public static BooleanPCD fromBoolean(boolean bool) {
		return bool ? ALWAYS : NEVER;
	}
	
	public final boolean matches;
	
	private BooleanPCD(String name, boolean matches) {
		super(name);
		this.matches = matches;
	}
	
	public boolean match(JoinPoint jp) {
		return matches;
	}
}