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
package de.tud.stg.popart.aspect;

public interface Constants {

	/* JoinPointShadow Type IDs */
	
	final int SHADOW_TYPE_METHOD_CALL = 0;
	final int SHADOW_TYPE_SERVICE_CALL = 1;
	final int SHADOW_TYPE_SERVICE_SELECTION = 2;
	
	/* JoinPointShadow Cast Position */
    final int SHADOW_POSITION_BEFORE = 100;
    final int SHADOW_POSITION_AFTER = 101;
	
	/* JoinPoint IDs */
	
	final String JOIN_POINT_TYPE_SERVICE_CALL = "service_call_jp";
	final String JOIN_POINT_TYPE_SERVICE_SELECTION = "service_selection_jp";

	/* JoinPoint Names */
	final String JOIN_POINT_NAME_SERVICE_CALL = "service_call";
	final String JOIN_POINT_NAME_SERVICE_SELECTION = "service_selection";

	
}
