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
package de.tud.stg.popart.dslsupport.sql.model

/**
 * Reference to a table.
 * @see Reference
 */
class TableReference extends Reference {
	
	public TableReference(String qualifier, String name, String alias) {
		super(qualifier, name, alias);
	}
	
	public TableReference(String qualifiedName, String alias) {
		super(qualifiedName, alias);
	}
	
	public TableReference(String qualifiedNameAndAs) {
		super(qualifiedNameAndAs);
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static TableReference Table(String tab) {
		return new TableReference(tab)
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static TableReference Table(String qualified, String alias) {
		return new TableReference(qualified, alias)
	}
	
	/**
	 * Meant to be statically imported, to clean up code
	 */
	public static TableReference Table(String qualifier, String name, String alias) {
		return new TableReference(qualifier, name, alias) ;
	}
}
