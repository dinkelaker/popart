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
 * <p>A delete statement consists of the <b>DELETE FROM</b> keyword followed by
 * a table name and an optional where clause.</p>
 * <p><b>Example:</b><br />
 * <b>DELETE FROM</b> Students <b>WHERE</b> id = 3</p>
 */
class DeleteStatement {
	QualifiedName tableName
	WhereClause whereClause
	
	DeleteStatement(QualifiedName tableName, WhereClause whereClause = null) {
		this.tableName = tableName
		this.whereClause = whereClause
	}
	
	String toString() {
		def string = "DELETE FROM ${tableName}"
		if (whereClause != null) string += " ${whereClause}"
		return string
	}
}
