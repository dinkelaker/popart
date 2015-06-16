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
package de.tud.stg.popart.aspect.extensions;

import  de.tud.stg.popart.aspect.PopartException;
import  de.tud.stg.popart.aspect.Aspect;
import  de.tud.stg.popart.aspect.PointcutAndAdvice;
import  de.tud.stg.popart.joinpoints.JoinPoint;

public class AspectFaultIsolationException extends PopartException {

	Aspect aspect;
	PointcutAndAdvice failingPA;
	JoinPoint jp;
	
	/**
	 * @param aspect	The aspect that advice throwed an exception.
	 * @param failingPA The pointcut-and-advice that failed.
	 * @param jp		The current join point at which the advice failed.
	 */
	public AspectFaultIsolationException(Aspect aspect, PointcutAndAdvice failingPA, JoinPoint jp) {
		this.aspect = aspect;
		this.failingPA = failingPA;
		this.jp = jp;
	}

	public AspectFaultIsolationException(Aspect aspect, PointcutAndAdvice failingPA, JoinPoint jp, String message) {
		super(message);
		this.aspect = aspect;
		this.failingPA = failingPA;
		this.jp = jp;
	}

	public AspectFaultIsolationException(Aspect aspect, PointcutAndAdvice failingPA, JoinPoint jp, Throwable cause) {
		super(cause);
		this.aspect = aspect;
		this.failingPA = failingPA;
		this.jp = jp;
	}

	public AspectFaultIsolationException(Aspect aspect, PointcutAndAdvice failingPA, JoinPoint jp, String message, Throwable cause) {
		super(message, cause);
		this.aspect = aspect;
		this.failingPA = failingPA;
		this.jp = jp;
	}
	
	public String toString() {
		String str = super.toString();
		str += "["+aspect.getName();
		str += "["+aspect.getPointcutAndAdviceIndex(failingPA)+"]";
		return str;
	}
}
