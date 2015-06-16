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
package de.tud.stg.popart.aspect.extensions.itd.declarations;

import groovy.lang.MetaMethod;

/**
 * This interface describes a method as a declared element
 * @author Joscha
 */
public interface MethodDeclaration extends Declaration {
	/**
	 * returns the meta method matching the given arguments
	 * (since one method can declare multiple meta methods for reasons
	 * like default parameters etc.)
	 * @param args the arguments
	 * @return the matching meta method
	 */
	public MetaMethod getDeclaredMetaMethod(Class<?>[] args);
}
