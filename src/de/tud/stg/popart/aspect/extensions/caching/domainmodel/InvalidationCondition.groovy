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

/**
 * Domain model layer (M) class representing an abstract invalidation
 * condition used to invalidate cached values of a memoizer.
 * @author Oliver Rehor
 */
public abstract class InvalidationCondition {
  
  protected String mInterceptionSignature;
  protected Memoizer mMemoizer;
  
  public InvalidationCondition(String interceptionSignature) {
    mInterceptionSignature = interceptionSignature;
  }
  
  /**
   * Sets the memoizer this invalidation condition will cause to reset
   * its cache.
   * @param memoizer  the memoizer this condition is tied to
   */
  public void setMemoizer(Memoizer memoizer) {
    mMemoizer = memoizer;
  }
  
  /**
   * Specific invalidation conditions must implement this method in order
   * to register aspects intercepting the invalidation trigger (like a method
   * call).
   */
  public abstract void registerAspects();

  /**
   * Return the type of the invalidation condition and the signature it
   * attaches on.
   * @return Type and intercepted signature of this condition.
   */
  public String toString() {
    return "InvalidationCondition(" + mInterceptionSignature + ");";
  }
  
  /**
   * Notify the memoizer to invalidate its cache generally or for the
   * given object (if in per object caching) only.
   * @param obj  the object to invalidate the memoizer for (if per object)
   */
  protected void invalidateMemoizer(Object obj) {
    mMemoizer.invalidate(obj);
  }
}
