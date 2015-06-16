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

import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.joinpoints.JoinPoint;

public interface IAspectManager {
	public Aspect getAspect(String name);
	public void register(Aspect aspect);
	public void unregister(Aspect aspect);
	
	//private Set calculateAspectInterferenceSet(List applicablePAs);
	//private void reportInterferenceToAllConcerningAspects(JoinPoint jp, Set aspectInterferenceSet, List applicablePAs);
	//private void executeAllApplicablePointcutAndAdvice(JoinPoint jp, List applicablePAs);

	public void fireJoinPointBeforeToAspects(JoinPoint jp);
	public void fireJoinPointAroundToAspects(JoinPoint jp);		
	public void fireJoinPointAfterToAspects(JoinPoint jp);		

	public void invokeAllApplicablePointcutAndAdvice(JoinPoint jp, List<PointcutAndAdvice> applicablePAs);
	public Object invokeAdvice(JoinPoint jp, PointcutAndAdvice pa);
	public void interactionAtJoinPoint(JoinPoint jp, Set<Aspect> aspects, List<PointcutAndAdvice> applicablePAs);
}
