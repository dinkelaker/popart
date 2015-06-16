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
package de.tud.stg.popart.joinpoints;

import java.util.Map;

public class MethodExecutionJoinPoint extends JoinPoint {

	public String methodName;
	
	public Object[] args;
	
	public MethodExecutionJoinPoint(String methodName, String location, Object[] args, Map<String,Object> context) {
		super(location, context);
	    this.methodName = methodName;
	    this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}
	
	public Object getTarget(){
		return context.get("targetObject");
	}

	@Override
	public String toString() {
		return "MethodExecution("+getTarget()+"."+methodName+"("+java.util.Arrays.toString(getArgs())+")";
	}
}
