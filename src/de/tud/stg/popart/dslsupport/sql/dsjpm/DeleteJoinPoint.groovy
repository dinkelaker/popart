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
package de.tud.stg.popart.dslsupport.sql.dsjpm;

import java.util.Map;
import de.tud.stg.popart.dslsupport.sql.model.DeleteStatement;

/**
 * Marks the point in the execution when a delete statement is to be executed
 */
public class DeleteJoinPoint extends SQLJoinPoint {
	DeleteStatement deleteStatement;

	public DeleteJoinPoint(String location, Map<String, Object> context) {
		super(location, context);
	}

}
