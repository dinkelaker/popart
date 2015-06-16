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
package de.tud.stg.popart.aspect.extensions.itd;

import de.tud.stg.popart.aspect.extensions.itd.declarations.Declaration;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;

/**
 * This is the central data model class for the Inter Type Declaration
 * System. An instance of this class represents an Introduction of some
 * kind which is valid for all Objects, matching the associated
 * Structural Pattern.<br>
 * Instances of this class will be associated to Aspects and are enabled
 * iff this Aspect is deployed.
 * @author Joscha Drechsler
 */
public abstract class InterTypeDeclaration implements Declaration {
	/**
	 * This field holds the structural pattern which defines the
	 * set of objects, to which this introduction is applied.
	 */
	private StructureDesignator pattern;
	
	
	/**
	 * The {@link InterTypeDeclarationAspect} defining this introduction
	 */
	private InterTypeDeclarationAspect aspect;

	/**
	 * Constructor for subclasses to set the pattern.
	 * @param aspect the aspect defining this introduction
	 * @param pattern the pattern
	 */
	protected InterTypeDeclaration(InterTypeDeclarationAspect aspect, StructureDesignator pattern){
		this.aspect = aspect;
		this.pattern = pattern;
	}

	/**
	 * determines, whether this InterTypeDeclaration affects the
	 * given object
	 * @param o the object
	 * @return true if the object is affected, false otherwise
	 */
	public boolean appliesTo(Object o) {
		return aspect.isDeployed() && aspect.isInScope(o) && pattern.matches(o.getClass());
	}
	
	/**
	 * @return the ITDs stored pattern
	 */
	public StructureDesignator getPattern(){
		return pattern;
	}

	/**
	 * getter for declaring aspect
	 * @return the aspect defining this introduction
	 */
	public InterTypeDeclarationAspect getDeclaringAspect(){
		return aspect;
	}
	
	public Declaration getParent(){
		return aspect;
	}
}
