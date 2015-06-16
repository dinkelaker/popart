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

import de.tud.stg.tests.dslsupport.sql.beanExample.AddressPersonBean;
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
import org.junit.*;
import static org.junit.Assert.*;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference.*;

/**
 * This Benchmark test compares the execution times of three different solution for the implementation
 * of a PersonBean with address support. The direct approach is a
 * implementation as a Groovy class {@link AddressPersonBean}. The
 * AOP approach uses AOP to extend the existing {@link PersonBean}.
 * The third approach is the {@link ModifiablePersonBean} class,
 * which can store additional values in a map. 
 */
public class BenchmarkTest extends JDBCTest {
	private ISimpleSQL sqlinterpreter;
	def aspect;
	def sqlaspect;
	def isModifiableBeanModified = false 

	@org.junit.Before
	public void before() {
		if (!Booter.isAspectSystemInitialized()) Booter.initialize();
		
		AspectManager.getInstance().unregisterAllAspects();
		
		if (!isModifiableBeanModified) {
			ModifiablePersonBean.additionalFields = ["street", "zip", "city"]
		}
		
		sqlinterpreter = new SimpleSQL(testdb) ;
		PersonBean.setSQLInterpreter(sqlinterpreter);
		AddressPersonBean.setSQLInterpreter(sqlinterpreter);
		ModifiablePersonBean.setSQLInterpreter(sqlinterpreter);
		def sqlpcdsl = SQLPointcutInterpreter.getInstance();
		def sqladvl = new SQLAdviceInterpreter();
		def itdccc = new ITDCCCombiner([sqladvl,sqlinterpreter,sqlpcdsl] as Set);
		sqladvl.setCcc(itdccc);
		
		AspectManager.instance.unregisterAllAspects();
		
		aspect = { HashMap params, Closure body ->
			Aspect result = itdccc.eval(params,body);
			AspectManager.getInstance().register(result);
			return result;
		}
	}
	
	@After
	public void unregisterAspects(){
		AspectManager.getInstance().unregisterAllAspects();
	}

	/**
	 * Checks, that the AOP implementation is working.
	 */
	@org.junit.Test
	public void testAOPExtendedBean() {
		registerAspect().deploy();
		
		PersonBean person = PersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("Ap #990-7809 Urna. Avenue",person.getStreet());
		assertEquals("32015",person.getZip());
		assertEquals("Sterling Heights",person.getCity());
	}
	
	/**
	 * Checks, that the Groovy only implementation is working.
	 */
	@Test
	public void testGroovyExtendedBean(){
		AddressPersonBean person = AddressPersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("Ap #990-7809 Urna. Avenue",person.getStreet());
		assertEquals("32015",person.getZip());
		assertEquals("Sterling Heights",person.getCity());
	}
	
	@Test
	public void testModifiableBean() {
		def person = ModifiablePersonBean.ormFindByPrimaryKey(new PersonKey(1));
		assertEquals("Ap #990-7809 Urna. Avenue",person.additionalAttributes.street);
		assertEquals("32015",person.additionalAttributes.zip);
		assertEquals("Sterling Heights",person.additionalAttributes.city);
	}
	
	/**
	 * The benchmark. It is measured, how long it takes to load the 50
	 * person entries from the database into {@link PersonBean} instances.
	 * There is a warmup in the beginning to reduce the influence of jvm
	 * optimizations.
	 */
	@Test
	public void benchmark(){
		long starttime, endtime;
		def iterations = 50  //MAX=50 because 50 ids in JDBCTest defined
		def modifiableList = []
		def directList = []
		def aopList = []
		def myAspect = registerAspect();
		
		// Warmup for JVM optimization
		myAspect.deploy();
		starttime = System.currentTimeMillis();
		for (int i = 1; i <= iterations; i++) {
			PersonBean.ormFindByPrimaryKey(new PersonKey(i));
		}
		endtime = System.currentTimeMillis();
		myAspect.undeploy();
			
		starttime = System.currentTimeMillis();
		for (int i = 1; i <= iterations; i++){
			ModifiablePersonBean.ormFindByPrimaryKey(new PersonKey(i));
		}
		endtime = System.currentTimeMillis();
		
		starttime = System.currentTimeMillis();
		for (int i = 1; i <= iterations; i++) {
			AddressPersonBean.ormFindByPrimaryKey(new PersonKey(i));
		}
		endtime = System.currentTimeMillis();
		
		// The benchmark
		
		starttime = System.currentTimeMillis();
		for (int i = 1; i <= iterations; i++) {
			directList.add(AddressPersonBean.ormFindByPrimaryKey(new PersonKey(i)));
		}
		endtime = System.currentTimeMillis();
		println("The direct approach (no AOP, hard-wired) needs ${endtime - starttime} ms");
		
		
		starttime = System.currentTimeMillis();
		for (int i = 1; i <= iterations; i++) {
			modifiableList.add(ModifiablePersonBean.ormFindByPrimaryKey(new PersonKey(i)));
		}
		endtime = System.currentTimeMillis();
		println("The modifiable approach (nop AOP, hashmap) needs ${endtime - starttime} ms");
		
		
		starttime = System.nanoTime()
		myAspect.deploy();
		endtime = System.nanoTime()
		println "Deploying the aspect took ${(endtime - starttime)/1000000} ms"
		
		starttime = System.currentTimeMillis();
		for (int i = 1; i <= iterations; i++) {
			aopList.add(PersonBean.ormFindByPrimaryKey(new PersonKey(i)));
		}
		endtime = System.currentTimeMillis();
		println("The AOP approach needs ${endtime - starttime} ms");
		
		def foo = aopList[0].getStreet();
		
		assertEquals(directList[0].getStreet(),foo);
		assertEquals(foo, modifiableList[0].additionalAttributes.street)
	}
	
	private Aspect registerAspect(){
		return aspect(name:"BeanDirectVSAOPTest_aspect",deployed:false){
			introduce_field(is_type(PersonBean), "street", "")
			
			introduce_method(is_type(PersonBean), "getStreet"){
				return street;
			}
			
			introduce_field(is_type(PersonBean), "zip", "")
			
			introduce_method(is_type(PersonBean), "getZip"){
				return zip;
			}
			
			introduce_field(is_type(PersonBean), "city", "")
			
			introduce_method(is_type(PersonBean), "getCity"){
				return city;
			}
			
			around(method_call("createPersonBean")){
				List<Map> queryResult = thisJoinPoint.args[0];
				PersonBean person = proceed();
				person.street = queryResult[0].street;
				person.zip = queryResult[0].zip;
				person.city = queryResult[0].city;
				return person;
			}
			
			around(pselect([ANY])){
				return proceed_select([ANY,street,zip,city]);
			}
		}
	}
	
}
