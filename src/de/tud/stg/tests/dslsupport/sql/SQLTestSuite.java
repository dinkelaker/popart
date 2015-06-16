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

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tud.stg.tests.dslsupport.sql.advice.ProceedInsertTest;
import de.tud.stg.tests.dslsupport.sql.advice.ProceedSelectTest;
import de.tud.stg.tests.dslsupport.sql.advice.ProceedUpdateTest;
import de.tud.stg.tests.dslsupport.sql.model.FromClauseTest;
import de.tud.stg.tests.dslsupport.sql.model.InsertStatementTest;
import de.tud.stg.tests.dslsupport.sql.model.QualifiedNameTest;
import de.tud.stg.tests.dslsupport.sql.model.QueryTest;
import de.tud.stg.tests.dslsupport.sql.model.ReferenceTest;
import de.tud.stg.tests.dslsupport.sql.pointcut.PDeleteTest;
import de.tud.stg.tests.dslsupport.sql.pointcut.PFromTest;
import de.tud.stg.tests.dslsupport.sql.pointcut.PInsertTest;
import de.tud.stg.tests.dslsupport.sql.pointcut.PSelectTest;
import de.tud.stg.tests.dslsupport.sql.pointcut.PUpdateTest;

/**
 * This is the TestSuite for the sql dsl. To add additional tests just 
 * add the according class to the {@link SuiteClasses} annotation.
 * This class is just the test suite and it is intentionally left empty. 
 */
@RunWith(Suite.class)
@SuiteClasses({FromClauseTest.class, InsertStatementTest.class, QualifiedNameTest.class, 
               QueryTest.class, ReferenceTest.class,
               PFromTest.class, PSelectTest.class, PUpdateTest.class, PInsertTest.class,
               PDeleteTest.class,
               ProceedSelectTest.class, ProceedInsertTest.class, ProceedUpdateTest.class,
               ProfilingWithSQLTest.class, ValidatingSQLQueriesTest.class, BeanAOPTest.class,
               //timing issue with this test. Must be at the end or other tests fail. WHY???
               SQLMetadataRetrievalTest.class, BeanDirectVSAOPTest.class, BeanTest.class})
public class SQLTestSuite { 
	
}


