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

import groovy.util.MapEntry;

import java.util.List;
import java.util.HashMap.Entry 

import de.tud.stg.tests.dslsupport.sql.BeanDirectVSAOPTest;
import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.dslsupport.sql.model.SetClause;
import de.tud.stg.popart.dslsupport.sql.ISimpleSQL;
import static de.tud.stg.popart.dslsupport.sql.model.ColumnReference.Col;
import static de.tud.stg.popart.dslsupport.sql.model.TableReference.Table;
import static de.tud.stg.popart.dslsupport.sql.model.bool.ComparisonPredicate.*;
import static de.tud.stg.popart.dslsupport.sql.model.WhereClause.*;
import static de.tud.stg.popart.dslsupport.sql.model.bool.BinaryBooleanTerm.*;

/**
 * Implementation variant: the bean can be modified at runtime without AOP by indirection via a hashmap.
 * This class is an extended reimplementation of the {@link PersonBean}. It
 * uses a Map to store additional values. Therefore, arbitrary additional
 * columns in the database can be stored in this bean. This class is 
 * used in the {@link BeanDirectVSAOPTest}.
 */
public class ModifiablePersonBean {
	PersonKey personKey;
	String forename;
	String lastname;
	int age;
	def additionalAttributes = [:] // store the additional Bean attributes key (fieldName) --> value (fieldValue)
	
	private static final originalAttributes = ["id", "personKey", "forename", "lastname", "age"]
	private static final originalFields = ["id", "forename", "lastname", "age"]
	private static ISimpleSQL sqlInterpreter;
	private static final DEBUG = true
	static additionalFields = [] // new column names in DB
	
	public ModifiablePersonBean(){
		super();
	}
	
	public ModifiablePersonBean(PersonKey personKey, String forename, String lastname,
	int age) {
		super();
		this.personKey = personKey;
		this.forename = forename;
		this.lastname = lastname;
		this.age = age;
	}
	
	public ModifiablePersonBean(PersonKey personKey, String forename, String lastname,
	int age, Map additionalAttributes) {
		super();
		this.personKey = personKey;
		this.forename = forename;
		this.lastname = lastname;
		this.age = age;
		this.additionalAttributes = additionalAttributes
	}
	
	public static ModifiablePersonBean createModifiablePersonBean(List<Map> queryResult){
		def result = queryResult[0]
		def newAttributes = result.findAll { !originalAttributes.contains(it.key) }
		return new ModifiablePersonBean(new PersonKey(result["id"]), result["forename"],
		result["lastname"], result["age"], newAttributes);
	}
	
	public static void setSQLInterpreter(ISimpleSQL iSimpleSQL){
		sqlInterpreter = iSimpleSQL;
	}
	
	private Map getAdditionalFieldValueMap() {
		def additionalFieldValueMap = [:]
		for (field in additionalFields) {
			additionalFieldValueMap.put(field, additionalAttributes[field])
		}
		return additionalFieldValueMap
	}
	
	public void ormCreate() {
		Closure cl = {
			insertIntoValues("Persons", ["id":personKey.id, Forename:"'${forename}'", Lastname:"'${lastname}'", Age:age] + getAdditionalFieldValueMap())
		}
		cl.delegate = sqlInterpreter
		cl()
	}
	
	public void ormRemove() {
		Closure cl = {deleteFromWhere("Persons", WHERE(EQUALS("id", personKey.id)))}
		cl.delegate = sqlInterpreter
		cl()
	}
	
	public void ormStore(){
		Closure cl = {
			def newClauses = getAdditionalFieldValueMap().collect {
				new SetClause(new Identifier(it.key), it.value)
			}
			updateSetWhere("Persons", 
					[
						new SetClause(new Identifier("lastname"), "'${lastname}'"),
						new SetClause(new Identifier("forename"), "'${forename}'"),
						new SetClause(new Identifier("age"), "$age"),
					] + newClauses,
					WHERE(EQUALS("id", personKey.id)))
		}
		cl.delegate = sqlInterpreter;
		cl();
	}
	
	public static ModifiablePersonBean ormFindByPrimaryKey(PersonKey pkey){
		Closure cl = {
			def newCols = additionalFields.collect { Col("p.$it") }
			selectFromWhere([
				Col("p.id"),
				Col("p.forename"),
				Col("p.lastname"),
				Col("p.age")
			] + newCols,
			[Table("persons", "p")],
			WHERE(EQUALS("p.id", "${pkey.id}")))
		};
		cl.delegate = sqlInterpreter;
		def result = cl();
		def lowerCaseFields = result.collect {
			def map = [:]
			for (entry in it) {
				map.put(entry.key.toLowerCase(), entry.value)
			}
			return map
		}
		return createModifiablePersonBean(lowerCaseFields);
	}
}
