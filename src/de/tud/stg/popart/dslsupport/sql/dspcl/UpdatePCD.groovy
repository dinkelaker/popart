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

import de.tud.stg.popart.dslsupport.sql.dsjpm.UpdateJoinPoint;
import de.tud.stg.popart.dslsupport.sql.model.QualifiedName;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * Poincut for update joinpoints
 * @see SQLPointcutInterpreter#pupdate()
 */
class UpdatePCD extends Pointcut {
	QualifiedName tableName;
	
	public UpdatePCD() {
		super("pupdate");
	}
	
	public UpdatePCD(QualifiedName table) {
		super("pupdate(table)");
		this.tableName = table;
	}
	
	@Override
	public boolean match(JoinPoint jp) {
		if (!(jp instanceof UpdateJoinPoint)) return false;
		def updateJp = jp as UpdateJoinPoint;
		if (tableName == null) return true;
		return tableName == updateJp.updateStatement.tableName;
	}
}
