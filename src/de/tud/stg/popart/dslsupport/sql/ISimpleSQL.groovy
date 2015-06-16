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
package de.tud.stg.popart.dslsupport.sql;

import java.util.List;
import java.util.Map;
import de.tud.stg.popart.dslsupport.sql.model.*;

/**
 * Language interface for a simple SQL DSL interpreter. It is simple, because nested queries or joins are not supported <i>(, yet)</i>.
 */
public interface ISimpleSQL {
	/**
	 * <p>This method tries to create a SQL connection out of the given argument that will be used in all later calls.</p>
	 * 
	 * <p><b>Example:</b><br />
	 * <b>CONNECT</b> <i>JDBC-URL<i></p>
	 * @param jdbcUrl see http://download.oracle.com/javase/tutorial/jdbc/basics/connecting.html for more information on JDBC URL's
	 */
	public void connect(String jdbcUrl)
	
	/**
	 * <b>SELECT</b> <i>Selection</i> <b>FROM</b> <i>Tables</i>
	 * @param selectList list of columns to select
	 * @param tables list of tables to get the columns from
	 * @return result of the query
	 */
	public List<Map> selectFrom(List<ColumnReference> selectList, List<TableReference> tables)
	
	/**
	 * <b>SELECT</b> <i>Selection</i> <b>FROM</b> <i>Tables</i> <b>WHERE</b> <i>Condition</i>
	 * @param selectList list of columns to select
	 * @param tables list of tables to get the columns from
	 * @param whereClause condition of what rows to get
	 * @return result of the query
	 */
	public List<Map> selectFromWhere(List<ColumnReference> selectList, List<TableReference> tables, WhereClause whereClause)
	
	/**
	 * <b>UPDATE</b> <i>Table</i> <b>SET</b> <i>Assignments</i> <b>WHERE</b> <i>Condition</i>
	 * @param tableName table to update
	 * @param setList what to update and how
	 * @param whereClause condition of what rows to update
	 * @return the number of rows updated
	 */
	public int updateSetWhere(Identifier tableName, List<SetClause> setList, WhereClause whereClause)
	
	/**
	 * <b>DELETE FROM</b> <i>Table</i> <b>WHERE</b> <i>Condition</i>
	 * @param tableName table to delete from
	 * @param whereClause condition which rows to delete
	 * @return always false
	 */
	public boolean deleteFromWhere(Identifier tableName, WhereClause whereClause)
	
	/**
	 * <b>INSERT INTO</b> <i>Table</i> <b>(</b><i>Columns</i><b>)</b> <b>VALUES</b> <b>(</b><i>Values</i><b>)</b>
	 * @param tableName table to insert into
	 * @param columns list of columns to insert into
	 * @param values values for columns
	 * @return list of auto-generated column values
	 * @throws SQLDSLException if the two lists have different lengths
	 */
	public List<List> insertIntoValues(Identifier tableName, List columns, List values) throws SQLDSLException
}
