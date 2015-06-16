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
package de.tud.stg.example.aosd2009.file.instrumentation;

import java.io.File;

import de.tud.stg.example.interpreter.metamodel.Process;
import de.tud.stg.popart.aspect.AspectLoader;
import de.tud.stg.popart.aspect.extensions.Booter;

public aspect InitMain {
	
	private final boolean DEBUG = false;
	
	private final String pathToAspectDir = "src/de/tud/stg/example/aop/".replace("/", File.separator);
	
	/** @todo Consider Scheduling */ 
	before() : execution(public static void de.tud.stg.example.aosd2009.metaadvice.Main.main(..))  {
		Booter.initialize();
		if (DEBUG) System.out.println("InitMain.advice: Booted.");
		AspectLoader.loadAspectDefinitions(pathToAspectDir);
	}

	before() : execution(public static void de.tud.stg.example.aosd2009.file.Main.main(..))  {
		Booter.initialize();
		if (DEBUG) System.out.println("InitMain.advice: Booted.");
		AspectLoader.loadAspectDefinitions(pathToAspectDir);
	}

	before() : execution(public static void de.tud.stg.example.aosd2009.file.FileMain.main(..))  {
		Booter.initialize();
		if (DEBUG) System.out.println("InitMain.advice: Booted.");
		AspectLoader.loadAspectDefinitions(pathToAspectDir);
	}

	before() : execution(public static void de.tud.stg.example.aosd2009.phone.Main.main(..))  {
		if (DEBUG) System.out.println("InitMain.advice: Booting.");
		Booter.initialize();
		if (DEBUG) System.out.println("InitMain.advice: Booted.");
		AspectLoader.loadAspectDefinitions(pathToAspectDir);
	}

	before() : execution(public static void de.tud.stg.example.aosd2009.x.Main.main(..))  {
		if (DEBUG) System.out.println("InitMain.advice: Booting.");
		Booter.initialize();
		if (DEBUG) System.out.println("InitMain.advice: Booted.");
		AspectLoader.loadAspectDefinitions(pathToAspectDir);
	}
}
