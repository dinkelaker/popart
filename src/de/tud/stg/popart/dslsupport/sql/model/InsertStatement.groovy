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
 * <p>An insert statement consists of the <b>INSERT INTO</b> keyword followed by a table name,
 * a parenthesized and comma-separated list of columns, they <b>VALUES</b> keyword and
 * a parenthesized and comma-separated list of values.</p>
 * <p><b>Example:</b><br />
 * <b>INSERT INTO</b> Scheme.Students (name, nickname)
 * <b>VALUES</b> ("Robert'); DROP TABLE Students;--", 'LITTLE BOBBY TABLES')</p>
 */
class InsertStatement {
	QualifiedName tableName
	Map<Identifier, String> values
	
	/**
	 * Constructor for a new insert statement
	 * @param tableName name of the table to insert to
	 * @param values map of the form column:value
	 */
	InsertStatement(QualifiedName tableName, Map<Identifier, String> values) {
		this.tableName = tableName
		this.values = values
	}
	
	String toString() {
		def keys = values.keySet().join(", ")
		def vals = values.values().join(", ")
		return "INSERT INTO ${tableName} (${keys}) VALUES (${vals})"
	}
}
