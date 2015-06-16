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

import de.tud.stg.tests.dslsupport.sql.BeanDirectVSAOPTest;
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
 * Implementation Variant in which the additional field was added by hand.
 * This is an extended reimplementation of the {@link PersonBean}. It has
 * additional fields for street, zip and city. This class is used in the
 * {@link BeanDirectVSAOPTest}
 */
public class AddressPersonBean {
	PersonKey personKey;
	String forename;
	String lastname;
	int age;
	String street;
	String zip;
	String city;
	private static ISimpleSQL sqlInterpreter;
	
	public AddressPersonBean(){
		super();
	}
	
	public AddressPersonBean(PersonKey personKey, String forename, String lastname,
			int age, String street, String zip, String city) {
		super();
		this.personKey = personKey;
		this.forename = forename;
		this.lastname = lastname;
		this.age = age;
		this.street = street;
		this.zip = zip;
		this.city = city;
	}
			
	public static AddressPersonBean createPersonBean(List<Map> queryResult){
		return new AddressPersonBean(new PersonKey(queryResult[0].id), queryResult[0].forename,
			queryResult[0].lastname, queryResult[0].age, queryResult[0].street,
			queryResult[0].zip, queryResult[0].city);
	}
			
	public static void setSQLInterpreter(ISimpleSQL iSimpleSQL){
		sqlInterpreter = iSimpleSQL;
	}
	
	public void ormCreate() {
		Closure cl = {insertIntoValues("Persons", ["id":personKey.id, Forename:"'${forename}'", 
			Lastname:"'${lastname}'", Age:age, street:"'$street'",
			zip:"'$zip'", city:"'$city'"])}
		cl.delegate = sqlInterpreter
		cl()
	}
	
	public void ormRemove() {
		Closure cl = {deleteFromWhere("Persons", WHERE(EQUALS("id", personKey.id)))}
		cl.delegate = sqlInterpreter
		cl()
	}
	
	public void ormStore(){
		Closure cl = {updateSetWhere("Persons", 
			[new SetClause(new Identifier("lastname"), "'${lastname}'"),
				new SetClause(new Identifier("forename"), "'${forename}'"),
				new SetClause(new Identifier("age"), "$age"),
				new SetClause(new Identifier("street"), "'$street'"),
				new SetClause(new Identifier("age"), "'$zip'"),
				new SetClause(new Identifier("age"), "'$city'")],
			WHERE(EQUALS("id", personKey.id)))
		}
		cl.delegate = sqlInterpreter;
		cl();
	}

	public static AddressPersonBean ormFindByPrimaryKey(PersonKey pkey){
		Closure cl = {selectFromWhere([Col("p.id"), Col("p.forename"), Col("p.lastname"), Col("p.age"), Col("p.street"),Col("p.zip"),Col("p.city")],
				[Table("persons", "p")],
				WHERE(EQUALS("p.id", "${pkey.id}")))};
		cl.delegate = sqlInterpreter;
		def result = cl();
		return createPersonBean(result);
	}
}
