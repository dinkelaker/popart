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
package de.tud.stg.tests.dslsupport.sql.pointcut

import org.junit.Test;
import de.tud.stg.tests.dslsupport.sql.AOPSQLTest;

import static org.junit.Assert.*
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*
import static de.tud.stg.popart.dslsupport.sql.model.AnyTableReference.ANY_TABLE;

class PFromTest extends AOPSQLTest {

	/**
	 * Doesn't succeed in test-suite. Change of sqlConnection to static in SimpleSQL broke this.
	 * This test suffers from side-effects, but I'm to lazy to investigate now
	 */
	// TODO: investigate how this test suffers from side-effects
	
	//Tests without ANY_TABLE.
	@Test
	void testSingleTableWithoutANY() {
		def i = 0
		Closure cl = {
			aspect(name:"pfrom_noArgument_test") {
				before(pfrom([Table("bar")])) {
					i++
				}
			}
			selectFrom([Col("foo")], [Table("bar"),Table("bla")])
			selectFrom([Col("foo")], [Table("bar")])
			selectFrom([Col("foo")], [Table("foo")])
		}
		
		cl.delegate = sqldsl
		cl()

		assertEquals(2, i);
		//-DINKELAKER-2011-04-04-BEGIN
		//Refactoring: all tables pointcut filter must be present in set of tables in the queries, but not vice versa)  
		//assertEquals(1, i);
		//-DINKELAKER-2011-04-04-END
	}
	
	@Test
	void testMultipleTablesWithoutANY() {
		def i = 0
		Closure cl = {
			aspect(name:"pfrom_withArgument_test") {
				before(pfrom([Table("bar"),Table("foo")])) {
					i++
				}
			}
			selectFrom([Col("foo")], [Table("foo"),Table("bar")])
			selectFrom([Col("foo")], [Table("bla"),Table("foo")])
		}
		
		cl.delegate = sqldsl
		cl()
		assertEquals(1, i)
	}
	
	//Tests with ANY_TABLE.
	@Test
	void testANY() {
		def i = 0
		Closure cl = {
			aspect(name:"pfrom_noArgument_test") {
				before(pfrom([ANY_TABLE])) {
					i++
				}
			}
			selectFrom([Col("foo")], [Table("bar")])
		}
		
		cl.delegate = sqldsl
		cl()
		assertEquals(1, i)
	}
	
	@Test
	void testListArgumentWithAny() {
		def i = 0
		Closure cl = {
			aspect(name:"pfrom_withArgument_test") {
				before(pfrom([ANY_TABLE,Table("bar")])) {
					i++
				}
			}
			selectFrom([Col("foo")], [Table("bar"), Table("foo")])
			selectFrom([Col("foo")], [Table("footable"), Table("bla")])
		}
		
		cl.delegate = sqldsl
		cl()
		assertEquals(1, i)
	}
}
