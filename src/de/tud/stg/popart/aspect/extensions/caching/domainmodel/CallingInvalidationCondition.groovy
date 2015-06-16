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

/**
 * Domain model layer (M) class representing a calling invalidation
 * condition (i.e. triggered by a method call) used to invalidate
 * cached values of a memoizer.
 * @author Oliver Rehor
 */
public class CallingInvalidationCondition extends InvalidationCondition {
  
  private static final boolean mDebug = true;
  
  public CallingInvalidationCondition(String methodSignature) {
    super(methodSignature);
  }
  
  /**
   * Intercept a method call with the given signature in order to invalidate
   * the memoized values when this specific method is called.
   */
  public void registerAspects() {
    def aspect = Memoizer.aspect;
    def interceptionSignature = mInterceptionSignature;
    AspectManager.getInstance().register(
      aspect(name:"cachingBeforeInvalidationCall") {
        before(is_class(mMemoizer.getCachedClass()) & method_call(mInterceptionSignature)) {
          if (mDebug)
            CachingDSL.debugMsg("CallInvCond", "before " + interceptionSignature);
          invalidateMemoizer(targetObject);
        }
      }
    );
  }
  
  /**
   * Return the type of the invalidation condition and the signature it
   * attaches on.
   * @return Type and intercepted signature of this condition.
   */
  public String toString() {
    return "CallingInvalidationCondition(" + mInterceptionSignature + ");";
  }
}
