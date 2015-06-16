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

import de.tud.stg.popart.aspect.extensions.cool.CoolLauncher;
import de.tud.stg.popart.aspect.extensions.cool.CoolDSL;
import de.tud.stg.popart.aspect.extensions.caching.CachingDSL;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.aspect.AspectFactory;

/**
 * @author Oliver Rehor
 */
public class CoCaLauncher extends CoolLauncher {
  
  public void bindBootstrapKeywords(Script script) {
    
    // first supported possibility: separate coordinator and cache
    script.getBinding().setVariable(
      "coordinator", CoolDSL.getInterpreter());
    script.getBinding().setVariable(
      "per_class_coordinator", CoolDSL.getPerClassInterpreter());
    
    script.getBinding().setVariable(
      "cache", CachingDSL.getInterpreter());
    script.getBinding().setVariable(
      "per_class_cache", CachingDSL.getPerClassInterpreter());
    
    // second supported possibility: coordinator and cache all in one
    script.getBinding().setVariable("per_class_cachingcoordinator",
      CoCaDSL.getPerClassMixtureInterpreter());
    
    script.getBinding().setVariable("cachingcoordinator",
      CoCaDSL.getMixtureInterpreter());
    
    // set comparator to resolve conflicts
    AspectFactory.defaultComparator =
      new CoCaComparator<AspectMember>();
  }
  
}
