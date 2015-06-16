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

import java.sql.ResultSet;
import java.sql.SQLException ;
import java.util.List;
import java.util.Map;

import groovy.sql.Sql;
import de.tud.stg.popart.dslsupport.sql.JDBCConnection;

public class TestJDBCConnection extends JDBCConnection {
	String lastQuery = "No Query was executed";
	protected setLastQuery(String lastQuery) { this.lastQuery = lastQuery }
	
	public TestJDBCConnection() {
		super(Sql.newInstance("jdbc:derby:testdb;create=true"))
	}

	public List<Map> executeQuery(String query) throws SQLException {
		lastQuery = query;
		return super.executeQuery( query );
	}

	public int executeUpdate(String query) throws SQLException {
		lastQuery = query;
		return super.executeUpdate( query ) ;
	}
}
