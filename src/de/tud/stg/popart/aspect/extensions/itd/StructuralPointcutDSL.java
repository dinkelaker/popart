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

import de.tud.stg.popart.aspect.PointcutDSL;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.*;
import de.tud.stg.popart.dslsupport.DSLCreator;
import de.tud.stg.popart.dslsupport.Interpreter;
import groovy.lang.Closure;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * This Class declares the Domain specific Language for evaluating
 * Strucutral Patterns in Pointcuts.
 * @author Joscha Drechsler
 */
public class StructuralPointcutDSL extends PointcutDSL {
	public static Interpreter getInterpreter(Map<String,Object> context) {
		return DSLCreator.getInterpreter(new StructuralPointcutDSL(),context);
	}
	
	public static Object eval(Closure dslCode){
		Interpreter dsl = getInterpreter(new java.util.HashMap<String,Object>());
		return dsl.eval(dslCode);
	}
	
	/**
	 * Keyword usage: is_class("String.*");<br>
	 * Matches the given String as a regular expression pattern
	 * against the objects {@link Class#getSimpleName()} using
	 * the ==~ operator to determine matching.
	 */
	public StructureDesignator is_class(String patternString){
		return new ClassPatternSD(patternString);
	}

	/**
	 * Keyword usage: is_class(~/St[inr]+g/);<br>
	 * Matches the given Pattern against the objects
	 * {@link Class#getSimpleName()} using the ==~ operator
	 * to determine Matching.
	 */
	public StructureDesignator is_class(Pattern pattern){
		return new ClassPatternSD(pattern);
	}
	
	/**
	 * Keyword usage: is_class(java.lang.String);<br>
	 * Matches Objects of the given Class only.
	 */
	public StructureDesignator is_class(Class<?> c){
		return new ClassSD(c);
	}

	/*
	 * pclass(x) are convenience duplicates of is_class(x). 
	 */
	public StructureDesignator pclass(String patternString){
		return is_class(patternString);
	}
	
	public StructureDesignator pclass(Pattern pattern){
		return is_class(pattern);
	}
	
	public StructureDesignator pclass(Class<?> aClass){
		return is_class(aClass);
	}
	
	/**
	 * Keyword usage: is_type("Number.*");<br>
	 * Matches the given String as a regular expression pattern
	 * against the objects class and all its superclasses
	 * {@link Class#getSimpleName()} to determine matching, while a
	 * single match suffices.
	 */
	public StructureDesignator is_type(String patternString){
		return new TypePatternSD(patternString);
	}
	
	/**
	 * Keyword usage: is_type(~/Numb.r/);<br>
	 * Matches the given pattern against the objects class and all
	 * superclasses {@link Class#getSimpleName()} to determine matching,
	 * while a single match suffices.
	 */
	public StructureDesignator is_type(Pattern pattern){
		return new TypePatternSD(pattern);
	}

	/**
	 * Keyword usage: is_type(java.lang.Number);<br>
	 * Matches all subclasses of the given class.
	 */
	public StructureDesignator is_type(Class<?> c){
		return new TypeSD(c);
	}
	
	/*
	 * ptype(x) are convenience replacements for is_type(x)
	 */
	public StructureDesignator ptype(String patternString){
		return is_type(patternString);
	}
	public StructureDesignator ptype(Pattern pattern){
		return is_type(pattern);
	}
	public StructureDesignator ptype(Class<?> c){
		return is_type(c);
	}
	
	/**
	 * Keyword usage: within_package(".*swing.*");<br>
	 * Matches the given String as a regular expression against the
	 * objects classes package name using the ==~ operator to determine
	 * matching.<br>
	 * Does not take subpackages into account, so wildcards at the end
	 * of the pattern DO matter. For example:<br>
	 * inPackage("java\\.lang")<br>
	 * instances of java.lang.String: Match.<br>
	 * instances of java.lang.reflect.Field: No Match.<br>
	 * inPackage("java\\.lang.*")<br>
	 * instances of java.lang.String: Match.<br>
	 * instances of java.lang.reflect.Field: Match.<br>
	 */
	public StructureDesignator within_package(String patternString){
		return new PackagePatternSD(patternString);
	}

	/**
	 * Keyword usage: within_package(".*swing.*");<br>
	 * Matches the given Pattern against the objects classes package
	 * name using the ==~ operator to determine matching.<br>
	 * Does not take subpackages into account, so wildcards at the end
	 * of the pattern DO matter. See {@link inPackage(String)} for an
	 * example.
	 */
	public StructureDesignator within_package(Pattern pattern){
		return new PackagePatternSD(pattern);
	}

	/**
	 * Keyword usage examples:<br>
	 *  - within_package(Package.getPackage("java.lang"));<br>
	 *  - within_package(someObject.class.getPackage());<br>
	 * Matches instances of classes of the given java.lang.Package only.
	 * Does not take subpackages into account.
	 */
	public StructureDesignator within_package(Package p){
		return new PackageSD(p);
	}
	
	/**
	 * Keyword usage: within_package_hierarchy(".*swing.*");<br>
	 * Matches the given String as a regular expression against the
	 * objects classes package and all its enclosing packages names using
	 * the ==~ operator. A single match suffices.
	 */
	public StructureDesignator within_package_hierarchy(String patternString){
		return new PackageHierarchyPatternSD(patternString);
	}

	/**
	 * Keyword usage: within_package_hierarchy(~/java\.(.*)\./);<br>
	 * Matches the given Pattern against the objects classes package
	 * and all its enclosing packages names using the ==~ operator.
	 * A single match suffices.
	 */
	public StructureDesignator within_package_hierarchy(Pattern pattern){
		return new PackageHierarchyPatternSD(pattern);
	}

	/**
	 * Keyword usage examples:<br>
	 *  - within_package_hierarchy(Package.getPackage("java.lang"));<br>
	 *  - within_package_hierarchy(someObject.class.getPackage());<br>
	 * Matches instances of classes of the given java.lang.Package or
	 * any subpackage.
	 */
	public StructureDesignator within_package_hierarchy(Package p){
		return new PackageHierarchySD(p);
	}

	/**
	 * Keyword usage: Canonical_name("java.*String");<br>
	 * Matches the given String as a regular expression pattern
	 * against the objects {@link Class#getCanonicalName()} using
	 * the ==~ operator to determine matching.
	 */
	public StructureDesignator canonical_name(String patternString){
		return new CanonicalNamePatternSD(patternString);
	}
	
	/**
	 * Keyword usage: Canonical_name(~/java.*String/);<br>
	 * Matches the given Pattern against the objects
	 * {@link Class#getCanonicalName()} using the ==~ operator to
	 * determine matching.
	 */
	public StructureDesignator canonical_name(Pattern pattern){
		return new CanonicalNamePatternSD(pattern);
	}
	
	/**
	 * Keyword usage: inherits_Canonical_name("java.*Number");<br>
	 * Matches the given String as a regular expression pattern against
	 * the objects class and all superclasses
	 * {@link Class#getCanonicalName()} to determine matching, while a
	 * single match suffices.
	 */
	public StructureDesignator inherits_canonical_name(String patternString){
		return new InheritsCanonicalNamePatternSD(patternString);
	}
	
	/**
	 * Keyword usage: inherits_Canonical_name(~/java.*Number/);<br>
	 * Matches the given Pattern against the objects class and all
	 * superclasses {@link Class#getCanonicalName()} to determine
	 * matching, while a single match suffices.
	 */
	public StructureDesignator inherits_canonical_name(Pattern pattern){
		return new InheritsCanonicalNamePatternSD(pattern);
	}

	/**
	 * Matches all objects, where the given StructureDesignator does
	 * not match and vice versa.
	 */
	public StructureDesignator not(StructureDesignator x) {
		return new NotSD(x);
	}
	
}
