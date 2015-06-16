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

import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.dslsupport.sql.model.SetClause;
import de.tud.stg.popart.dslsupport.sql.model.WhereClause;
import java.util.List;
import org.junit.*;
import de.tud.stg.popart.aspect.extensions.instrumentation.InstrumentationContextParameter;
import de.tud.stg.tests.dslsupport.sql.AOPSQLTest;

import static de.tud.stg.popart.dslsupport.sql.model.TableReference.Table;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.Col;
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.WHERE;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.EQUALS;
import static org.junit.Assert.*

class PUpdateTest extends AOPSQLTest {
	
	@Test
	void testEmptyArgument() {
		int i = 0
		Closure cl = {
			aspect(name:"pupdate_noArgument_test") {
				before(pupdate()) {
					i++	
				}
			}
			updateSetWhere("foo", [new SetClause(new Identifier("bar"), "1")],null);
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertEquals(1, i)
	}
	
	@Test
	void testAround() {
		def toggled = false
		Closure cl = {
			aspect(name:"pupdate_around_test") {
				around(pupdate()) {
					toggled = true
					proceed()
				}
			}
			updateSetWhere("foo", [new SetClause(new Identifier("bar"), "1")],null)
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertTrue(toggled)
	}

	@Test
	void testSelectListArgument() {
		def i = 0
		Closure cl = {
			aspect(name:"pupdate_argument_test") {
				before(pupdate(new QualifiedName("foo"))) {
					i++
				}
			}
			
			updateSetWhere("foo", [new SetClause(new Identifier("bar"), "1")],null)
			updateSetWhere("bar", [new SetClause(new Identifier("bar"), "1")],null)
		}
		
		cl.delegate = sqldsl
		cl()
		
		assertEquals(1, i)
	}
}
