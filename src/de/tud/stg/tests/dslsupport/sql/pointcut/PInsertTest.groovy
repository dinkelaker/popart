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
package de.tud.stg.tests.dslsupport.sql.pointcut;

import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.dslsupport.sql.model.SetClause;
import de.tud.stg.popart.dslsupport.sql.model.WhereClause;
import java.util.List;
import java.util.Map;
import org.junit.*;
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationContextParameter;
import de.tud.stg.tests.dslsupport.sql.AOPSQLTest;

import static de.tud.stg.popart.dslsupport.sql.model.TableReference.Table;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.Col;
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.WHERE;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.EQUALS;
import static org.junit.Assert.*

class PInsertTest extends AOPSQLTest {
	
	@Test
	void testEmptyArgument() {
		int i = 0
		Closure cl = {
			aspect(name:"pinsert_noArgument_test") {
				before(pinsert()) {
					i++	
				}
			}
			insertIntoValues("table", ["foo":"test","bar":"test"]);
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertEquals(1, i)
	}
	
	@Test
	void testAround() {
		def toggled = false
		Closure cl = {
			aspect(name:"pinsert_around_test") {
				around(pinsert()) {
					toggled = true
					proceed()
				}
			}
			insertIntoValues("table", ["foo":"test","bar":"test"]);
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertTrue(toggled)
	}

	@Test
	void testSelectListArgument() {
		def i = 0
		Closure cl = {
			aspect(name:"pinsert_argument_test") {
				before(pinsert(new QualifiedName("table"))) {
					i++
				}
			}
			
			insertIntoValues("table", ["foo":"test","bar":"test"]);
			insertIntoValues("bar", ["foo":"test","bar":"test"]);
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertEquals(1, i)
	}
}
