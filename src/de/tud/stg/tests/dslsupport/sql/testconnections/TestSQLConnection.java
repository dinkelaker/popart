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
package de.tud.stg.tests.dslsupport.sql.testconnections;

import java.sql.SQLException ;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tud.stg.popart.dslsupport.sql.ISQLConnection;

public class TestSQLConnection implements ISQLConnection {
	String lastQuery = "No Query was executed";
	Set<String> columnNames;
	Set<String> tableNames;

	public String getLastQuery() {
		return lastQuery;
	}

	public boolean execute(String query) throws SQLException {
		lastQuery = query;
		return false;
	}
	
	public List<List<Object>> executeInsert(String query) throws SQLException {
		lastQuery = query;
		return null;
	}
	
	public int executeUpdate( String query ) throws SQLException {
		lastQuery = query;
		return -1;
	}
	
	public List<Map> executeQuery(String query) {
		lastQuery = query;
		return null;
	}
	
	public Set<String> getColumnNames() throws SQLException{
		return columnNames;
	}
	
	public Set<String> getTableNames() throws SQLException{
		return tableNames;
	}
	
	public void setColumnNames(Set<String> columnNames){
		this.columnNames = columnNames;
	}
	
	public void setTableNames(Set<String> tableNames){
		this.tableNames = tableNames;
	}
	
	public boolean isIdentifier(String string) {
		return columnNames.contains(string) || tableNames.contains(string);
	}
}
