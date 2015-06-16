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
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationContextParameter;

import static de.tud.stg.popart.dslsupport.sql.model.TableReference.Table;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.Col;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.Table;
import static de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference.ANY;
import static org.junit.Assert.*

class PSelectTest extends AOPSQLTest {
	
	//Tests without ANY.
	@Test
	void testSingleColumnWithoutANY() {
		int i = 0
		Closure cl = {
			aspect(name:"pselect_noArgument_test") {
				before(pselect([Col("foo")])) {
					i++
				}
			}
			selectFrom([Col("foo")], [Table("bar")])
			selectFrom([Col("bar")], [Table("table")])
			selectFrom([Col("foo"),Col("bar")], [Table("table")])
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertEquals(2, i)
		//-DINKELAKER-2011-04-04-BEGIN
		//Refactoring: all tables pointcut filter must be present in set of tables in the queries, but not vice versa)  
		//assertEquals(1, i);
		//-DINKELAKER-2011-04-04-END
	}

	@Test
	void testSingleColumnWithoutANYAround() {
		def toggled = false
		Closure cl = {
			aspect(name:"pselect_around_test") {
				around(pselect([Col("foo")])) {
					toggled = true
					proceed()
				}
			}
			selectFrom([Col("foo")], [Table("bar")])
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertTrue(toggled)
	}

	@Test
	void testMultileColumnsWithoutANY() {
		int i = 0
		Closure cl = {
			aspect(name:"pselect_noArgument_test") {
				before(pselect([Col("foo"),Col("bar")])) {
					i++
				}
			}
			selectFrom([Col("foo")], [Table("bar")])
			selectFrom([Col("foo"),Col("bar")], [Table("table")])
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertEquals(1, i)
	}
	
	//Tests with ANY
	@Test
	void testAny() {
		int i = 0
		Closure cl = {
			aspect(name:"pselect_noArgument_test") {
				before(pselect([ANY])) {
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
	void testAnyAround() {
		def toggled = false
		Closure cl = {
			aspect(name:"pselect_around_test") {
				around(pselect([ANY])) {
					toggled = true
					proceed()
				}
			}
			selectFrom([Col("foo")], [Table("bar")])
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertTrue(toggled)
	}
	
	@Test
	void testListArgumentWithAny() {
		def i = 0
		Closure cl = {
			aspect(name:"pselect_argument_test") {
				before(pselect([ANY,Col("foo")])) {
					i++
				}
			}
			
			selectFrom([Col("bar"),Col("bla")], [Table("table")])
			selectFrom([Col("foo"),Col("bla")], [Table("table")])
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertEquals(1, i)
	}
}
