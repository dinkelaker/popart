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

import java.util.regex.Pattern;

/**
 * This StructureDesignator matches a Pattern or a String evaluated
 * as a regular expression against the object-to-be-evaluateds classes
 * package and all surrounding packages names
 * @author Joscha Drechsler
 */
public class PackageHierarchyPatternSD extends StructureDesignator{
	/**
	 * The String or Pattern to match against
	 */
	private Pattern pattern;
	
	/**
	 * String Constructor.
	 * @param patternString the String to be used as regular expression
	 */
	public PackageHierarchyPatternSD(String patternString){
		this(Pattern.compile(patternString));
	}
	
	/**
	 * Pattern Constructor.
	 * @param pattern the Pattern to be used
	 */
	public PackageHierarchyPatternSD(Pattern pattern){
		super("pattern_within_package_hierarchy("+pattern.pattern()+")");
		this.pattern = pattern;
	}

	/**
	 * @return <code>true</code> if the objects classes package or any
	 * surrounding packages name matches the stored string or pattern,
	 * using the ==~ operator.
	 */
	public boolean matches(Class<?> c){
		Package p = c.getPackage();
		if(p == null) return false;
		/*
		 * Iterates over pname removing the last .name section with each
		 * iteration:
		 *  - java.lang.reflect
		 *  - java.lang
		 *  - java
		 */
		for(
				String pname = p.getName();
				pname.indexOf('.')>0;
				pname = pname.substring(0,pname.lastIndexOf('.'))
			){
				if(pattern.matcher(pname).matches()) return true;
			}
			return false;
	}
}
