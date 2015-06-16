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

import java.util.Collections;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.pointcuts.Pointcut;
import groovy.lang.Closure;

public abstract class PointcutAndAdvice implements AspectMember {
	
	protected Aspect aspect;
	protected Pointcut pc;
	protected Closure advice;
	
	public PointcutAndAdvice(Aspect aspect, Pointcut pc, Closure advice) {
		this.aspect = aspect;
		this.pc = pc;
		this.advice = advice;
	}
	
	public Aspect getAspect() { return aspect; }
	
	public Pointcut getPointcut() { return pc; }
	
	public Closure getAdvice() { return advice; }
	
	public String toString() {
//		String str = super.toString();
//		str += "[aspect="+aspect.getName()+",pc='"+pc+"', advice="+advice+"]";
		String str = "";//super.toString();
		str += aspect.getName()+"["+aspect.getPointcutAndAdviceIndex(this)+"]";
		
		return str;
	}
	
	/**
	 * Used for sorting pointcut-and-advice bindings. 
	 * Assumes that <tt>this</tt> and <tt>other</tt> have the same sub-type.
	 */
	public int compareTo(PointcutAndAdvice otherPA) {
		if (!otherPA.getClass().equals(this.getClass())) throw new RuntimeException("PointcutAndAdvice that are compared must have the same Sub-Type/Point-in-time.");
		return Collections.reverseOrder().compare(this.aspect.priority,otherPA.aspect.priority);
	}
	
}

