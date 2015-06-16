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
package de.tud.stg.popart.aspect;

import groovy.lang.Closure;

import java.util.Map;

import de.tud.stg.popart.dslsupport.*;
import de.tud.stg.popart.pointcuts.*;

/**
 * This class defines an PointcutDSL implementing the default.
 * @author Tom Dinkelaker
 */
public class PointcutDSL implements DSL {
	public static Interpreter getInterpreter(Map<String,Object> context) {
		return DSLCreator.getInterpreter(new PointcutDSL(),context);
	}
	
	/* Literals */
	
	
	/* Operations */
	@Deprecated
	public Pointcut service_call(String serviceNameRegEx) {
		throw new RuntimeException("This is a domain-specific point cut. Use the according domain-specific ProcessPointcutDSL!");
	}
	
	@Deprecated
	public Pointcut service_selection(String categoryRegEx) {
		throw new RuntimeException("This is a domain-specific point cut. Use the according domain-specific ProcessPointcutDSL!");
	}
	
	public Pointcut if_pcd(Closure ifClosure) {
		return new IfPCD(ifClosure);
	}
		
	public Pointcut debug(Closure pcClosure) {
		return new DebugPCD(pcClosure);
	}
		
	public Pointcut deep_debug(Closure pcClosure) {
		return new DeepDebugPCD(pcClosure);
	}
		
	public Pointcut input_debug(Closure pcClosure) {
		return new InputDebugPCD(pcClosure);
	}
		
	public Pointcut not(Pointcut pc) {
		if(pc instanceof BooleanPCD) return BooleanPCD.fromBoolean(pc == BooleanPCD.NEVER);
		return new NotPCD(pc);
	}
		
	public Pointcut cflow(Pointcut pc) {
		if(pc instanceof BooleanPCD) return pc;
		return new CflowPCD(pc);
	}
		
	public Pointcut cflowbelow(Pointcut pc) {
		if(pc instanceof BooleanPCD) return pc;
		return new CflowBelowPCD(pc);
	}
		
	@Deprecated
	public Pointcut task_execution(String taskNamePattern) {
		throw new RuntimeException("This is a domain-specific point cut. Use the according domain-specific ProcessPointcutDSL!");
	}

	@Deprecated
	public Pointcut process_execution(String processNamePattern) {
		throw new RuntimeException("This is a domain-specific point cut. Use the according domain-specific ProcessPointcutDSL!");
	}
	
	public Pointcut method_call(String signaturePattern) {
		return new MethodCallPCD(signaturePattern);
	}
		
	public Pointcut method_execution(String signaturePattern) {
		return new MethodExecutionPCD(signaturePattern);
	}
	
    public Pointcut initialization(String signaturePattern) {
        return new InitializationPCD(signaturePattern);
    }
    
    public Pointcut field_assignment(String signaturePattern) {
      return new FieldAssignmentPCD(signaturePattern);
    }
		
	public Pointcut advice_execution() {
		return new AdviceExecutionPCD();
	}
		
}



