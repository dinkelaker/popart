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
package de.tud.stg.popart.dslsupport.sql ;

import java.sql.Connection ;
import java.sql.DriverManager ;
import java.sql.ResultSet ;
import java.sql.ResultSetMetaData ;
import java.sql.SQLException ;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Properties ;
import java.util.Random ;
import groovy.sql.*;

/**
 * This class is an implementation of ISQLConnection
 * @see ISQLConnection
 */
public class JDBCConnection implements ISQLConnection {
	private final Sql sql
	
	/**
	 * Use given argument as back-end
	 * @param sql SQL connection to use
	 */
	public JDBCConnection(Sql sql) {
		this.sql = sql
	}
	
	/**
	 * Closes the sql connection.
	 * @see Sql#close()
	 */
	public void close() {
		sql.close
	}
	
	@Override
	public boolean execute(String query) throws SQLException {
		return sql.execute(query)
	}
	
	@Override
	public List<List> executeInsert(String query) throws SQLException {
		return sql.executeInsert(query)
	}
	
	@Override
	public int executeUpdate(String query) throws SQLException {
		return sql.executeUpdate(query)
	}
	
	@Override
	public List<Map> executeQuery(String query) throws SQLException {
		return sql.rows(query)
	}
	
	public Set<String> getColumnNames() throws SQLException{
		Set<String> returnSet = new HashSet<String>();
		def tables = getTableNames();
		def columnResults = sql.getConnection().getMetaData().getColumns(null, null, null, null);
		while(columnResults.next()){
			if(tables.contains(columnResults.getString(3).toLowerCase())){
				returnSet.add(columnResults.getString(4).toLowerCase());
			}
		}
		return returnSet;
	}
	
	public Set<String> getTableNames() throws SQLException{
		Set<String> returnSet = new HashSet<String>();
		def tableResults = sql.getConnection().getMetaData().getTables(null, null, null, ["TABLE"] as String[]);
		while(tableResults.next()){
			returnSet.add(tableResults.getString(3).toLowerCase());
		}
		return returnSet;
	}
	
	public boolean isIdentifier(String string) {
		def tableNames = getTableNames()
		def columnNames = getColumnNames()
		return columnNames.contains(string) || tableNames.contains(string)
	}
} 
