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
package de.tud.stg.popart.pointcuts;

import de.tud.stg.popart.aspect.PointcutDSL;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;

public class NotPCD extends PointcutWithCache {
	private Pointcut pc;
	
	public NotPCD(Pointcut pc) {
		super("not("+pc+")");
		this.pc = pc;
	}
	
	@Override
	public Pointcut partialEvalUncached(StaticJoinPoint staticJoinPoint) {
		Pointcut residual = pc.partialEval(staticJoinPoint);
		if(residual == pc){
			//preserve instance if nothing was partially evaluated
			return this;
		}else{
			//use negotiation of residual otherwise
			return new PointcutDSL().not(residual);
		}
	}
	
	@Override
	public boolean matchUncached(JoinPoint jp) {
		return !pc.match(jp);
	}

}
