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
package de.tud.stg.tests.dslsupport.sql.beanExample;

import java.util.List;

import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.dslsupport.sql.model.SetClause;
import de.tud.stg.popart.dslsupport.sql.model.WhereClause;
import de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate;
import de.tud.stg.popart.dslsupport.sql.ISimpleSQL;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.Col;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.Table;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.*;
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.*;
import static de.tud.stg.popart.dslsupport.sql.model.bool.BinaryBooleanTerm.*;

/**
 * This is a sample implementation of bean, which stores forename,
 * lastname and age of an person. It uses the SQL DSL to retrieve and
 * persist itself to the database. The class is used by several testcases
 * to demonstrate the features of the SQL DSL.
 */
public class PersonBean {
	PersonKey personKey;
	String forename;
	String lastname;
	int age;
	private static ISimpleSQL sqlInterpreter;
	
	public PersonBean(){
		super();
	}
	
	public PersonBean(PersonKey personKey, String forename, String lastname,
			int age) {
		super();
		this.personKey = personKey;
		this.forename = forename;
		this.lastname = lastname;
		this.age = age;
	}
			
	public static PersonBean createPersonBean(List<Map> queryResult){
		return new PersonBean(new PersonKey(queryResult[0].id), queryResult[0].forename,
			queryResult[0].lastname, queryResult[0].age);
	}
			
	public static void setSQLInterpreter(ISimpleSQL iSimpleSQL){
		sqlInterpreter = iSimpleSQL;
	}
	
	public void ormCreate() {
		Closure cl = {insertIntoValues("persons", ["id":personKey.id, Forename:"'${forename}'", Lastname:"'${lastname}'", Age:age])}
		cl.delegate = sqlInterpreter
		cl()
	}
	
	public void ormRemove() {
		Closure cl = {deleteFromWhere("persons", WHERE(EQUALS("id", personKey.id)))}
		cl.delegate = sqlInterpreter
		cl()
	}
	
	public void ormStore(){
		Closure cl = {updateSetWhere("persons", 
			[new SetClause(new Identifier("lastname"), "'${lastname}'"),
				new SetClause(new Identifier("forename"), "'${forename}'"),
				new SetClause(new Identifier("age"), "$age"),],
			WHERE(EQUALS("id", personKey.id)))
		}
		cl.delegate = sqlInterpreter;
		cl();
	}

	public static PersonBean ormFindByPrimaryKey(PersonKey pkey){
		Closure cl = {selectFromWhere([Col("id"), Col("forename"), Col("lastname"), Col("age")],
				[Table("persons")],
				WHERE(EQUALS("id", "${pkey.id}")))};
		cl.delegate = sqlInterpreter;
		def result = cl();
		return createPersonBean(result);
	}
}
