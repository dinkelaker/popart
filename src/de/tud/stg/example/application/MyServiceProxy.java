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
package de.tud.stg.example.application;

import java.util.ArrayList;
import java.util.HashMap;

import de.tud.stg.example.interpreter.metamodel.Registry;
import de.tud.stg.example.interpreter.metamodel.ServiceProxy;
import de.tud.stg.example.interpreter.metamodel.Process;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.Constants;

/**
 * @author Tom Dinkelaker
 */
public class MyServiceProxy extends ServiceProxy {
	
	public static void init() {
		Registry.getInstance().register(new MyServiceProxy());
	}
	
	public MyServiceProxy() {
		super("exampeService","http://myserver:3000/myservice","Category1");
	}
	
	public Object call(String operation, ArrayList args) {
		System.out.println("MY_SERVICE: \t call op="+operation+" args="+args);
			
		if ("getRate".equals(operation)) {
			int param0 = ((Integer)args.get(0)).intValue();
			int param1 = ((Integer)args.get(1)).intValue();
			int result = 2 * param0 + param1;
			System.out.println("MY_SERVICE: \t call result="+result);
			return new Integer(result);
		}
		
		return null;
	}

}
