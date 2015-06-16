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

import de.tud.stg.popart.joinpoints.AdviceExecutionJoinPoint;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;

public class AdviceExecutionPCD extends PrimitivePCD {
	
	public AdviceExecutionPCD() {
		super("advice_execution");
	}
	
	@Override
	public Pointcut partialEvalUncached(StaticJoinPoint staticJoinPoint) {
		return BooleanPCD.fromBoolean(AdviceExecutionJoinPoint.class.isAssignableFrom(staticJoinPoint.getCorrespondingNonStaticJoinPointType()));
	}
	
	public boolean matchUncached(JoinPoint jp) {
		if (!(jp instanceof AdviceExecutionJoinPoint)) return false;
		
//		List joinPointsOnStack = jp.context['joinPointStack']
//		JoinPoint underJP = joinPointsOnStack.find { jpInCflow -> jpInCflow instanceof AdviceExecutionJoinPoint }
		
		return true;
	}
}