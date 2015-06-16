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
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import de.tud.stg.popart.dslsupport.sql.model.*;
import de.tud.stg.popart.dslsupport.sql.*;
import de.tud.stg.popart.dslsupport.sql.dsadvl.*;
import de.tud.stg.popart.dslsupport.sql.dspcl.*;
import de.tud.stg.popart.aspect.extensions.itd.*;
import de.tud.stg.popart.aspect.*;
import de.tud.stg.tests.dslsupport.sql.beanExample.* ;
import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation;
import de.tud.stg.popart.aspect.extensions.Booter;
import org.junit.*;

import static org.junit.Assert.*;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.*;
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.*;
import static de.tud.stg.popart.dslsupport.sql.model.bool.BinaryBooleanTerm.*;

/**
 * This test demonstrates, how an aspect can be used to add
 * validation code for specific SQL statements.
 */
public class ValidatingSQLQueriesTest extends JDBCTest {
	private ISimpleSQL sqlinterpreter;
	def sqlaspect;
	String queryInsertCityZips;
	String queryCreateCityZipTable;
	String queryCreatePersonTable;
	String queryInsertPersons;

	@org.junit.Before
	public void before() {
		if (!Booter.isAspectSystemInitialized()) Booter.initialize();
		
		AspectManager.getInstance().unregisterAllAspects();
		
		sqlinterpreter = new SimpleSQL(testdb) ;
		PersonBean.setSQLInterpreter(sqlinterpreter);
		def sqlpcdsl = SQLPointcutInterpreter.getInstance()
		def sqlccc = new CCCombiner([sqlinterpreter,sqlpcdsl]);
		
		sqlaspect = { HashMap params, Closure body ->
			Aspect result = sqlccc.eval(params,body);
			AspectManager.getInstance().register(result);
			return result;
		}
	}
	
	@org.junit.Test
	public void validatingAspectTest() {
		
		/* This aspect validates inserted city/zip pairs against a
		 * zip code table. (Plausibility check of semantic data)
		 */
		def mySqlAspect = sqlaspect(name:"ValidatingSQLQueriesTest-aspect1"){
			around(pinsert(persons)){
				InsertStatement insert = thisJoinPoint.insertStatement;
				def values = insert.values;
				if(values.keySet().containsAll(["city","zip"])){
						def result = selectFromWhere([city,zip],[cityzip],
							WHERE(AND([EQUALS(city,"${values.city}"),EQUALS("zip", "${values.zip}")] as Set)))
					if(result.isEmpty()) throw new Exception("Constraint violated");
				}
				proceed();
			}
		}
		
		//This closure contains a valid city/zip pair
		Closure goodInsert = {
			insertIntoValues(persons, [
				lastname:"'Marley'",
				forename:"'Bob'",
				age: "50",
				street : "'reggae St 1'",
				city : "'Sheffield'",
				zip : "'49448'"]);
		};
		goodInsert.delegate = sqlinterpreter;
		
		//This closure contains an invalid city/zip pair
		Closure badInsert = {
			insertIntoValues(persons, [
				lastname:"'Doe'",
				forename:"'John'",
				age: "30",
				street : "'Doe St 1'",
				city : "'Springfield'",
				zip : "'11111'"]);
		};
		badInsert.delegate = sqlinterpreter;
		
		goodInsert();
		def result = testdb.executeQuery("SELECT lastname,forename FROM persons WHERE lastname='Marley' AND forename='Bob'");
		//Bob Marley should be inserted into the database.
		assertFalse(result.isEmpty());
		
		try{
			badInsert();
			fail("The bad insert statement should result in an exception!");
		}catch(Exception e){
			//This exception is expected by the test.	
		}
		result = testdb.executeQuery("SELECT lastname,forename FROM persons WHERE lastname='Doe' AND forename='John'");
		//John Doe should not be inserted into the database.
		assertTrue(result.isEmpty());
	}
	
}
