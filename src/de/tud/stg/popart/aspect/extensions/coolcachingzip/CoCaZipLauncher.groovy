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
package de.tud.stg.popart.aspect.extensions.coolcachingzip;

import de.tud.stg.popart.aspect.extensions.cool.CoolLauncher;
import de.tud.stg.popart.aspect.extensions.cool.CoolDSL;
import de.tud.stg.popart.aspect.extensions.caching.CachingDSL;
import de.tud.stg.popart.aspect.extensions.zip.ZipDSL;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.aspect.AspectFactory;

/**
 * @author Oliver Rehor
 */
public class CoCaZipLauncher extends CoolLauncher {
  
  public void bindBootstrapKeywords(Script script) {
    
    // first supported possibility: separate coordinator, cache and zip module in
    // one source file
    script.getBinding().setVariable(
      "coordinator", CoolDSL.getInterpreter());
    script.getBinding().setVariable(
      "per_class_coordinator", CoolDSL.getPerClassInterpreter());
    
    script.getBinding().setVariable(
      "cache", CachingDSL.getInterpreter());
    script.getBinding().setVariable(
      "per_class_cache", CachingDSL.getPerClassInterpreter());
    
    script.getBinding().setVariable(
      "zip", ZipDSL.getInterpreter());
    
    // second supported possibility: coordinator, cache and zip as "all in one"
    // module in one source file
    script.getBinding().setVariable("per_class_zippingcachingcoordinator",
      CoCaZipDSL.getPerClassInterpreter());
    script.getBinding().setVariable("per_class_cocazip",
        CoCaZipDSL.getPerClassInterpreter());

    script.getBinding().setVariable("zippingcachingcoordinator",
      CoCaZipDSL.getInterpreter());
    script.getBinding().setVariable("cocazip",
        CoCaZipDSL.getInterpreter());
    

    // set comparator to resolve conflicts
    AspectFactory.defaultComparator =
      new CoCaZipComparator<AspectMember>();
  }
  
}
