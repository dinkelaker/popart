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

import org.junit.Test;

import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;

import static org.junit.Assert.*;

class QualifiedNameTest {
	@Test
	void testEqualsName() {
		def qn = new QualifiedName("name")
		def qn2 = new QualifiedName("name")
		assertTrue(qn == qn2)
	}
	
	@Test
	void testNotEqualsName() {
		def qn = new QualifiedName("name")
		def qn2 = new QualifiedName("foo")
		assertFalse(qn == qn2)
	}
	
	@Test
	void testEqualsQualifiedName() {
		def qn = new QualifiedName("qualifier.name")
		def qn2 = new QualifiedName("qualifier.name")
		assertTrue(qn == qn2)
	}
	
	@Test
	void testNotEqualsQualifiedName() {
		def qn = new QualifiedName("qualifier.name")
		def qn2 = new QualifiedName("foo.name")
		assertFalse(qn == qn2)
	}
}
