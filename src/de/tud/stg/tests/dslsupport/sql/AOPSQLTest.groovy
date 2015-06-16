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
import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.CCCombiner;
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.dslsupport.sql.SimpleSQL;
import de.tud.stg.popart.dslsupport.sql.ISimpleSQL;
import de.tud.stg.popart.dslsupport.sql.dsadvl.SQLAdviceInterpreter;
import de.tud.stg.popart.dslsupport.sql.dspcl.SQLPointcutInterpreter;
import de.tud.stg.tests.dslsupport.sql.testconnections.TestSQLConnection;

/**
 * This is a super class for several tests. It is used to prevent
 * code duplication.
 */
class AOPSQLTest {
	Closure aspect
	ISimpleSQL sqldsl
	CCCombiner ccc;
	TestSQLConnection sqlconnection;
	
	@Before
	void setup() {
		if (!Booter.isAspectSystemInitialized()) Booter.initialize()
		
		sqlconnection = new TestSQLConnection()
		sqldsl = new SimpleSQL(sqlconnection)
		def sqlpcdsl = SQLPointcutInterpreter.getInstance()
		def sqladvl = new SQLAdviceInterpreter();
		//ccc = new CCCombiner([sqldsl, sqlpcdsl])
		ccc = new CCCombiner([sqladvl,sqldsl, sqlpcdsl])
		
		//workaround context problem
		sqladvl.setCCCombiner(ccc);
		
	    aspect = { HashMap params, Closure body ->
	        Aspect result = ccc.eval(params,body);
	        AspectManager.getInstance().register(result);
	        return result;
	    }
	}
	
	@After
	public void tearDown(){
		AspectManager.instance.unregisterAllAspects();
	}

}
