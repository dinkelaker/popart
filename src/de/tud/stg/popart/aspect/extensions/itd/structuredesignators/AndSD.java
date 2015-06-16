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

/**
 * This StructureDesignator represents the logical "and" concatenation of
 * two StructureDesignators.
 * @author Joscha Drechsler
 */
public class AndSD extends StructureDesignator{
	/**
	 * the concatenated StructureDesignators
	 */
	private StructureDesignator a, b;
	
	/**
	 * Constructor.
	 * @param a the first StructureDesignator
	 * @param b the second StructureDesignator
	 */
	public AndSD(StructureDesignator a, StructureDesignator b){
		super("and("+a+","+b+")");
		this.a=a;
		this.b=b;
	}
	
	/**
	 * Matches if the first and the second StructureDesignator match.
	 */
	public boolean matches(Class<?> c){
		return a.matches(c) && b.matches(c);
	}
}
