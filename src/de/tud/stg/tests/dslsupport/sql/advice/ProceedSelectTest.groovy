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
package de.tud.stg.tests.dslsupport.sql.advice

import org.junit.Test;
import de.tud.stg.popart.dslsupport.sql.model.Query;
import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.tests.dslsupport.sql.testconnections.TestSQLConnection;
import de.tud.stg.tests.dslsupport.sql.AOPSQLTest;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.AnyTableReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.*;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.*;
import static org.junit.Assert.*;

/**
 * This is a test for the proceed_select advice. Proceeds with and
 * without the ANY keyword are tested.
 */
class ProceedSelectTest extends AOPSQLTest {
	
	@Test
	public void proceed_select_columns(){
		def aspect_entered = false;
		
		aspect(name : "PROCEEDSELECTTEST_proceed_select_columns"){
			around(pselect([ANY])){
				aspect_entered = true;
				proceed_select([ANY,Col("col3")], [ANY_TABLE]);
			}
		}

		Closure cl = {
			selectFromWhere([Col("col1"),Col("col2")],[Table("table1")],WHERE(EQUALS(new Identifier("col1"),2)));
		}
		cl.delegate = ccc;
		cl();
		
		assertTrue(aspect_entered);
		assertEquals("SELECT col1, col2, col3 FROM table1 WHERE (col1 = 2)",sqlconnection.lastQuery);
	}
	
	@Test
	public void proceed_select_columnsAndTables(){
		def aspect_entered = false;
		
		aspect(name : "PROCEEDSELECTTEST_proceed_select_columnsAndTables"){
			around(pselect([ANY])){
				aspect_entered = true;
				proceed_select([ANY,Col("col3")],[ANY_TABLE, Table("table2")]);
			}
		}

		Closure cl = {
			selectFromWhere([Col("col1"),Col("col2")],[Table("table1")],WHERE(EQUALS(new Identifier("col1"),2)));
		}
		cl.delegate = ccc;
		cl();
		
		assertTrue(aspect_entered);
		assertEquals("SELECT col1, col2, col3 FROM table1, table2 WHERE (col1 = 2)",sqlconnection.lastQuery);
	}
}
