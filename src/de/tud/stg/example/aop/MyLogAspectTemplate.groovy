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
package de.tud.stg.example.aop;

import de.tud.stg.example.aosd2010.process.dspcl.ProcessPointcutDSL;
import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.pointcuts.Pointcut;

def aspectTemplateClosure = { pc ->
    assert pc instanceof Pointcut;
	aspect(name:"MyLogAspectTemplate",deployed:false) {
		before (pc) {
			println "ASPECT(${thisAspect.name}): \t before advice"
		}
	}
}

def pcdsl = new ProcessPointcutDSL(); 
def logPointcut1 = pcdsl.service_call("getRate(.)*") & pcdsl.if_pcd { external };
def aspect1 = aspectTemplateClosure.call(logPointcut1);
AspectManager.getInstance().register(aspect1);

def logPointcut2 = pcdsl.service_selection("Banking");
def aspect2 = aspectTemplateClosure.call(logPointcut2);
AspectManager.getInstance().register(aspect2);

//aspect(name:"MyLogAspectTemplateXX",deployed:false) {}
return null