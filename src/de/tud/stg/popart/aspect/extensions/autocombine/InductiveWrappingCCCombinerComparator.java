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
package de.tud.stg.popart.aspect.extensions.autocombine;

import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.extensions.comparators.ApplicationSpecificComparator;

import java.util.List;
import java.util.HashMap;

public class InductiveWrappingCCCombinerComparator<T extends AspectMember> extends ApplicationSpecificComparator<T> {

  private final boolean mDebug = true;
  
  private HashMap<String,Long> mBeforeAdvicePriorities = new HashMap<String,Long>();
  private HashMap<String,Long> mAroundAdvicePriorities = new HashMap<String,Long>();
  private HashMap<String,Long> mAfterAdvicePriorities = new HashMap<String,Long>();
  
  public InductiveWrappingCCCombinerComparator(List<AutoComposable> dslDefinitions) {
    super();
    generatePriorityMaps(dslDefinitions);
  }
  
  private void generatePriorityMaps(List<AutoComposable> dslDefinitions) {
    int priority=1;
    for (AutoComposable aDSL : dslDefinitions) {
      List<String> beforeAspectNames = aDSL.getBeforeAspectNames();
      List<String> aroundAspectNames = aDSL.getAroundAspectNames();
      List<String> afterAspectNames = aDSL.getAfterAspectNames();
      for (String b : beforeAspectNames)
        mBeforeAdvicePriorities.put(b, new Long(priority));
      for (String A : beforeAspectNames)
        mAroundAdvicePriorities.put(A, new Long(priority));
      for (String a : beforeAspectNames)
        mAfterAdvicePriorities.put(a, new Long(priority));
      priority++;
    }
  }
  
  public int compare(T to1, T to2) {
    String o1Name = to1.getAspect().getName();
    String o2Name = to2.getAspect().getName();
    
    if (mBeforeAdvicePriorities.containsKey(o1Name) && mBeforeAdvicePriorities.containsKey(o2Name))
      if (mBeforeAdvicePriorities.get(o1Name) < mBeforeAdvicePriorities.get(o2Name)) // o1 has higher priority (lower index)
        return -1;
      else if (mBeforeAdvicePriorities.get(o1Name) > mBeforeAdvicePriorities.get(o2Name)) // o2 has higher priority (lower index)
        return 1;

    if (mAroundAdvicePriorities.containsKey(o1Name) && mAroundAdvicePriorities.containsKey(o2Name))
      if (mAroundAdvicePriorities.get(o1Name) < mAroundAdvicePriorities.get(o2Name)) // o1 has higher priority (lower index)
        return -1;
      else if (mAroundAdvicePriorities.get(o1Name) > mAroundAdvicePriorities.get(o2Name)) // o2 has higher priority (lower index)
        return 1;
      
    if (mAfterAdvicePriorities.containsKey(o1Name) && mAfterAdvicePriorities.containsKey(o2Name))
      if (mAfterAdvicePriorities.get(o1Name) < mAfterAdvicePriorities.get(o2Name)) // o1 has lower index, but is 'after', so lower priority!
        return 1;
      else if (mAfterAdvicePriorities.get(o1Name) > mAfterAdvicePriorities.get(o2Name)) // o2 has lower index, but is 'after', so lower priority!
        return -1;
      
    return 0;
  }
  
}
