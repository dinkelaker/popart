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
package de.tud.stg.tests.dslsupport.sql.ramse2011;

import de.tud.stg.tests.dslsupport.sql.testconnections.TestJDBCConnection;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashSet;
import java.util.LinkedList;
import de.tud.stg.popart.dslsupport.sql.model.*;
import de.tud.stg.popart.dslsupport.sql.*;
import de.tud.stg.popart.dslsupport.sql.dsadvl.*;
import de.tud.stg.popart.dslsupport.sql.dspcl.*;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.aspect.extensions.itd.*;
import de.tud.stg.popart.aspect.*;
import de.tud.stg.tests.dslsupport.sql.beanExample.* ;
import de.tud.stg.popart.aspect.extensions.Booter;
import org.junit.*;
import static org.junit.Assert.*;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference.*;

import de.tud.stg.tests.dslsupport.sql.*;

/**
 * The test demonstrates, how a bean can be extended with
 * an additional field (e.g. city) with the help of POPART driven aspects
 * and the SQL DSL. The POPART aspect introduces a new field "city" into the bean
 * and adapts all queries of the bean to also read and write the new city column.
 * The Test assumes that the additional column city has already been made available in 
 * the database. A corresponding field city is added to
 * the {@link PersonBean} class with the help of an aspect.
 */
public class BeanAOPTest extends JDBCTest {
	private ISimpleSQL sqlinterpreter;
	def aspect;
	Aspect beanAspect;

	@org.junit.Before
	public void before() {
		if (!Booter.isAspectSystemInitialized()) Booter.initialize();
		
		AspectManager.getInstance().unregisterAllAspects();
		
		sqlinterpreter = new SimpleSQL(testdb) ;
		PersonBean.setSQLInterpreter(sqlinterpreter);
		def sqlpcdsl = SQLPointcutInterpreter.getInstance();
		def sqldsladvl = new SQLAdviceInterpreter();
		def itdccc = new ITDCCCombiner([sqlinterpreter,sqlpcdsl,sqldsladvl] as Set);
		sqldsladvl.setCcc(itdccc);
		
		aspect = { HashMap params, Closure body ->
			Aspect result = itdccc.eval(params,body);
			AspectManager.getInstance().register(result);
			return result;
		}
		registerAspect();
	}
	
	@After
	public void unregisterAspects(){
		beanAspect.undeploy();
		AspectManager.getInstance().unregisterAllAspects();
	}
	
	private void registerAspect(){
		beanAspect = aspect(name:"BeanAOPTest_aspect",deployed:false){
			introduce_field(is_type(PersonBean), "city", "")
			
			introduce_method(is_type(PersonBean), "getCity"){
				return city;
			}
			
			//ormFindByPrimaryKey --> update the constructor (currently a factory method)
			around(method_call("createPersonBean")){
				List<Map> queryResult = thisJoinPoint.args[0];
				PersonBean person = proceed();
				person.city = queryResult[0].city;
				return person;
			}
			
			//update the SQL query inside ormFindByPrimaryKey
			around(pFROM([persons])){
				proceed_SELECT([ANY,city]);
			}
			
			//ormCreate
			around(pINSERT(persons)){
				def jp = thisJoinPoint;
				//get a reference for the bean from the stack
				List<JoinPoint> l = jp.context.joinPointStack;
				PersonBean person = l.find{it.methodName == "ormCreate"}.context.targetObject;
				proceed_INSERT([city:"'${person.getCity()}'"]); 
			}
			
			//ormStore
			around(pUPDATE(persons)){
				def jp = thisJoinPoint;
				//get a reference for the bean from the stack
				List<JoinPoint> l = jp.context.joinPointStack;
				PersonBean person = l.find{it.methodName == "ormStore"}.context.targetObject;
				//TODO city without the as QualifiedName does not work -> workaround: overload the SetClause constructor
				proceed_UPDATE([new SetClause(city, "'${person.getCity()}'")]);
			}
		}
	}

	@org.junit.Test
	/**
	 * Test findByPrimaryKey + field introduction
	 */
	public void addFieldToBeanTest() {
		beanAspect.deploy();
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("Sterling Heights",person.getCity());
	}
	
	//TODO This test throws a ConcurrentModificationException, when run by the SQLTestSuite
	@Test
	/**
	 * create + introduction
	 */
	public void testCreation(){
		beanAspect.deploy();
		PersonBean person = new PersonBean(new PersonKey(100), "John", "Doe", 11);
		person.city = "Doe-Town";
		person.ormCreate();
		List<Map> result = testdb.executeQuery("SELECT * FROM persons WHERE id = 100");
		assertFalse(result.isEmpty());
		assertEquals(100,result[0].id); //checks if the new created person is actually created in the DB
	}
	
	//TODO This test throws a ConcurrentModificationException, when run by the SQLTestSuite
	@Test
	public void testStoring(){
		beanAspect.deploy();
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		person.city = "Doe-Town";
		person.ormStore();
		List<Map> result = testdb.executeQuery("SELECT * FROM persons WHERE id = 1");
		assertEquals(1,result.size());
		assertEquals("Doe-Town",result[0].city);
	}

	@org.junit.Test
	/**
	 * First tries to read a not yet defined field (which is expected to fail)
	 * then add the field and tries to read it again (which is expected to succeed)
	 */
	public void addRuntimeEvolution() {
		try {
  		    PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		    assertEquals("Sterling Heights",person.getCity());
		    fail("should not be reached -> test did not detect missing field in bean");
		} catch (Exception ex) {
			//correctly detected missing field (because myITDAspect and mySqlAspect not yet deployed)
		}
		
		beanAspect.deploy();
		
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("Sterling Heights",person.getCity());
	}
	
}
