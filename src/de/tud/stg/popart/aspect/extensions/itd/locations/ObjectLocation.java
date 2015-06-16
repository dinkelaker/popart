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
 * This structure location is aware of an actual object that is
 * being accessed.
 * @author Joscha Drechsler
 */
public interface ObjectLocation extends StructureLocation {
	/**
	 * retrieves the accessed object. Note that only the accessed class 
	 * of the target object is used for property selection. Therefore,
	 * any caching functionality is based solely on the class aswell.
	 * If you use a locations target object for anything, you
	 * will need do disable all such caches!
	 * @return the object
	 */
	public Object getObject();
}
