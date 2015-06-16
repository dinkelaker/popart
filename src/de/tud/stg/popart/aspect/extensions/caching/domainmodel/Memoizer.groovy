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
package de.tud.stg.popart.aspect.extensions.caching.domainmodel;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.caching.CachingDSL;
import de.tud.stg.popart.aspect.extensions.itd.StructuralPointcutDSL;

/**
 * Domain model layer (M) class representing a memoization unit.
 * @author Oliver Rehor
 */
public class Memoizer {
  
  private static final boolean mDebug = true;

  public static def aspect = { map, definition ->
    return new CCCombiner(new StructuralPointcutDSL()).eval(map,definition)
  }
  
  private String mMethodName;
  private List<InvalidationCondition> mInvalidationConditions = [];
  private Cache mCache;
  
  //TODO: Checke Sortierung
  private HashMap<List<Object>,Object> mCachedValues = [:];
  private HashMap<Object, HashMap<List<Object>,Object>> mPerObjectValues = [:];
  
  public Memoizer() {
  }
    
  public Memoizer(Cache cache, String methodName) {
    this();
    mCache = cache;
    mMethodName = methodName;
  }

  /**
   * Set the memoizer for a given invalidation condition to this memoizer and
   * add the condition to the list of conditions within here.
   * @param invalidationCondition the invalidation condition that should be added
   */
  public void addInvalidationCondition(InvalidationCondition
    invalidationCondition) {
    
    invalidationCondition.setMemoizer(this);
    mInvalidationConditions.add(invalidationCondition);
  }
  
  /**
   * Return the class cached by the cache.
   * @return the cached class
   */
  public Class getCachedClass() {
    return mCache.getCachedClass();
  }
  
  /**
   * Tells the memoizer to invalidate the cached values.
   * @param obj  the object the invalidation belongs to, if in a per_object cache
   */
  public void invalidate(Object obj) {
    if (Memoizer.mDebug)
      CachingDSL.debugMsg("Memoizer", "invalidating cache");
    if (mCache.isPerClass())
      mCachedValues = [:];
    else
      mPerObjectValues[obj] = [:];
  }
  
  /**
   * Determines if there is a cached value for some given method arguments.
   * @param args  the method parameters of the memoized method
   * @param obj   if we have a per_object cache, this parameter specifies
   *              the object caching is done for
   */
  private boolean needsUpdateFor(Object obj, List<Object> args) {
    if (mCache.isPerClass())
      return !mCachedValues.containsKey(args);
    else
      return !mPerObjectValues[obj].containsKey(args);
  }
  
  /**
   * Cache a value for given args and, if in per_object case, a given object.
   * @param obj  if in per_object case, this is the instance caching is done for
   * @param args the memoized-method parameters, who serve as key
   * @param value  the value that should be saved for specific arguments
   */
  private void cacheArgsAndValue(Object obj, List<Object> args, Object value) {
    if (mCache.isPerClass())
      mCachedValues.put(args, value);
    else
      mPerObjectValues[obj].put(args, value);
  }

  /**
   * Returns the cached value for given arguments and, if in per_object case,
   * for a given object.
   * @return the cached value
   */
  private Object cachedValue(Object obj, List<Object> callArgs) {
    if (mCache.isPerClass())
      return mCachedValues.get(callArgs);
    else
      return mPerObjectValues[obj].get(callArgs);
  }
  
  /**
   * Creates a new per_object cache representation for a given object.
   * @param obj  the object the cache representation is created for
   */
  public void init(Object obj) {
    mPerObjectValues[obj] = new HashMap<List<Object>,Object>();
  }
  
  /**
   * Registers aspects to intercept method calls of the memoized method
   * and also triggers registration of invalidation conditions.
   * If a cached value is found for given arguments and, if in per_object
   * case, for agiven object, the method is skipped and its cached value
   * returned. Otherwise it is executed and gets its result stored in the cache.
   */
  public void registerAspects() {
    def methodName = mMethodName;
    def cachedValues = mCachedValues;
    // 1. register "before mMethodName" interception
    Aspect asp = aspect(name:"cachingAroundMemoizedMethod") {
      around(is_class(mCache.getCachedClass()) & method_call(methodName)) {
        if (Memoizer.mDebug)
          CachingDSL.debugMsg("Memoizer", "around '" + methodName + args + "'");
        if (needsUpdateFor(targetObject, args)) {
          if (Memoizer.mDebug)
            CachingDSL.debugMsg("Memoizer", "no cached value found, run '" + methodName + args + "'");
          Object callResult = proceed();
          if (Memoizer.mDebug)
            CachingDSL.debugMsg("Memoizer", "caching '" + methodName + args + "' = " + callResult);
          // TODO: Achtung, callResult und args sind mutable!
          cacheArgsAndValue(targetObject, args, callResult);
          return callResult;
        }
        else {
          if (Memoizer.mDebug)
            CachingDSL.debugMsg("Memoizer", "taking cached value for '" +\
              methodName + args + "' = " + cachedValue(targetObject, args));
          return cachedValue(targetObject, args);
        }
      }
    }
    AspectManager.getInstance().register(asp);
    // 2. register all mInvalidationConditions
    mInvalidationConditions.each {
      if (Memoizer.mDebug)
        CachingDSL.debugMsg("Memoizer", "register '" + it);
      it.registerAspects();
    }
  }
}
