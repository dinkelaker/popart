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
package de.tud.stg.popart.aspect;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.FilenameFilter;

public abstract class AspectLoader {
	private static final boolean DEBUG = false;
	
	private static GroovyShell gshell = new GroovyShell();

	/**
	 * Loads aspects from a directory.
	 */
	public static void loadAspectDefinitions(String pathToAspectDir) {
		File dir = new File(pathToAspectDir);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File directory, String name) {
				return name.endsWith(".groovy");
				// return true;
			}
		};

		String[] aspectDefs = dir.list(filter);

		if (DEBUG) {
			System.out.println("*** START LOADING ASPECT ... ***");
			System.out.print("List of Aspects = {");
			for (int i = 0; i < aspectDefs.length; i++) {
				System.out.print(aspectDefs[i] + ", ");
			}
			System.out.print("}");
		}

		assert (aspectDefs != null); //Wrong directory or no files are contained
		for (int i = 0; i < aspectDefs.length; i++) {
			if (DEBUG)
				System.out.println("Load Aspect[" + i + "]:");
			loadAspect(pathToAspectDir, aspectDefs[i]);
		}
		if (DEBUG) {
			System.out.println("********************************");
			System.out.println("********************************");
			System.out.println("********************************");
			System.out.println("");
			System.out.println("*** ALL ASPECT LOADED ***");
			System.out.println();
		}
	}


	public static void loadAspect(String pathToAspectDir, String aspectFileName) {
		if (DEBUG){
			System.out.println("********************************");
			System.out.println("AspectManager: \t\t AspectManager.loadAspect(" + aspectFileName + ") ...");
		}
		Script script = null;
		try {
			synchronized (gshell) {
				script = gshell.parse(new File(pathToAspectDir + aspectFileName));
			}

			script.getBinding().setVariable("aspect",
					new AspectBootstrapClosure(AspectLoader.class));
			Object definitionResult = script.run();
			Aspect aspect = (Aspect) definitionResult;
			if (aspect != null) {
				AspectManager.getInstance().register(aspect);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (DEBUG){
			System.out.println("AspectManager: \t\t AspectManager.loadAspect(" + aspectFileName + ") ... finished.");
			System.out.println("********************************");
			System.out.println("");
		}
	}
}
