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
package de.tud.stg.popart.dslsupport.sql.model

/**
 * This class is a model for SQL queries (a.k.a. SELECT statements)
 */
class Query {
	Quantifier quantifier
	List<ColumnReference> selectList
	TableExpression tableExpression
	
	Query(List<ColumnReference> selectList, TableExpression tableExpression, Quantifier quantifier = Quantifier.Default) {
		this.selectList = selectList
		this.tableExpression = tableExpression
		this.quantifier = quantifier
	}
	
	String toString() {
		def query = "${selectList.join(', ')} ${tableExpression}"
		if (getQuantifier() == Quantifier.Default) {
			return "SELECT ${query}"
		} else {
			return "SELECT ${quantifier} ${query}"
		}
	}
	
	static Query SELECT(List<ColumnReference> selectList, TableExpression tableExpression) {
		return new Query(selectList, tableExpression)
	}
	
	static Query SELECT(Quantifier quantifier, List<ColumnReference> selectList, TableExpression tableExpression) {
		return new Query(selectList, tableExpression, quantifier)
	}
}
