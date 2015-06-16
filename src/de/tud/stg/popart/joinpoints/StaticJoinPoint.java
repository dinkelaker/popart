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

/**
 * StaticJoinPoints contain the information that are available if an joinpoint is reached.
 * For some JoinPoint types, the static information is enough to (partialy) evaluate them.
 * For example, the class of the receiver and the name of the called method is all
 * an MethodCallJoinPoint needs to know to evaluate whether it matches or not.
 * The partial evaluate code uses these StaticJoinPoints to represent the information known
 * about a potential joinpoint. It passes them to the "partialEval" methods of the Pointcuts.
 * There are be different kinds of StaticJoinPoints: For method calls, for field assingment,
 * for constructor calls and so on.
 * But we can currently just intercept method calls with this instrumentation.
 * Some other pointcuts can reuse the StaticJoinPoints for method calls, as long as they don't have their
 * own StaticJoinPoint kind. Namely, the pointcuts that represent special kinds of method calls,
 * like most or all of the EDSL specific pointcuts: ProcessExecution, ServiceCall, ...
 * @author Jan Stolzenburg
 */
public class StaticJoinPoint {
	
	private Class<?> receiverClass;
	private String methodName;
	private Class<? extends JoinPoint> correspondingNonStaticJoinPointType;
	
	public StaticJoinPoint(Class<?> receiverClass, String methodName, Class<? extends JoinPoint> correspondingNonStaticJoinPointType) {
		this.receiverClass = receiverClass;
		this.methodName = methodName;
		this.correspondingNonStaticJoinPointType = correspondingNonStaticJoinPointType;
	}
	
	public Class<?> getReceiverClass() {
		return receiverClass;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public Class<? extends JoinPoint> getCorrespondingNonStaticJoinPointType() {
		return correspondingNonStaticJoinPointType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StaticJoinPoint){
			StaticJoinPoint other = (StaticJoinPoint) obj;
			return this.receiverClass.equals(other.receiverClass) && this.methodName.equals(other.methodName) && this.correspondingNonStaticJoinPointType.equals(other.correspondingNonStaticJoinPointType);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return receiverClass.hashCode()^methodName.hashCode()^correspondingNonStaticJoinPointType.hashCode();
	}
}