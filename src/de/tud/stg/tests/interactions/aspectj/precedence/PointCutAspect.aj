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
package de.tud.stg.tests.interactions.aspectj.precedence;

import de.tud.stg.tests.interactions.aspectj.precedence.TestObject;

abstract aspect PointCutAspect {
	
	pointcut pcBefore(TestObject to):
	execution (* testMethodBefore())&&
	this(to);

	pointcut pcAfter(TestObject to):
		execution (* testMethodAfter())&&
		this(to);

	pointcut pcAround(TestObject to):
		execution (* testMethodAround())&&
		this(to);

	pointcut pcAroundProceedAfter(TestObject to):
		execution (* testMethodAroundProceedAfter())&&
		this(to);
	
	pointcut pcAroundProceedBefore(TestObject to):
		execution (* testMethodAroundProceedBefore())&&
		this(to);

	pointcut pcAroundProceed2(TestObject to):
		execution (* testMethodAroundProceed2())&&
		this(to);

}
