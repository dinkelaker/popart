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

/**
 * A class describing pointcuts.
 * @author unkown
 */
public abstract class Pointcut {
	/**
	 * A pretty name
	 */
	private String name;

	/**
	 * constructor
	 * @param name a pretty name
	 */
	public Pointcut(String name) {
		this.name = name;
	}

	/**
	 * determines, whether this point cut matches the join point
	 * @param jp the join point
	 * @return <code>true</code> iff the pointcut matches
	 */
	public abstract boolean match(JoinPoint jp);

	/**
	 * Returns the residual of the partial evaluation of this pointcut for the StaticJoinPoint.
	 * The residual is <code>this</code> if nothing can be partial evaluated.
	 * The default implementation always just returns <code>this</code>.
	 * You should override this method, if you can partially evaluate something.
	 * @author Jan Stolzenburg
	 */
	public Pointcut partialEval(StaticJoinPoint staticJoinPoint) {
		return this;
	}

	public Object and(Object other) {
		if(this == BooleanPCD.ALWAYS) return other;
		if(this == BooleanPCD.NEVER) return BooleanPCD.NEVER;
		if(other == BooleanPCD.ALWAYS) return this;
		if(other == BooleanPCD.NEVER) return BooleanPCD.NEVER;
		return new AndPCD(this, (Pointcut)other);
	}

	public Object or(Object other) {
		if(this == BooleanPCD.ALWAYS) return BooleanPCD.ALWAYS;
		if(this == BooleanPCD.NEVER) return other;
		if(other == BooleanPCD.ALWAYS) return BooleanPCD.ALWAYS;
		if(other == BooleanPCD.NEVER) return this;
		return new OrPCD(this, (Pointcut)other);
	}

	public String toString() {
		return name;
	}
}
