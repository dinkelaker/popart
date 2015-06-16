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
import org.junit.Test;
import de.tud.stg.popart.dslsupport.sql.model.*;

class ReferenceTest {
	@Test
	void testSplittingWithoutQualifier() {
		def selection = new Reference("name_name")
		assertEquals("name_name", selection.name)
		assertEquals(null, selection.qualifier)
	}
	
	@Test
	void testSplittingWithQualifier() {
		def selection = new Reference("qualifier.name_name")
		assertEquals("name_name", selection.name)
		assertEquals("qualifier", selection.qualifier)
	}
	
	@Test
	void testAliasGetsSet() {
		def selection = new Reference("name", "foo")
		assertEquals("foo", selection.alias)
		selection = new Reference("qualifier", "name", "foo")
		assertEquals("foo", selection.alias)
	}
	
	@Test
	void testToStringBareName() {
		def selection = new Reference("name")
		assertEquals("name", selection.toString())
	}
	
	@Test
	void testToStringBareNameAndAlias() {
		def selection = new Reference("name", "alias")
		assertEquals("name AS alias", selection.toString())
	}
	
	@Test
	void testToStringQualified() {
		def selection = new Reference("qualifier.name")
		assertEquals("qualifier.name", selection.toString())
	}
	
	@Test
	void testToStringQualifiedWithAlias() {
		def selection = new Reference("qualifier.name", "alias")
		assertEquals("qualifier.name AS alias", selection.toString())
	}
	
	@Test
	void testParsingNameOnly() {
		def ref = new Reference("name")
		assertEquals("name", ref.toString())
	}
	
	@Test
	void testParsingWithQualifier() {
		def ref = new Reference("qualifier.name")
		assertEquals("qualifier.name", ref.toString())
	}
	
	@Test
	void testParsingAsWhitespace() {
		def ref = new Reference("qualifier.name foo")
		assertEquals("qualifier.name AS foo", ref.toString())
	}
	
	@Test
	void testParsingAsLowercase() {
		def ref = new Reference("qualifier.name as foo")
		assertEquals("qualifier.name AS foo", ref.toString())
	}
	
	@Test
	void testParsingAsUppercase() {
		def ref = new Reference("qualifier.name AS foo")
		assertEquals("qualifier.name AS foo", ref.toString())
	}
	
	@Test
	void testParsingTooManyWhitespace() {
		def ref = new Reference("qualifier.name    as   foo")
		assertEquals("qualifier.name AS foo", ref.toString())
	}
	
	@Test
	void testEqualsRef() {
		def ref = new Reference("qualifier.name as foo")
		def ref2 = new Reference("qualifier.name as foo")
		assertTrue(ref == ref2)
	}
	
	@Test
	void testNotEqualsRef() {
		def ref = new Reference("qualifier.name as foo")
		def ref2 = new Reference("qualifier.name as bar")
		assertFalse(ref == ref2)
	}
	
	@Test
	void testNotEqualsName() {
		def ref = new Reference("qualifier.name as foo")
		def ref2 = new Reference("qualifier.foo as foo")
		assertFalse(ref == ref2)
	}
}
