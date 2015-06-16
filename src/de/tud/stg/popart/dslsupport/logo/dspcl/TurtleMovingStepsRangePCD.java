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
package de.tud.stg.popart.dslsupport.logo.dspcl;

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

import de.tud.stg.popart.dslsupport.logo.dsjpm.*;

public class TurtleMovingStepsRangePCD extends Pointcut {

	public TurtleMovingStepsRangePCD(int minSteps, int maxSteps) {
		super("pmoving(minSteps,maxSteps)");
		this.minSteps = minSteps;
		this.maxSteps = maxSteps;
	}
	
	private int minSteps;

	public int getMinSteps() {
		return minSteps;
	}

	public void setMinSteps(int minSteps) {
		this.minSteps = minSteps;
	}

	private int maxSteps;

	public int getMaxSteps() {
		return maxSteps;
	}

	public void setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
	}
	
	@Override
	public boolean match(JoinPoint jp) {
		return 
		  ((jp instanceof BackwardJoinPoint) ||
		  (jp instanceof ForwardJoinPoint)) &&
		  (minSteps <= ((TurtleMoveJoinPoint)jp).getSteps() && 
		  maxSteps >= ((TurtleMoveJoinPoint)jp).getSteps());
	}

}
