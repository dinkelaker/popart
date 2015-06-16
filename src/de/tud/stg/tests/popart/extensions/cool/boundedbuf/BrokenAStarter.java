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
package de.tud.stg.tests.popart.extensions.cool.boundedbuf;

import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.extensions.cool.CoolAspectFactoryImpl;
import junit.framework.TestSuite;


/**
 * This class is for testing the debug funtionality because JUnit does not support user input. 
 * @author dinkelaker
 */
public class BrokenAStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    org.apache.log4j.PropertyConfigurator.configure(
	      "src/de/tud/stg/tests/popart/extensions/cool/log4j.properties");

		AspectFactory.setInstance(new CoolAspectFactoryImpl()); //TODO Make the initialization of the Cool Aspects nicer...
		
		BrokenBoundedBufferTest bbbt = new BrokenBoundedBufferTest();  
		bbbt.addBoundedBufferTest(BrokenBoundedBufferTestCase.class, bbbt); //load the cool aspect
		
		try { 
		    new BrokenBoundedBufferTestCase().testSth(); //invoke the bounded buffer test
		} catch (Throwable t) {
			System.out.println("error: "+t);
		}
	}

}
