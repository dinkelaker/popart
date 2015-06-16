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
package de.tud.stg.popart.dslsupport.sql.dspcl

import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;
import de.tud.stg.popart.dslsupport.sql.dsjpm.SelectJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference;
import de.tud.stg.popart.dslsupport.sql.model.ColumnReference;
import static de.tud.stg.popart.dslsupport.sql.model.AnyColumnReference.ANY;

/**
 * Pointcut for select joinpoints
 * @see SQLPointcutInterpreter#pselect(java.util.List)
 */
class SelectPCD extends Pointcut {
	List<ColumnReference> selectList = []
	
	SelectPCD() {
		super("pselect")
	}
	
	SelectPCD(List<ColumnReference> selectList) {
		super("pselect(selectList)")
		this.selectList = selectList
	}
	
	@Override
	public boolean match(JoinPoint jp) {
		if (!(jp instanceof SelectJoinPoint)) return false
		def selectJp = jp as SelectJoinPoint
		if (selectList.contains(ANY)) {
			return selectJp.query.selectList.containsAll(selectList - [ANY]);
		} else {
			return selectJp.query.selectList.containsAll(selectList);
			/*
			//-DINKELAKER-2011-04-04-BEGIN
			//Refactoring: the selection of the columns from the pointcut is used to filter the queries 
			// (pointcut columns must be subset of queries tables)  
			//The two lists are permutations of each other.
			return selectJp.query.selectList.containsAll(selectList) && selectList.containsAll(selectJp.query.selectList);
		    //-DINKELAKER-2011-04-04-END
			*/
		}
	}
}
