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

import de.tud.stg.popart.dslsupport.*;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.cool.CoolDSL;
import de.tud.stg.popart.aspect.extensions.caching.CachingDSL;

/**
 * @author Oliver Rehor
 **/
public class CoCaDSL extends InterpreterCombiner {
	
  private DSL mCoolInstance;
  private DSL mCachingInstance;

  public CoCaDSL(DSL cool, DSL caching) {
	//super(cool,caching);
	//workaround for issue http://jira.codehaus.org/browse/GROOVY-4122
    super([cool, caching].toArray(new DSL[0]));
    mCoolInstance = cool;
    mCachingInstance = caching;
  }
  
  private static Closure getInterpreter(final boolean isPerClass) {
    return { className, body ->
      DSL coolDSL = new CoolDSL(isPerClass);
      DSL cachingDSL = new CachingDSL(isPerClass);
    
      DSL coolcaching = new CoCaDSL(coolDSL, cachingDSL);
      return coolcaching.eval(className, body) };
  }
  
  public static Closure getPerClassMixtureInterpreter() {
    return getInterpreter(true);
  }
  
  public static Closure getMixtureInterpreter() {
    return getInterpreter(false);
  }
  
  public Object eval(Class className, Closure definitionBody) {
    mCoolInstance.preEvalActions([className]);
    mCachingInstance.preEvalActions(className);
    
    Object result = super.eval(definitionBody);
    
    mCachingInstance.postEvalActions(className);
    mCoolInstance.postEvalActions([className]);
    
    return result;
  }
  
}
