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
package de.tud.stg.example.aosd2009;

import de.tud.stg.example.interpreter.ProcessDSL;
import de.tud.stg.example.application.MyServiceProxy;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.extensions.DebugMetaAspect;
import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.pointcuts.*;
import de.tud.stg.popart.joinpoints.*;

import org.codehaus.groovy.reflection.CachedMethod;
import java.lang.reflect.Method;

ExpandoMetaClass.enableGlobally();

MyServiceProxy.init();

def p = 

new ProcessDSL().eval (name:"Shipment") {

	def serviceList;
	
	sequence ({

		task (name:"getCarriers") {
		    println "PROCESS: \t Get carriers from registry."
		    serviceList = registry.find("Category1");
	    }

		task (name:"getRates") {
		    println "PROCESS: \t Get rates from carriers."
		    serviceList.each { service -> 
		        println "PROCESS: \t\t Calling service at "+service.endPoint
		        def result = service.call( "getRate", [1,2] ) 
		        println "PROCESS: \t\t result=$result from service at "+service.endPoint
		    }
	    }
	})
	
}

println "\n1. run"
p.execute()

println "\n2. run (without debugging)"
def debugThisAspect = AspectManager.getInstance().getAspect("MyDynAspect")
debugThisAspect.deploy();
p.execute()

println "\n2. run (with debugging)"
debugThisAspect.metaAspect = new DebugMetaAspect(debugThisAspect.class);
p.execute()


