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
package de.tud.stg.popart.aspect.extensions.caching;

import java.util.Formatter;
import java.lang.StringBuilder;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.caching.domainmodel.*;

/**
 * Interpreter layer (I) class that handles interpretation of Caching EDSL
 * programs utilizing Groovy's Meta Object Protocol.
 * @author Oliver Rehor
 */
public class CachingDSL implements ICachingDSL {

  public final static boolean mDebug = true;
  
  private boolean mIsInMemoizeBlock = false;
  
  private Cache mCache;
  private Memoizer mCurrentMemoizer;
  private boolean mIsPerClass = false;
  
  private Object mTheDelegate = this;

  // constructors
  public CachingDSL() {
    mIsPerClass = false;
  }
  
  public CachingDSL(boolean isPerClass) {
    mIsPerClass = isPerClass;
  }

  public void setDelegate(Object delegate) {
    mTheDelegate = delegate;
  }
  
  /**
   * Return a closure that manages the actual call to a Caching interpreter.
   * This closure can be bound to a bootstrap keyword.
   * @return a closure with an interpreter call
   */
  public static Closure getInterpreter() {
    return { params, body ->
             ICachingDSL cachingInterpreter = new CachingDSL();
             return cachingInterpreter.eval(params, body) };
  }

  /**
   * Return a closure that manages the actual call to a per class
   * Caching interpreter.
   * This closure can be bound to a bootstrap keyword.
   * @return a closure with an interpreter call
   */
  public static Closure getPerClassInterpreter() {
    return { params, body ->
             ICachingDSL cachingInterpreter = new CachingDSL(true);
             return cachingInterpreter.eval(params, body) };
  }
  
  /**
   * Main entry point of the Caching interpreter. This method creates a
   * {@link Cache} object representing the cache this method
   * is called for. Keywords found in this cache's body are mapped
   * to method names of {@link CachingDSL}.
   * @param className  class the current cache is responsible for
   * @param cacheBody  the body of the coordinator containing Caching
   *                   statements/language constructs
   */
  public void eval(Class className, Closure cacheBody) {
    preEvalActions(className);
    
    evalClosure(cacheBody);
    
    postEvalActions(className);
  }
  
  public void preEvalActions(Class className) {
    mCache = new Cache(className, !mIsPerClass);
  }
   
  public void postEvalActions(Class className) {
    if (!mIsPerClass) {
      def aspect = { map, definition ->
        return new CCCombiner().eval(map,definition)
      }
      String clazzName = className.getName();
      AspectManager.getInstance().register(
        aspect(name:"cachingAfterObjectConstruction") {
          after(initialization(clazzName)) {
            if (mDebug)
              debugMsg("CachingDSL", "intercepted constructor call of class '" +\
                clazzName + "' for targetObject '" +\
                targetObject + ":" + targetObject.hashCode());
            mCache.initMemoizer(targetObject);
          }});
    }
    mCache.registerAspects();
  }
  
  /**
   * Keyword method to define a memoized method.
   * @param  methodName  the method name which should be memoized
   * @param  definition  the memoize definition closure containing
   *                     invalidation conditions.
   * */
  public void memoize(String methodName, Closure definition) {
    if (mIsInMemoizeBlock)
      throw new Exception("Error: Another memoize found inside a memoize block.");
    mIsInMemoizeBlock = true;
    mCurrentMemoizer = new Memoizer(mCache, methodName);
    evalClosure(definition);
    mCache.add(mCurrentMemoizer);
    mIsInMemoizeBlock = false;
  }
  
  public void invalidated_by_calling(String methodName) {
    mCurrentMemoizer.addInvalidationCondition(
        new CallingInvalidationCondition(methodName));
  }
  
  public void or_calling(String methodName) {
    mCurrentMemoizer.addInvalidationCondition(
        new CallingInvalidationCondition(methodName));
  }
  
  public void invalidated_by_assigning(String fieldName) {
    mCurrentMemoizer.addInvalidationCondition(
        new AssigningInvalidationCondition(fieldName));
  }
  
  public void or_assigning(String fieldName) {
    mCurrentMemoizer.addInvalidationCondition(
        new AssigningInvalidationCondition(fieldName));
  }
  
  /**
   * Keyword method to define an assignment invalidation condition.
   * @param fieldName the name of the field triggering cache invalidation when
   *                  assigned
   * */
  public void untilAssigning(String fieldName) {
    if (!mIsInMemoizeBlock)
      throw new Exception("Error: untilAssigning Statement may only occur" +\
        "within a memoize block.");
    mCurrentMemoizer.addInvalidationCondition(
      new AssigningInvalidationCondition(fieldName));
  }
  
  /**
   * Keyword method to define a calling invalidation condition.
   * @param methodName the name of the method triggering cache invalidation when
   *                   called
   * */
  public void untilCalling(String methodName) {
    if (!mIsInMemoizeBlock)
      throw new Exception("Error: untilCalling Statement may only occur" +\
        "within a memoize block.");
    mCurrentMemoizer.addInvalidationCondition(
      new CallingInvalidationCondition(methodName));
  }
  
  /**
   * Evaluates a closure and delegates its (unknown) keywords to this class.
   * @param cl the closure to evaluate
   */
  private void evalClosure(Closure cl) {
    cl.delegate = mTheDelegate;
    cl.resolveStrategy = Closure.DELEGATE_FIRST;
    cl.call();
  }
  
  public static void debugMsg(String module, String message) {
    StringBuilder sb = new StringBuilder();
    Formatter fm = new Formatter(sb, Locale.US);
    fm.format("Caching [%02d] %-12s | %s",
      Thread.currentThread().getId(), module, message);
    System.out.println(sb);
  }
}
