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
package de.tud.stg.popart.aspect.extensions.cool;

/**
 * @author Oliver Rehor
 */
public abstract class DSLauncher {
	
	/**
	 * Subclasses must override this method and bind there specific
	 * bootstrap keywords therein.
	 */
	public abstract void bindBootstrapKeywords(Script script);
	
	/**
	 * Load a DSL source file and interpret it after determining the
	 * class path from a dependant class.
	 */
	public final void loadAndInterpretSource(File dslSrcFile, Class dependentClass) {
		String dependentClassName = dependentClass.getName();
		String applicationClassPath = dependentClassName.substring(0, dependentClassName.lastIndexOf(".") + 1);
		loadAndInterpretSource(dslSrcFile, applicationClassPath);
	}
	
	/**
	 * Load a DSL source file and interpret it with a given class path.
	 */
	public void loadAndInterpretSource(File dslSrcFile, String applicationClassPath) {
		GroovyShell gshell = new GroovyShell();
		if (applicationClassPath != ""){
			gshell.getClassLoader().addClasspath("bin/"+applicationClassPath.replace(".", "/"));
		}
		Script script = gshell.parse(dslSrcFile);
		bindBootstrapKeywords(script);
		script.run();
	}
	
}
