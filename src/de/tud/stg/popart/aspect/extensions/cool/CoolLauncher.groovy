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
package de.tud.stg.popart.aspect.extensions.cool;

import java.io.File;

import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectManagerFactory;

/**
 * @author Oliver Rehor
 */
public class CoolLauncher extends DSLauncher {
  public static void installCoolAspectFactory(){
    AspectFactory.setInstance(new CoolAspectFactoryImpl());
  }

  public void loadAndInterpretSource(File dslSrcFile, String applicationClassPath) {
	installCoolAspectFactory();
	super.loadAndInterpretSource(dslSrcFile, applicationClassPath);
  }
		
  public void bindBootstrapKeywords(Script script) {
    script.getBinding().setVariable(
      "coordinator", CoolDSL.getInterpreter());
    script.getBinding().setVariable(
      "per_class_coordinator", CoolDSL.getPerClassInterpreter());
  }
  
}
