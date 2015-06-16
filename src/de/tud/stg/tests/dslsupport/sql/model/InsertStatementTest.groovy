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

import org.junit.Test
import org.junit.Before
import de.tud.stg.popart.dslsupport.sql.model.*

class InsertStatementTest {
	def table
	def values
	
	@Before
	void setup() {
		table = new QualifiedName("foo.bar")
		values = [id:"DEFAULT", fruit:"'banana'", meat:"'chicken'", num:3]
	}
	
	@Test
	void testToString() {
		def statement = new InsertStatement(table, values)
		assertEquals("INSERT INTO foo.bar (id, fruit, meat, num) VALUES (DEFAULT, 'banana', 'chicken', 3)", statement.toString())
	}
}
