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
package de.tud.stg.popart.aspect.extensions.cool.domainmodel;

/**
 * @author Oliver Rehor
 */
public class MethodState {
  private int mDepth = 0;
  private Vector<Thread> mT = [];
  
  public boolean isActiveInOtherThread() {
    if (mDepth > 0 && !mT.contains(Thread.currentThread()))
      return true;
    else
      return false;
  }
  
  public void enter() {
    mDepth++;
    mT.addElement(Thread.currentThread());
  }
  
  public void leave() {
    mT.removeElement(Thread.currentThread());
    mDepth--;
  }
  
  public int getDepth() {
    return mDepth;
  }
}
