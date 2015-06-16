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

import org.junit.*;
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.itd.ITDCCCombiner;
import de.tud.stg.popart.dslsupport.sql.*;
import de.tud.stg.popart.dslsupport.sql.model.*;
import de.tud.stg.tests.dslsupport.sql.beanExample.*;
import de.tud.stg.popart.dslsupport.sql.dsadvl.SQLAdviceInterpreter;
import de.tud.stg.popart.dslsupport.sql.dspcl.SQLPointcutInterpreter;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.*;
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.*;
import static de.tud.stg.popart.dslsupport.sql.model.bool.BinaryBooleanTerm.*;
import static org.junit.Assert.*;

import static de.tud.stg.popart.dslsupport.sql.model.TableReference.*;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.*;

import de.tud.stg.tests.dslsupport.sql.*;

/**
 * Demonstrates what can be done with the SimpleSQL implementation
 */
public class SQLLoggingTest extends JDBCTest {
	private ISimpleSQL sqlinterpreter;
	def aspect;
	def SQL;
	Aspect beanAspect;
	
	String log;
	
	@Before
	public void setup(){
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
		
		SQL = {Closure body -> 
			body.delegate = sqlinterpreter;
			return body();
		}
		
		def myLoggingSqlAspect = aspect(name:"myLoggingSqlAspect"){
			before(pSELECT()){
				Query query = thisJoinPoint.query;
				log += "LOG(all): "+query+"\n";				
			}

			before(pSELECT() & pFROM([persons])) {
				Query query = thisJoinPoint.query;
				log += "LOG(persons): "+query+"\n";				
			}
		}
		
		log = "";
	}
	
	@After
	public void tearDown() {
		print log;
	}
	
	@Test
	public void selectQuery(){
		
	}
	
	@Test
	/**
	 * Test that the fields (e.g. id,lastname,forename) tables (e.g. persons) names are resolved via SimpleSQL.propertyMissing.
	 */
	public void NameWildcardQuery(){
		log += "---------------------\n";
		def result = SQL{
			selectFromWhere([id,lastname,forename],[persons],WHERE(LIKE(lastname, "'N%'")))
		};
		result.each { log += it.toString()+"\n"; };
		assert(log.contains("LOG(all)"));
		assert(log.contains("LOG(persons)"));
	}

	@Test
	public void selectCars(){
		log += "---------------------\n";
		def result = SQL{
			selectFrom([producer,modell],[cars])
		};
		result.each { log += (it.values().join(","))+"\n" };
		assert(log.contains("LOG(all)"));
		assert(!log.contains("LOG(persons)"));
	}	
	
	@Test
	public void joinPersonsAndCars(){
		log += "---------------------\n";
		def result = SQL{
			selectFromWhere([lastname,forename,producer,modell],[persons,cars],
				WHERE(EQUALS(new QualifiedName("persons.id"),driverid)))
		};
		result.each { log += (it.values().join(","))+"\n" };
		assert(log.contains("LOG(all)"));
		assert(log.contains("LOG(persons)"));
	}
	
	@Test
	public void insertANewCar(){
		log += "---------------------\n";
		SQL{ insertIntoValues(cars,[driverid,producer,modell,buildyear,km],
			[1,"'Ford'","'Mustang'",2010,0]) };
		def result = SQL{ selectFromWhere([driverid,producer,modell],
			[cars],WHERE(EQUALS(driverid,1)))}
		assertNotNull(result.find { it.modell == "Mustang" });
		assert(log.contains("LOG(all)"));
		assert(!log.contains("LOG(persons)"));
	}
	
}
