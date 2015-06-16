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
package de.tud.stg.tests.dslsupport.sql;

import org.junit.*;

import de.tud.stg.popart.dslsupport.sql.ListHelper;
import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.popart.dslsupport.sql.model.TableReference;
import de.tud.stg.popart.dslsupport.sql.model.ColumnReference;
import static org.junit.Assert.*;

class ListHelperTest {
	
	@Test
	public void testColumnList(){
		def testList = [new ColumnReference("col1"),
			new Identifier("col2")];
		def result = ListHelper.convertToColumnReferenceList(testList);
		assertTrue(result.every{it instanceof ColumnReference});
	}
		
	@Test(expected=AssertionError)
	public void testColumnListWithWrongType(){
		def testList = [new ColumnReference("col1"),
			new Identifier("col2"), "col3"];
		ListHelper.convertToColumnReferenceList(testList);
	}
	
	@Test
	public void testTableList(){
		def testList = [new TableReference("table"),
			new Identifier("table2")];
		def result = ListHelper.convertToTableReferenceList(testList);
		assertTrue(result.every{it instanceof TableReference});
	}
		
	@Test(expected=AssertionError)
	public void testTableListWithWrongType(){
		def testList = [new TableReference("table1"),
			new Identifier("table2"), "table3"];
		ListHelper.convertToTableReferenceList(testList);
	}
}
