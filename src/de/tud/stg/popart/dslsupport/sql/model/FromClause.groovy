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
 * <p>A from clause consists of the <b>FROM</b> keyword followed by a list of tables. These
 * tables are separated by comma and could be derived from different kinds of joins.
 * The actual implementations only supports lists of {@link TableReference}, though.</p>
 * <p><b>Example:</b><br />
 * <b>FROM</b> students AS Students, grades</p>
 */
class FromClause {
	List<TableReference> tableReferences = []
	
	FromClause(List<TableReference> tableReferences) {
		this.tableReferences = tableReferences
	}
	
	String toString() {
		return "FROM ${tableReferences.join(', ')}"
	}
	
	static FromClause FROM(List<TableReference> tableReferences) {
		return new FromClause(tableReferences)
	}
}
