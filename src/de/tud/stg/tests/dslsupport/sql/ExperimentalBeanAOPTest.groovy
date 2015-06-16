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
import java.util.HashSet;
import java.util.LinkedList;
import de.tud.stg.popart.dslsupport.sql.model.*;
import de.tud.stg.popart.dslsupport.sql.*;
import de.tud.stg.popart.dslsupport.sql.dsadvl.*;
import de.tud.stg.popart.dslsupport.sql.dspcl.*;
import de.tud.stg.popart.aspect.extensions.itd.*;
import de.tud.stg.popart.aspect.*;
import de.tud.stg.tests.dslsupport.sql.beanExample.* ;
import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation;
import de.tud.stg.popart.aspect.extensions.Booter;
import org.junit.Test;
import static org.junit.Assert.*;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*;

public class ExperimentalBeanAOPTest {
	private ISQLConnection testdb;
	private ISimpleSQL sqlinterpreter;
	def itdaspect;
	def sqlaspect;

	@org.junit.Before
	public void before() {
		if (!Booter.isAspectSystemInitialized()) Booter.initialize();
		
		AspectManager.getInstance().unregisterAllAspects();
		
		testdb = new TestJDBCConnection() ;
		sqlinterpreter = new SimpleSQL(testdb) ;
		PersonBean.setSQLInterpreter(sqlinterpreter);
		def sqlpcdsl = SQLPointcutInterpreter.getInstance()
		def sqlccc = new CCCombiner([sqlinterpreter,sqlpcdsl]);
		def itdccc = new ITDCCCombiner();
		
		//AspectManager.instance.unregisterAllAspects();
		
		itdaspect = { HashMap params, Closure body ->
			Aspect result = itdccc.eval(params,body);
			AspectManager.getInstance().register(result);
			return result;
		}
		
		sqlaspect = { HashMap params, Closure body ->
			Aspect result = sqlccc.eval(params,body);
			AspectManager.getInstance().register(result);
			return result;
		}
		
		//setup derby database
		String queryCleanup = "DROP TABLE persons" ;
		String queryCreateTable = "CREATE TABLE persons(primarykey INTEGER NOT NULL,lastname VARCHAR(30),forename VARCHAR(30),age INTEGER,job VARCHAR(30), PRIMARY KEY(primarykey))" ;
		String queryInsertSomething = "INSERT INTO persons (primarykey, lastname, forename, age, job) VALUES (1, 'Marley', 'Bob', 65, 'musician')" ;
		try{
			testdb.executeUpdate(queryCleanup);
		}catch(SQLSyntaxErrorException e){
			//ignore it. There is no beanTest table, hence it needn't be dropped.
		}
		testdb.executeUpdate(queryCreateTable);
		testdb.executeUpdate(queryInsertSomething);
	}

	@org.junit.Test
	public void addFieldToBeanTest() {
		itdaspect(name:"BeanAOPTest_itdaspect"){
			introduce_field(is_type(PersonBean), "job", "jobless")
			
			introduce_method(is_type(PersonBean), "getJob"){
				return job;
			}
			
			around(method_call("createPersonBean")){
				List<Map> queryResult = thisJoinPoint.args[0];
				PersonBean person = proceed();
				person.job = queryResult[0].job;
				return person;
			}
		}
		
		def mySqlAspect = sqlaspect(name:"BeanAOPTest_sqlaspect"){
			around(pselect()){
				Query query = thisJoinPoint.query;
				query.selectList.add(Col("job"));
				return proceed();
			}
		}
		
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("musician",person.getJob());
	}
	
	@org.junit.Test
	public void addFieldToBeanTestCFlow() {
		itdaspect(name:"BeanAOPTest_itdaspect"){
			introduce_field(is_type(PersonBean), "job", "jobless")
			
			introduce_method(is_type(PersonBean), "getJob"){
				return job;
			}
			
			around(method_call("createPersonBean")){
				List<Map> queryResult = thisJoinPoint.args[0];
				PersonBean person = proceed();
				person.job = queryResult[0].job;
				return person;
			}
		}
		
		def mySqlAspect = sqlaspect(name:"BeanAOPTest_sqlaspect"){
			around(pselect()){
				Query query = thisJoinPoint.query;
				query.selectList.add(Col("job"));
				return proceed();
			}
		}
		
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("musician",person.getJob());
	}
	
	@org.junit.Test
	public void addFieldToBeanTestToPersonTable() {
		itdaspect(name:"BeanAOPTest_itdaspect"){
			introduce_field(is_type(PersonBean), "job", "jobless")
			
			introduce_method(is_type(PersonBean), "getJob"){
				return job;
			}
			
			around(method_call("createPersonBean")){
				List<Map> queryResult = thisJoinPoint.args[0];
				PersonBean person = proceed();
				person.job = queryResult[0].job;
				return person;
			}
		}
		
		def mySqlAspect = sqlaspect(name:"BeanAOPTest_sqlaspect"){
			around(pselect() & pfrom([Table("persons")])){
				Query query = thisJoinPoint.query;
				query.selectList.add(Col("job"));
				return proceed();
			}
		}
		
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("musician",person.getJob());
	}
	

	
	@org.junit.Test
	public void addRuntimeEvolution() {
		def myITDAspect = itdaspect(name:"BeanAOPTest_itdaspect",deployed:false){
			introduce_field(is_type(PersonBean), "job", "jobless")
			
			introduce_method(is_type(PersonBean), "getJob"){
				return job;
			}
			
			around(method_call("createPersonBean")){
				List<Map> queryResult = thisJoinPoint.args[0];
				PersonBean person = proceed();
				person.job = queryResult[0].job;
				return person;
			}
		}
		
		def mySqlAspect = sqlaspect(name:"BeanAOPTest_sqlaspect",deployed:false){
			around(pselect()){
				Query query = thisJoinPoint.query;
				query.selectList.add(Col("job"));
				return proceed();
			}
		}

		try {
  		    PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		    assertEquals("musician",person.getJob());
		    assert false == "should not be reached -> test did not detect missing field in bean"
		} catch (Exception ex) {
			//correctly detected missing field (because myITDAspect and mySqlAspect not yet deployed)
		}
		
		myITDAspect.deploy();

		try {
  		    PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		    assertEquals("musician",person.getJob());
		    assert false == "should not be reached -> test did not detect missing field in bean"
		} catch (Exception ex) {
			//correctly detected missing field (because mySqlAspect not yet deployed)
		}

		mySqlAspect.deploy();
		
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("musician",person.getJob());
	}
	
	@org.junit.Test
	public void addRuntimeEvolutionStoreAndUpdate() {
		def myITDAspect = itdaspect(name:"BeanAOPTest_itdaspect",deployed:false){
			introduce_field(is_type(PersonBean), "job", "jobless")
			
			introduce_method(is_type(PersonBean), "getJob"){
				return job;
			}
			
			around(method_call("createPersonBean")){
				List<Map> queryResult = thisJoinPoint.args[0];
				PersonBean person = proceed();
				person.job = queryResult[0].job;
				return person;
			}
		}
		
		def mySqlAspect = sqlaspect(name:"BeanAOPTest_sqlaspect",deployed:false){
			around(pselect()){
				Query query = thisJoinPoint.query;
				query.selectList.add(Col("job"));
				return proceed();
			}
		}

		myITDAspect.deploy();
		mySqlAspect.deploy();
		
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("musician",person.getJob());
		person.personKey = new PersonKey(2)
		person.job = "actor";
		person.ormCreate();
		
		PersonBean actor = PersonBean.ormFindByPrimaryKey(new PersonKey(2));
		assertEquals("actor",actor.getJob());
	}
	
	@org.junit.Test
	public void addRuntimeEvolutionStoreAndLoad() {
		def myITDAspect = itdaspect(name:"BeanAOPTest_itdaspect",deployed:false){
			introduce_field(is_type(PersonBean), "job", "jobless")
			
			introduce_method(is_type(PersonBean), "getJob"){
				return job;
			}
			
			introduce_method(is_type(PersonBean), "setJob"){ newJob ->
				delegate.job = newJob;
			}
			
			around(method_call("createPersonBean")){
				List<Map> queryResult = thisJoinPoint.args[0];
				PersonBean person = proceed();
				assert person != null
				assert queryResult != null
				person.job = queryResult[0].job;
				return person;
			}
		}
		
		def mySqlAspect = sqlaspect(name:"BeanAOPTest_sqlaspect",deployed:false){
			around(pselect()){
				Query query = thisJoinPoint.query;
				query.selectList.add(Col("job"));
				return proceed();
			}
		}

		
		PersonBean person = new PersonBean(new PersonKey(2),"Jimmy","Hendrix",68);
		person.ormCreate();

		myITDAspect.deploy();
		mySqlAspect.deploy();
		
		PersonBean jimmy = PersonBean.ormFindByPrimaryKey(new PersonKey(2));
		assert (jimmy.getJob() == "jobless") 
		jimmy.setJob("filmstar")
		
			
		PersonBean actor = PersonBean.ormFindByPrimaryKey(new PersonKey(2));
		assert (actor.getJob() == "filmstar") 
	}
}
