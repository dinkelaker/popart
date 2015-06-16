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

import de.tud.stg.popart.pointcuts.Pointcut;
import de.tud.stg.popart.joinpoints.JoinPoint;

import java.util.List;
import java.util.Set;

/**
 * @author Tom Dinkelaker
 */
public interface MetaAspectProtocol {
    
	public void receiveBefore(Aspect aspect, JoinPoint jp, List<PointcutAndAdvice> applicablePAs);	

	public void receiveAround(Aspect aspect, JoinPoint jp, List<PointcutAndAdvice> applicablePAs);

	public void receiveAfter(Aspect aspect, JoinPoint jp, List<PointcutAndAdvice> applicablePAs);
	
	
	public boolean matchPointcut(Aspect aspect, JoinPoint jp, Pointcut pc);
	
	public void matchedPointcut(Aspect aspect, JoinPoint jp, PointcutAndAdvice pa);
	
	public void notMatchedPointcut(Aspect aspect, JoinPoint jp, PointcutAndAdvice pa);

	public void beforeCallingAdvice(Aspect aspect, JoinPoint jp, PointcutAndAdvice pa);
	
	public void interactionAtJoinPoint(Aspect aspect, JoinPoint jp, Set<Aspect> aspectInterferenceSet, List<PointcutAndAdvice> applicablePAs);

	public void afterCallingAdvice(Aspect aspect, JoinPoint jp, PointcutAndAdvice pa);	
}

