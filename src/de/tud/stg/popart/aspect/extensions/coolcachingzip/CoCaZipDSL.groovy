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

import java.util.Formatter;
import java.util.Locale;
import de.tud.stg.popart.dslsupport.*;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.cool.CoolDSL;
import de.tud.stg.popart.aspect.extensions.caching.CachingDSL;
import de.tud.stg.popart.aspect.extensions.zip.ZipDSL;

/**
 * @author Oliver Rehor
 */
public class CoCaZipDSL extends InterpreterCombiner {
	
  private CoolDSL mCoolInstance;
  private CachingDSL mCachingInstance;
  private ZipDSL mZipInstance;

  public CoCaZipDSL(CoolDSL cool, CachingDSL caching, ZipDSL zipping) {
    //super(cool, caching, zipping);
	//workaround for issue http://jira.codehaus.org/browse/GROOVY-4122
    super([cool, caching, zipping].toArray(new DSL[0]));
    mCoolInstance = cool;
    mCachingInstance = caching;
    mZipInstance = zipping;
  }
  
  private static Closure getInterpreter(final boolean isPerClass) {
    return { className, body ->
      CoolDSL coolDSL = new CoolDSL(isPerClass);
      CachingDSL cachingDSL = new CachingDSL(isPerClass);
      ZipDSL zipDSL = new ZipDSL();
    
      DSL coolcachingzip = new CoCaZipDSL(coolDSL, cachingDSL, zipDSL);
      return coolcachingzip.eval(className, body) };
  }
  
  public static Closure getPerClassInterpreter() {
    return getInterpreter(true);
  }

  public static Closure getInterpreter() {
    return getInterpreter(false);
  }

  public Object eval(Class className, Closure definitionBody) {
    mCoolInstance.preEvalActions([className]);
    mCachingInstance.preEvalActions(className);
    mZipInstance.preEvalActions(className);
    
    Object result = super.eval(definitionBody);
    
    mZipInstance.postEvalActions(className);
    mCachingInstance.postEvalActions(className);
    mCoolInstance.postEvalActions([className]);
    
    return result;
  }

  public static void debugMsg(String module, String message) {
    StringBuilder sb = new StringBuilder();
    Formatter fm = new Formatter(sb, Locale.US);
    fm.format("CoCaZip [%02d] %-12s | %s",
    Thread.currentThread().getId(), module, message);
    System.out.println(sb);
  }
}
