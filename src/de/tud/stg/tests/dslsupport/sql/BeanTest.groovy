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

import de.tud.stg.tests.dslsupport.sql.testconnections.TestJDBCConnection;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.LinkedList;
import de.tud.stg.popart.dslsupport.sql.model.*;
import de.tud.stg.popart.dslsupport.sql.*;
import de.tud.stg.tests.dslsupport.sql.beanExample.* ;
import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test checks, that the ORM methods of the 
 * {@link PersonBean} class are working correctly.
 */
public class BeanTest extends JDBCTest {
	private ISimpleSQL sqlinterpreter;

	@org.junit.Before
	public void before() {
		sqlinterpreter = new SimpleSQL(testdb) ;
		PersonBean.setSQLInterpreter(sqlinterpreter);
	}

	@org.junit.Test
	public void test_PersonBean_ormFindByPrimaryKey() {
		PersonBean resultPerson = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("Watts", resultPerson.lastname);
		assertEquals("Neve",resultPerson.forename);
		assertEquals(70, resultPerson.age);
	}
	
	@org.junit.Test
	public void test_PersonBean_ormCreate() {
		PersonBean person = new PersonBean(new PersonKey(100),"John", "Doe", 33);
		person.ormCreate();
		List<Map> query = testdb.executeQuery("SELECT * FROM persons WHERE id = 100");
		assertEquals(1,query.size);
		assertEquals(person.personKey.id, query[0].id);
		assertEquals(person.forename, query[0].forename);
		assertEquals(person.lastname, query[0].lastname);
		assertEquals(person.age, query[0].age);
	}
	
	@org.junit.Test
	public void test_PersonBean_ormStore() {
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		person.lastname = "Doe";
		person.forename = "John";
		person.age = 100;
		person.ormStore();
		List<Map> query = testdb.executeQuery("SELECT * FROM persons WHERE id = 1");
		assertEquals(1,query.size);
		assertEquals(1, query[0].id);
		assertEquals("John", query[0].forename);
		assertEquals("Doe", query[0].lastname);
		assertEquals(100, query[0].age);
	}
	
	@org.junit.Test
	public void test_PersonBean_ormDelete() {
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		person.ormRemove();
		List<Map> query = testdb.executeQuery("SELECT * FROM persons WHERE id = 1");
		assertTrue(query.isEmpty());
	}
}
