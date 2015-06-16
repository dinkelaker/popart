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
package de.tud.stg.popart.dslsupport.sql.model.bool

class ComparisonPredicate extends Predicate {
	def val1, val2
	ComparisonOperator op
	
	ComparisonPredicate(ComparisonOperator op, val1, val2) {
		this.op = op
		this.val1 = val1
		this.val2 = val2
	}
	
	String toString() {
		return "(${val1} ${op} ${val2})"
	}
	
	boolean equals(Object o) {
		if(!(o instanceof ComparisonPredicate)) return false
		def other = o as ComparisonPredicate
		return val1 == other.val1 && val2 == other.val2 && op == other.op
	}
	
	static ComparisonPredicate EQUALS(val1, val2) {
		return new ComparisonPredicate(ComparisonOperator.EQUALS, val1, val2)
	}
	
	static ComparisonPredicate LIKE(val1, val2) {
		return new ComparisonPredicate(ComparisonOperator.LIKE, val1, val2)
	}
}
