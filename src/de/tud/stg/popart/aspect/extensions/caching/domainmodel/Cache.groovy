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
 * Domain model layer (M) class representing a cache consisting of
 * multiple memoization units.
 * @author Oliver Rehor
 */
public class Cache {
  
  private Class mCachedClass;
  private boolean mIsPerObject = false;
  private List<Memoizer> mMemoizers = [];
  
  public Cache(Class cachedClass, boolean isPerObject) {
    mCachedClass = cachedClass;
    mIsPerObject = isPerObject;
  }
  
  /**
   * Returns whether this cache's memoization policy is per class or per cache
   * @return true, if this is a per class cache; false, if it is per object
   */
  public boolean isPerClass() {
    return !mIsPerObject;
  }
  
  /**
   * Add a memoizer to this cache.
   * @param memoizer the memoizer which should be added.
   */
  public void add(Memoizer memoizer) {
    mMemoizers.add(memoizer);
  }
  
  /**
   * Returns the class this cache manages.
   * @return the class this cache is responsible for.
   */
  public Class getCachedClass() {
    return mCachedClass;
  }
  
  /**
   * Trigger registration of the aspects defined by all memoizers.
   */
  public void registerAspects() {
    mMemoizers.each {
      it.registerAspects();
    }
  }
  
  /**
   * Needed in the per object case to forward an initialization request
   * to each memoizer: there new hashmaps are generated for the given object.
   * @param obj  the object this cache is responsible for
   */
  public void initMemoizer(Object obj) {
    mMemoizers.each {
      it.init(obj);
    }
  }  
}
