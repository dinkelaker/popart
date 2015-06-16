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
package de.tud.stg.tests.instrumentation.core

/**
 * Mostly for Groovy 1.5 and 1.6 compatible tests.
 * In Groovy 1.6, the HandleMetaClass acts as a delegator to the actual metaclass.
 * Therefore, just using <code>foo.metaClass instanceof SomeMetaClass</code> will fail.
 * And <code>if (foo.metaClass instanceof HandleMetaClass)</code> to switch between the version will fail, too.
 * Because there is no "HandleMetaClass" in Groovy 1.5, leading to a "UnknownConstant" error or something similar.
 * @author Jan Stolzenburg
 */
public class Util {
	
	/**
	 * <code>isMetaclassInstanceOf(testedMetaClass, wantedMetaClass)</code>
	 * is similar to:
	 * <code>testedMetaClass instanceof wantedMetaClass</code>
	 */
	static boolean isMetaclassInstanceOf(def testedMetaClass, def wantedMetaClassClass) {
		if (testedMetaClass.getClass().canonicalName == 'org.codehaus.groovy.runtime.HandleMetaClass') {
			return wantedMetaClassClass.isAssignableFrom(testedMetaClass.delegate.class)
		} else {
			return wantedMetaClassClass.isAssignableFrom(testedMetaClass.getClass())
		}
	}
}