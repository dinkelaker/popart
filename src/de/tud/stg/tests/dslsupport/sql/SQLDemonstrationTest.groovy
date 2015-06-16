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


/**
 * Demonstrates what can be done with the SimpleSQL implementation
 */
public class SQLDemonstrationTest extends JDBCTest {
	private ISimpleSQL sqlinterpreter;
	def aspect;
	def SQL;
	Aspect beanAspect;
	
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
	}
	
	@Test
	/**
	 * Test that the fields (e.g. id,lastname,forename) tables (e.g. persons) names are resolved via SimpleSQL.propertyMissing.
	 */
	public void NameWildcardQuery(){
		def result = SQL{
			selectFromWhere([id,lastname,forename],[persons],WHERE(LIKE(lastname, "'N%'")))
		};
		result.each {println(it) };
	}
	
	@Test
	public void joinPersonsAndCars(){
		def result = SQL{
			selectFromWhere([lastname,forename,producer,modell],[persons,cars],
				WHERE(EQUALS(new QualifiedName("persons.id"),driverid)))
		};
		result.each { println(it.values().join(",")) };
	}
	
	@Test
	public void insertANewCar(){
		SQL{ insertIntoValues(cars,[driverid,producer,modell,buildyear,km],
			[1,"'Ford'","'Mustang'",2010,0]) };
		def result = SQL{ selectFromWhere([driverid,producer,modell],
			[cars],WHERE(EQUALS(driverid,1)))}
		assertNotNull(result.find { it.modell == "Mustang" });
	}
}
