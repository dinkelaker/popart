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
package de.tud.stg.popart.aspect;

import de.tud.stg.popart.pointcuts.*;

/**
 * This class defines an MetaPointcutDSL that can be used in order to quantify over join points in the meta-aspect protocol.
 * @author Tom Dinkelaker
 */
public class MetaPointcutDSL extends PointcutDSL {

	private static final boolean DEBUG = false; 
	
	/* Literals */
	
	
	/* Operations */
	
	public Pointcut meta_call(String signaturePattern) {
		return new MetaCallPCD(signaturePattern);
	}
	
	public Pointcut interaction() {
		return new MetaCallPCD("interactionAtJoinPoint.*");
	}
		
}



