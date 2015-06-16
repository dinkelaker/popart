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

class BinaryBooleanTerm extends BooleanTerm {
	Set<BooleanTerm> terms
	BinaryBooleanOperator op
	
	BinaryBooleanTerm(BinaryBooleanOperator op, Set<BooleanTerm> terms) {
		this.op = op
		this.terms = terms
	}
	
	String toString() {
		def opString = op.toString()
		def joined = terms.join(" ${opString} ")
		return "(${joined})"
	}
	
	boolean equals(Object o) {
		if (!(o instanceof BinaryBooleanTerm)) return false
		def other = o as BinaryBooleanTerm
		return terms == other.terms && op == other.op
	}
	
	static BinaryBooleanTerm AND(Set<BooleanTerm> terms) {
		return new BinaryBooleanTerm(BinaryBooleanOperator.AND, terms)
	}
	
	static BinaryBooleanTerm OR(Set<BooleanTerm> terms) {
		return new BinaryBooleanTerm(BinaryBooleanOperator.OR, terms)
	}
}
