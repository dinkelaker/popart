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
import java.util.Date;
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
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.*;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.*;

import de.tud.stg.tests.dslsupport.sql.*;

/**
 * This test demonstrates, how profiling and logging of SQL queries
 * can be done with the help of an aspect.
 * Aspect measures time of SQL queries.
 */
public class ProfilingAppCtxtSQLTest extends JDBCTest{
	private ISimpleSQL sqlinterpreter;
	def sqlaspect;
	
	public class LogRecord{
		Query query;
		Date pointOfTime;
		long executionTime;
		@Override
		public String toString() {
			return pointOfTime.toString() +": " + query + " --- " + executionTime + "ms"; 
		}
	}

	@org.junit.Before
	public void before() {
		if (!Booter.isAspectSystemInitialized()) Booter.initialize();
		
		AspectManager.getInstance().unregisterAllAspects();
		
		sqlinterpreter = new SimpleSQL(testdb) ;
		def sqlpcdsl = SQLPointcutInterpreter.getInstance()
		def sqlccc = new CCCombiner([sqlinterpreter,sqlpcdsl]);
		
		sqlaspect = { HashMap params, Closure body ->
			Aspect result = sqlccc.eval(params,body);
			AspectManager.getInstance().register(result);
			return result;
		}
	}
	

	@org.junit.Test
	public void profilingMeasuringWithAnAspect() {
		List<LogRecord> log = [];
		
		def mySqlAspect = sqlaspect(name:"ProfilingWithSQLTest-aspect1"){
			//around(pselect() ){
			around( ( pSELECT() | pINSERT() | 
			          pUPDATE() | pDELETE() ) & 
			          cflow(method_execution("method2.*")) &
		              not(cflow(method_execution("method1.*"))) ) {
				//System.out.println(thisJoinPoint.context.toString());
				long starttime = System.currentTimeMillis();
				def output = proceed();
				long endtime = System.currentTimeMillis();
				Query query = thisJoinPoint.query;
				log.add(new LogRecord(query:query, pointOfTime: new Date(), executionTime:endtime - starttime));
				return output;
			}
		}
		
		A a = new A(sqlinterpreter);

		('A'..'Z').each{
			a.method2(it);
		}
		log.each { println it }
		assert(log.size() == 26)
		
		log = []

		('A'..'Z').each{
			a.method1(it);
		}
		log.each { println it }
		assert(log.size() == 0)
	}
}

public class A {
	
	private ISimpleSQL sqlinterpreter;
	
	public A(ISimpleSQL _sqlinterpreter) {
		sqlinterpreter = _sqlinterpreter;
	}
	
	public void method1(String x) {
		method2(x);
	}

	public void method2(String x) {
		Closure cl = {
		    selectFromWhere([lastname,forename,city,zip], [persons], WHERE(LIKE(lastname,"'${x}%'")));
		}
		cl.delegate = sqlinterpreter;
        cl();
	}
}
