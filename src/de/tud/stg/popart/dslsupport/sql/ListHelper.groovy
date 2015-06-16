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
package de.tud.stg.popart.dslsupport.sql;

import de.tud.stg.popart.dslsupport.sql.model.ColumnReference;
import de.tud.stg.popart.dslsupport.sql.model.Identifier;
import de.tud.stg.popart.dslsupport.sql.model.TableReference;

public class ListHelper {

	/**
	 * This method converts instances of {@link Identifier}, which are in
	 * the given list, to {@link ColumnReference}s. Any other types than
	 * Identifier and ColumnReference result in an AssertionError
	 * @param columns a list, that contains any types.
	 * @return a list, that only contains instances of {@link ColumnReference}
	 */
	public static List<ColumnReference> convertToColumnReferenceList(List columns){
		return columns.collect {
			switch(it.class){
				case ColumnReference: return it;
				case Identifier: return it as ColumnReference;
				default: throw new AssertionError();
			}
		};
	}
	
	/**
	* This method converts instances of {@link Identifier}, which are in
	* the given list, to {@link TableReference}s. Any other types than
	* Identifier and TableReference result in an AssertionError
	* @param columns a list, that contains any types.
	* @return a list, that only contains instances of {@link TableReference}
	*/
	public static List<TableReference> convertToTableReferenceList(List tables){
		return tables.collect {
			switch(it.class){
				case TableReference: return it;
				case Identifier: return it as TableReference;
				default: throw new AssertionError();
			}
		};
	}
}
