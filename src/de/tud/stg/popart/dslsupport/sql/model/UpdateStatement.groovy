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
 * <p>An update statement consists of the UPDATE keyword, followed by the table to update, the SET keyword,
 * a comma-separated-list of set clauses and finally an optional where clause.</p>
 * <p><b>Example:</b><br />
 * <b>UPDATE</b> Students
 * <b>SET</b> name = "Robert'); DROP TABLE Students;--", nickname = 'LITTLE BOBBY TABLES'
 * <b>WHERE</b> id = 3</p>
 * 
 * @see SetClause
 * @see WhereClause
 */
class UpdateStatement {
	QualifiedName tableName
	List<SetClause> setClauseList
	WhereClause whereClause
	
	UpdateStatement(QualifiedName tableName, List<SetClause> setClauseList, WhereClause whereClause = null) {
		this.tableName = tableName
		this.setClauseList = setClauseList
		this.whereClause = whereClause
	}
	
	String toString() {
		def string = "UPDATE ${tableName} SET ${setClauseList.join(', ')}"
		if (whereClause != null) string += " ${whereClause}"
		return string
	}
}
