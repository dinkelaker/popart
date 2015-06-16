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
package de.tud.stg.popart.aspect.extensions.itd.structuredesignators;

import java.util.Map;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;
import de.tud.stg.popart.pointcuts.BooleanPCD;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * Abstract superclass for all StructureDescriptors.<br>
 * StructureDescriptors are special pointcuts which evaluate purely
 * on static information.<br>
 * A StructureDescriptor is a pattern, defined over the program structure
 * of packages and classes. The pattern can be evaluated for any class,
 * to determine, if it matches.
 * @author Joscha Drechsler
 */
/*
 * TODO StructureDesignators should match StructureLocations rather than
 * only classes, to unify InterTypeDeclarations and PointcutAndAdvice further.
 * However, it would not be as easy to match JoinPoints once this change is
 * made. But: As stated in the StructureDescriptor source code, a Structure
 * Descriptor is actually an equivalent of a join points static information
 * set. Therefore, once the JoinPoint hierarchy has been reworked and equipped
 * with proper static join point parts, and these join point parts have replaced
 * StructureLocation objects, the StructureDesignators should be rewritten
 * to match static join point parts instead of classes, which automatically
 * covers all occasions where former StructureLocations were used.
 */
public abstract class StructureDesignator extends Pointcut {
	private Map<Class<?>,Boolean> cache;
	
	public StructureDesignator(String name){
		super(name);
		cache = new java.util.HashMap<Class<?>,Boolean>();
	}
	
	@Override
	public final boolean match(JoinPoint jp) {
		return matches(jp.context.get("targetObject").getClass());
	}
	
	/**
	 * performs a match check with cache lookup
	 * @param theClass the class to match against
	 * @return <code>true</code> if the class is matched,
	 * 		<code>false</code> if not.
	 */
	public boolean matchesCached(Class<?> theClass) {
		Boolean result = cache.get(theClass);
		if(result == null){
			return matches(theClass);
		}else{
			return result;
		}
	}

	/**
	 * since a structuredesignator can always completely evaluated by
	 * purely static information. This means, that partialEval always
	 * evaluates to {@link BooleanPCD#ALWAYS} or {@link BooleanPCD#NEVER}
	 * and thus allows for very good optimiziation. The partial evaluation
	 * result will be stored in the designators cache.
	 * @param staticJoinPoint the static joinpoint to evaluate
	 * @return the partially evaluated pointcut
	 */
	@Override
	public Pointcut partialEval(StaticJoinPoint staticJoinPoint) {
		Class<?> theClass = staticJoinPoint.getReceiverClass();
		Boolean cacheValue = cache.get(theClass);
		if(cacheValue != null) return BooleanPCD.fromBoolean(cacheValue);

		boolean result = matches(theClass);
		cache.put(theClass, result);
		return BooleanPCD.fromBoolean(result);
	}
	
	/**
	 * Determine, whether the given object matches the pattern.
	 * @param c the Class
	 * @return <code>true</code> if the object matches,
	 * 		<code>false</code> otherwise.
	 */
	public abstract boolean matches(Class<?> c);
	
	/**
	 * Overrides {@link Pointcut#and(Object)} to return a StructureDesignator
	 * instead of a Pointcut linking two StructureDesignators, when one
	 * StructureDesignator is combined with another. If the other object
	 * is a Pointcut instead of a {@link StructureDesignator}, the
	 * base implementation will be invoked and a Pointcut will be returned.
	 * @param other the second StructureDescriptor 
	 * @return the and-concatenation of <code>this</code> and
	 * 		<code>other</code>
	 */
	public Object and(Object other){
		if(other instanceof StructureDesignator){
			return new AndSD(this,(StructureDesignator) other);
		}else{
			return super.and(other);
		}
	}
	
	/**
	 * Overrides {@link Pointcut#or(Object)} to return a StructureDesignator
	 * instead of a Pointcut linking two StructureDesignators, when one
	 * StructureDesignator is combined with another. If the other object
	 * is a Pointcut instead of a {@link StructureDesignator}, the
	 * base implementation will be invoked and a Pointcut will be returned.
	 * @param other the second StructureDescriptor 
	 * @return the or-concatenation of <code>this</code> and
	 * 		<code>other</code>
	 */
	public Object or(Object other){
		if(other instanceof StructureDesignator){
			return new OrSD(this,(StructureDesignator) other);
		}else{
			return super.or(other);
		}
	}
}
