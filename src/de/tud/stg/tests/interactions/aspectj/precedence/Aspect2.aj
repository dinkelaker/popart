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

import de.tud.stg.tests.interactions.aspectj.precedence.PointCutAspect;
import de.tud.stg.tests.interactions.aspectj.precedence.TestObject;

public aspect Aspect2 extends PointCutAspect {
	int id = 2;

	private void trace(TestObject to) {
		to.results.add(id);
	}

	before(TestObject to): pcBefore (to) {
		trace(to);
	}

	after(TestObject to): pcAfter (to) {
		trace(to);
	}

	void around(TestObject to): pcAround(to) {
		trace(to);
	}

	void around(TestObject to): pcAroundProceedBefore(to) {
		proceed(to);
		trace(to);
	}
	void around(TestObject to): pcAroundProceedAfter(to) {
		trace(to);
		proceed(to);
	}

	void around(TestObject to): pcAroundProceed2(to) {
		trace(to);
		proceed(to);
		trace(to);
	}
}
