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
package de.tud.stg.popart.dslsupport.sql.dspcl;

import de.tud.stg.popart.dslsupport.sql.dsjpm.DeleteJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * Pointcut for delete joinpoints
 * @see SQLPointcutInterpreter#pdelete()
 */
public class DeletePCD extends Pointcut {
	QualifiedName tableName;
	
	public DeletePCD() {
		super("pdelete");
	}
	
	public DeletePCD(QualifiedName table) {
		super("pdelete(table)");
		this.tableName = table;
	}
	
	@Override
	public boolean match(JoinPoint jp) {
		if (!(jp instanceof DeleteJoinPoint)) return false;
		def deleteJp = jp as DeleteJoinPoint;
		if (tableName == null) return true;
		return tableName == deleteJp.deleteStatement.tableName;
	}
}
