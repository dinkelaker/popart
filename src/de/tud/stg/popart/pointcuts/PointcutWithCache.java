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

import java.util.Collections;
import java.util.Map;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.MethodCallJoinPoint;
import de.tud.stg.popart.joinpoints.MethodExecutionJoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;

/**
 * This extends the normal pointcut to offer caching
 * functionallity for partial evaluation results.
 * @author Joscha Drechsler
 */
public abstract class PointcutWithCache extends Pointcut {
	/**
	 * The cache, associating static point cuts with the partial
	 * evaluations residual pointcut
	 */
	Map<StaticJoinPoint, Pointcut> cache;
	
	/**
	 * constructor
	 * @param name some pretty name
	 */
	public PointcutWithCache(String name) {
		//super("cached_"+name);
		super(name);
		cache = Collections.synchronizedMap(new java.util.HashMap<StaticJoinPoint, Pointcut>());
	}
	
	/**
	 * Overrides the default implementation to first lookup the cache and
	 * match the residual pointcut if one is present, else invokes
	 * {@link #matchUncached(JoinPoint)} which is to be implemented by
	 * child classes.
	 * @param jp the join point.
	 */
	@Override
	public final boolean match(JoinPoint jp) {
		/* Since StaticJoinPoint cannot handle any join points that are
		 * not related to methods (meaning only method call and method
		 * execution can be handled), we don't need to look up the
		 * cache for other join points. In fact, that usually raise an
		 * exception since context[methodName] is usually not set when
		 * dealing with other types of join points.
		 */
		Class<? extends JoinPoint> joinPointClass = jp.getClass();
		if(joinPointClass.equals(MethodCallJoinPoint.class) || joinPointClass.equals(MethodExecutionJoinPoint.class)){
			Class<?> receiverClass = jp.context.get("targetObject").getClass();
			String methodName = (String) jp.context.get("methodName");
			StaticJoinPoint staticJoinPoint = new StaticJoinPoint(receiverClass, methodName, joinPointClass);

			Pointcut residual = cache.get(staticJoinPoint);
			/* if the returned point cut is this, calling match again
			 * would lead to infinite recursion. Since at the same time
			 * that means, there was nothing partially evaluated, we
			 * just default back to the uncached matching.
			 */
			if(residual != this && residual != null) return residual.match(jp);
		}
		return matchUncached(jp);
	}
	
	/**
	 * Returns the partial evaluation result from the cache.
	 * If none is present, it is calculated.
	 * @param staticJoinPoint the static information for partial evaluation
	 */
	@Override
	public final Pointcut partialEval(StaticJoinPoint staticJoinPoint) {
		Pointcut result = cache.get(staticJoinPoint);
		if(result == null){
			result = partialEvalUncached(staticJoinPoint);
			cache.put(staticJoinPoint, result);
		}
		return result;
	}
	
	/**
	 * This method performs the partial evaluation which would have been
	 * done in {@link #partialEval(StaticJoinPoint)} without using the
	 * cache.
	 * @param staticJoinPoint the static information for partial evaluation
	 * @return the partially evaluated pointcut residual
	 */
	public abstract Pointcut partialEvalUncached(StaticJoinPoint staticJoinPoint);
	
	/**
	 * This method performs the match evaluation which would have been
	 * done for {@link #match(JoinPoint)}, if the pointcut was not
	 * evaluated with a cache.
	 * @param jp the join point
	 * @return <code>true</code> iff this pointcut matches.
	 */
	public abstract boolean matchUncached(JoinPoint jp);

}
