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
package de.tud.stg.tests.dslsupport.sql.model;

import static org.junit.Assert.*;
import static de.tud.stg.popart.dslsupport.sql.model.FromClause.*
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*
import static de.tud.stg.popart.dslsupport.sql.model.Query.*
import static de.tud.stg.popart.dslsupport.sql.model.Quantifier.*


import org.junit.Test
import org.junit.Before

import de.tud.stg.popart.dslsupport.sql.model.*

class QueryTest {
	def selectList
	def tableExpression
	
	@Before
	void setup() {
		selectList = [Col("foo.id"), Col("name")]
		def fromClause = FROM([Table("scheme.tablename", "foo")])
		tableExpression = new TableExpression(fromClause)
	}
	
	@Test
	void testToStringDefaultQuantifier() {
		assertEquals("SELECT foo.id, name FROM scheme.tablename AS foo",
			         SELECT(selectList, tableExpression).toString())
	}
	
	@Test
	void testToStringDistinctQualifier() {
		assertEquals("SELECT DISTINCT foo.id, name FROM scheme.tablename AS foo",
					 SELECT(DISTINCT, selectList, tableExpression).toString())
	}
}
