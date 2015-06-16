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

import de.tud.stg.popart.dslsupport.sql.SimpleSQL;

/**
 * This class is an identifier as it is defined in the SQL BNF. It represents
 * a simple name without qualification. With help of the <code>asType</code> 
 * method, it can be converted to {@link QualifiedName}, {@link ColumnReference}
 * and {@link TableReference}. This mechanism is used to resolve column and
 * table names, which are written as identifier names in the Groovy code.
 * @see SimpleSQL
 */
class Identifier {
	String string
	
	public Identifier(String string) {
		this.string = string
	}
	
	public String toString() {
		return string
	}
	
	public Object asType(Class clazz) {
		def obj = null
		switch (clazz) {
			case QualifiedName:
				obj =  new QualifiedName(string)
				break
				
			case ColumnReference:
				obj = new ColumnReference(string)
				break
				
			case TableReference:
				obj = new TableReference(string)
				break
				
			case String:
				obj = this.toString()
				break
		}
		
		return obj
	}
}
