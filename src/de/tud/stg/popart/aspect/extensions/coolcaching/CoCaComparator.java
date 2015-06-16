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
package de.tud.stg.popart.aspect.extensions.coolcaching;

import java.util.Formatter;
import java.util.Locale;

import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.extensions.comparators.ApplicationSpecificComparator;

/**
 * @author Oliver Rehor
 */
public class CoCaComparator<T extends AspectMember> extends ApplicationSpecificComparator<T> {

  private final boolean mDebug = true;
  
  public int compare(T to1, T to2) {
    // Java comparator behaviour:
    // to1 > to2: returns +1
    // to1 = to2: returns  0
    // to1 < to2: returns -1
    String o1Name = to1.getAspect().getName();
    String o2Name = to2.getAspect().getName();
    
    if (mDebug)
      debugMsg("CoCaComparator", "compare '" + o1Name + "':'" + o2Name + "'");
    
    if (o1Name.startsWith("cachingBeforeInvalidation") && 
        o2Name.startsWith("cool"))
      return 1; // seems to be different to standard java comparator!
    else if (o1Name.startsWith("cool") && o2Name.startsWith("cool"))
      return 0;
    else
      return -1;
  }
    
  public static void debugMsg(String module, String message) {
    StringBuilder sb = new StringBuilder();
    Formatter fm = new Formatter(sb, Locale.US);
    fm.format("CoCa    [%02d] %-12s | %s",
    Thread.currentThread().getId(), module, message);
    System.out.println(sb);
  }
    
}
