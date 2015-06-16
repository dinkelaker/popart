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
package de.tud.stg.tests.popart.extensions.coolcaching;

import java.util.Vector;

public class GuardedCacheVictim {
  
  public int resetTrigger = 0;
  private Vector<String> trace;
  private int id = 0;
  
  public GuardedCacheVictim(Vector<String> trace) {
    this.trace = trace;
  }
  
  public long doingSth(int id) throws Exception {
    this.id = id;
    System.out.println(">> Inside GuardedCacheVictim.doingSth(" + id + ")");
    final long tmp = internal();
    if (id == 20)
      resetTrigger = -1;
    return tmp;
  }
  
  public long internal() throws Exception {
    Thread.currentThread().sleep(2000);
    trace.add("internal:" + id + ":" + resetTrigger);
    return System.currentTimeMillis();
  }
  
  public String toString() {
    return "GuardedCacheVictim";
  }
}
