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
 * This {@link OneLevelStructureDesignator} matches a Pattern, or
 * a String evaluated as a regular expression, against
 * {@link Class.getSimpleName()} of the supplied Class instances.
 * @author Joscha Drechsler
 */
public class ClassPatternSD extends OneLevelStructureDesignator{
	/**
	 * The String or Pattern to match against
	 */
	private Pattern pattern;
	
	/**
	 * String Constructor.
	 * @param patternString the String to be used as regular expression
	 */
	public ClassPatternSD(String patternString){
		this(Pattern.compile(patternString));
	}
	
	/**
	 * Pattern Constructor.
	 * @param pattern the Pattern to be used
	 */
	public ClassPatternSD(Pattern pattern){
		super("pattern_is_class("+pattern.pattern()+")");
		this.pattern = pattern;
	}
	
	/**
	 * see {@link OneLevelStructureDesignator#isDirectMatch(Class)}
	 */
	protected boolean isDirectMatch(Class<?> c){
		return pattern.matcher(c.getSimpleName()).matches();
	}
}
