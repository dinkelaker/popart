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

import groovy.lang.Closure;

import java.util.Set;

import de.tud.stg.popart.dslsupport.*;
import de.tud.stg.popart.aspect.extensions.RuleBasedCCCombiner;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;
import de.tud.stg.popart.aspect.*;

/**
 * This class extends the default Cross cutting combiner by an
 * additional inter type declaration domain specific language interpreter
 * and introduces the "introduce" keyword.
 * @author Joscha Drechsler
 */
public class ITDCCCombiner extends RuleBasedCCCombiner{
	/**
	 * the default InterTypeDeclarationDSL.
	 */
	public static StructuralPointcutDSL defaultStructuralPointcutDSL = new StructuralPointcutDSL(); 
	
	/**
	 * the InterTypeDeclarationDSL used.
	 */
	protected StructuralPointcutDSL structuralPointcutDSL;
	
	private final static boolean DEBUG = false;
	
	/**
	 * default constructor
	 */
	public ITDCCCombiner(){
		super(CCCombiner.defaultAdviceDSL, defaultStructuralPointcutDSL);
		structuralPointcutDSL = defaultStructuralPointcutDSL;
		//setCombinerAsBodyDelegateOfAllInterpreters();
	}
	
	/**
	 * constructor with custom DSL interpeters for pointcut, advice and ITD.
	 * @param adviceDSL the AdviceDSL to use
	 * @param pointcutDSL the PointcutDSL to use
	 * @param ITDDSL the InterTypeDeclarationDSL to use
	 */
	public ITDCCCombiner(AdviceDSL adviceDSL, StructuralPointcutDSL structuralPointcutDSL){
		super(adviceDSL, structuralPointcutDSL);
		this.structuralPointcutDSL = structuralPointcutDSL;
		//setCombinerAsBodyDelegateOfAllInterpreters();
	}
	
	/**
	 * constructor with custom DSL interpreters.
	 * @param dsls a List of DSLs to use.
	 */
	public ITDCCCombiner(Set<DSL> dsls){
		super(dsls);
		structuralPointcutDSL = defaultStructuralPointcutDSL;
		if(!dslDefinitions.contains(structuralPointcutDSL)) dslDefinitions.add(structuralPointcutDSL);
		//setCombinerAsBodyDelegateOfAllInterpreters();
	}
	
	/**
	 * associates a new InterTypePropertyDeclaration with the current Aspect.
	 * Note, that the property values will be stored using weak references.
	 * Thus, if the value of a property references the Object, for which it is
	 * set, this Object will never be garbage collected which leads to memory
	 * leaks, since theoretically, by iterating over the map, you could
	 * re-retrieve a strong Object reference from its properties value.
	 * @param pattern the StructureDesignator definining the set of objects
	 * 		to match
	 * @param propertyName the properties name
	 * @param initialValue the properties initial value
	 */
	public void introduce_field(StructureDesignator pattern, String propertyName, Object initialValue){
		if(DEBUG) System.out.println("ITDCCCombiner: introducing property: "+propertyName+" = "+initialValue);
		if(DEBUG) System.out.println("ITDCCCombiner: pattern = "+pattern);

		if(!(getCurrentAspect() instanceof InterTypeDeclarationAspect)){
			throw new RuntimeException("Cannot add inter type declarations to aspect "+getCurrentAspect()+". Maybe the wrong AspectFactory is set?");
		}
		((InterTypeDeclarationAspect)getCurrentAspect()).addPropertyIntroduction(pattern, propertyName, initialValue);
	}
	
	/**
	 * Deprecated alias of {@link #introduce_field(StructureDesignator, String, Object)}
	 * @param pattern the pattern
	 * @param propertyName the property name
	 * @param initialValue the initial value
	 */
	@Deprecated
	public void introduce(StructureDesignator pattern, String propertyName, Object initialValue){
		introduce_field(pattern, propertyName, initialValue);
	}
	
	/**
	 * associates a new InterTypeMethodDeclaration with the current Aspect.
	 * @param pattern the StructureDesignator definining the set of objects
	 * 		to match
	 * @param methodName the methods name
	 * @param method the closure representing the methods code
	 */
	public void introduce_method(StructureDesignator pattern, String methodName, Closure method){
		if(DEBUG) System.out.println("ITDCCCombiner: introducing method: "+methodName+"("+java.util.Arrays.toString(method.getParameterTypes())+") = "+method);
		if(DEBUG) System.out.println("ITDCCCombiner: pattern = "+pattern);

		if(!(getCurrentAspect() instanceof InterTypeDeclarationAspect)){
			throw new RuntimeException("Cannot add inter type declarations to aspect "+getCurrentAspect()+". Maybe the wrong AspectFactory is set?");
		}
		((InterTypeDeclarationAspect)getCurrentAspect()).addMethodIntroduction(pattern, methodName, method);
	}
}
