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

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;

public class OrPCD extends PointcutWithCache {
	
	private Pointcut left;
	private Pointcut right;
	
	public OrPCD(Pointcut left, Pointcut right) {
		super("or("+left+","+right+")");
		this.left = left;
		this.right = right;
	}
	
	public Pointcut partialEvalUncached(StaticJoinPoint staticJoinPoint) {
		Pointcut leftResidual = left.partialEval(staticJoinPoint);
		Pointcut rightResidual = right.partialEval(staticJoinPoint);
		if(left == leftResidual && right == rightResidual){
			//preserve instance if nothing was partially evaluated
			return this;
		}else{
			//if something was partially evaluated, re-concatenate the residuals
			return (Pointcut) leftResidual.or(rightResidual);
		}
	}

	@Override
	public boolean matchUncached(JoinPoint jp) {
		return left.match(jp) || right.match(jp);
	}
}