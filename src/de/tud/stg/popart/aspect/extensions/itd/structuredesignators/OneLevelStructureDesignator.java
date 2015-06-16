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
package de.tud.stg.popart.aspect.extensions.itd.structuredesignators;

import java.lang.reflect.Type;

/**
 * This {@link StructureDesignator} traverses the given objects class
 * and all its directly implemented interfaces and supplies them to be
 * checked.
 * @author Joscha Drechsler
 */
public abstract class OneLevelStructureDesignator extends StructureDesignator {
	public OneLevelStructureDesignator(String name){
		super(name);
	}
	
	/**
	 * determines, whether the given Class object is a direct match.
	 * @param c the Class object to be checked
	 * @return <code>true</code> iff match
	 */
	protected abstract boolean isDirectMatch(Class<?> c);
	
	/**
	 * see {@link StructureDesignator#matches(Object)}
	 */
	@Override
	public boolean matches(Class<?> c) {
		for(Type t : c.getGenericInterfaces()){
			if(!(t instanceof Class<?>)) return false;
			if(isDirectMatch((Class<?>)t)) return true;
		}
		return isDirectMatch(c);
	}

}
