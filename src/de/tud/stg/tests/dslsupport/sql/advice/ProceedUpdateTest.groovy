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

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.dslsupport.sql.model.Query;
import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.popart.dslsupport.sql.model.SetClause;
import de.tud.stg.popart.dslsupport.sql.model.WhereClause;
import de.tud.stg.tests.dslsupport.sql.testconnections.TestSQLConnection;
import de.tud.stg.tests.dslsupport.sql.AOPSQLTest;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.*;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.*;
import static org.junit.Assert.*;

/**
 * This is a test for the proceed_update advice.
 */
class ProceedUpdateTest extends AOPSQLTest {
	
	@Test
	public void proceed_update_setclause(){
		def aspect_entered = false;
		
		aspect(name : "PROCEEDUPDATETEST_proceed_update_setClauses"){
			around(pupdate()){
				aspect_entered = true;
				proceed_update([new SetClause(new Identifier("col3"), "3")]);
			}
		}

		Closure cl = {
			updateSetWhere(new Identifier("table1"), [new SetClause(new Identifier("col1"), "1"),
				new SetClause(new Identifier("col2"), "2")], WHERE(EQUALS(new Identifier("col4"), "4")))
		}
		cl.delegate = ccc;
		cl();
		
		assertTrue(aspect_entered);
		assertEquals("UPDATE table1 SET col1 = 1, col2 = 2, col3 = 3 WHERE (col4 = 4)",sqlconnection.lastQuery);
	}
	
}
