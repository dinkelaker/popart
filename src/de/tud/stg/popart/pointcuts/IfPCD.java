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

import groovy.lang.Closure;
import de.tud.stg.popart.joinpoints.JoinPoint;

public class IfPCD extends Pointcut {
	
	private Closure ifClosure;
	
	public IfPCD(Closure ifClosure) {
		super("if("+ifClosure+")");
		this.ifClosure = ifClosure;
	}
	
	public boolean match(JoinPoint jp) {
		Closure clone = (Closure) ifClosure.clone();
		clone.setResolveStrategy(Closure.DELEGATE_FIRST);
		clone.setDelegate(jp.context);
		Object result = clone.call();
		if(result == null){
			return false;
		}else if(Boolean.class.isAssignableFrom(result.getClass())){
			return (Boolean)result;
		}else{
			throw new RuntimeException("The closure of if-poincut "+this+" must return boolean results!");
		}
	}
}
