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
package de.tud.stg.popart.aspect.extensions.itd.locations;

/**
 * A structure location is some accessed point within the
 * program structure at runtime. For example, a property name
 * at a specific object.
 * @author Joscha Drechsler
 */
/*
 * TODO the information represented by structure locations actually is the
 * same as the static parts of join points. Once the JoinPoint hierarchy is
 * refactored and includes explicit static information sets, the
 * StructureLocation hierarchy should be removed an replaced by the
 * the join points static parts.
 */
public interface StructureLocation {
	public Class<?> getTargetClass();
}
