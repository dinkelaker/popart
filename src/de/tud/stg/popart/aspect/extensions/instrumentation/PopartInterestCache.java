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
import java.util.HashMap;
import java.util.Map;

import de.tud.stg.popart.aspect.AspectChangeListener;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.joinpoints.MethodCallJoinPoint;
import de.tud.stg.popart.joinpoints.MethodExecutionJoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;
/**
 * @author Jan Stolzenburg
 */
public abstract class PopartInterestCache {
	static{
		AspectManager.getInstance().registerAspectChangeListener(new AspectChangeListener() {
			public void aspectsChanged() {
				interestCache.clear();
			}
		});
	}
	
	// ============ POPART OPTIMIZATION METHODS =============
	/**
	 * boolean switch to enable partial pointcut evaluation
	 */
	private static boolean enablePopartOptimization = false;
	
	/**
	 * determines if partial pointcut evaluation is enabled
	 * @return <code>true</code> if it is enabled, <code>false</code> if not
	 */
	public boolean isPopartOptimizationEnabled(){
		return enablePopartOptimization;
	}
		
	/**
	 * sets the popart optimization state to the given value
	 * @param enable the new value
	 */
	public static void setEnablePopartOptimization(boolean enable){
		enablePopartOptimization=enable;
	}
	
	/**
	 * enables popart optimization
	 */
	public static void enablePopartOptimization(){
		setEnablePopartOptimization(true);
	}
	
	/**
	 * disables popart optimization
	 */
	public static void disablePopartOptimization(){
		setEnablePopartOptimization(false);
	}

	/**
	 * Cache that indicates whether Popart is interested or not.
	 * Key: theDelegate.class.canonicalName + '.' + methodName
	 * Value: true means, perhaps interested; false means, not interested.
	 */
	private static Map<String, Boolean> interestCache = new HashMap<String, Boolean>();
	
	public static boolean isPopartInterested(Class<?> receiverClass, String methodName) {
		if(!enablePopartOptimization) return true;
		String cacheKey = receiverClass.getCanonicalName() + "." + methodName;
		if (!interestCache.containsKey(cacheKey)) {
			//check method call interest
			StaticJoinPoint callJoinPoint = new StaticJoinPoint(receiverClass, methodName, MethodCallJoinPoint.class);
			boolean callInterest = AspectManager.getInstance().partialEval(callJoinPoint);
			//check method execution interest
			StaticJoinPoint executionJoinPoint = new StaticJoinPoint(receiverClass, methodName, MethodExecutionJoinPoint.class);
			boolean executionInterest = AspectManager.getInstance().partialEval(executionJoinPoint);
			//store result
			boolean interested = callInterest || executionInterest;
			interestCache.put(cacheKey, interested);
		}
		return interestCache.get(cacheKey);
	}
}