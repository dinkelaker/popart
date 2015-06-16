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
 * This kind of {@link StructureDesignator} examines the given objects
 * whole super type hierarchy for possible matches
 * @author Joscha Drechsler
 */
public abstract class FullHierarchyStructureDesignator extends StructureDesignator {
	public FullHierarchyStructureDesignator(String name) {
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
		return hasMatchInSuperHierarchy(c);
	}

	/**
	 * This method traverses the whole Super type hierarchy of the given
	 * class recursively, supplying each Class object to
	 * {@link #isDirectMatch(Class)}
	 * @param c the class to begin at
	 * @return <code>true</code> iff there was at least one matching Class
	 * 		object within the hierarchy, for which
	 * 		{@link #isDirectMatch(Class)} returned <code>true</code>
	 */
	private boolean hasMatchInSuperHierarchy(Class<?> c){
		if(c == null) return false;
		if(isDirectMatch(c)) return true;
		for(Type t : c.getGenericInterfaces()){
			if(!(t instanceof Class<?>)) return false;
			if(hasMatchInSuperHierarchy((Class<?>)t)) return true;
		}
		return hasMatchInSuperHierarchy(c.getSuperclass());
	}
}
