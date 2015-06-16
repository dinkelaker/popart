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
package de.tud.stg.example.aosd2009.file;

import groovy.lang.GroovyObject;

import groovy.lang.*;

import de.tud.stg.example.aosd2009.file.nativecode.NativeFile;
import de.tud.stg.popart.aspect.AspectManager;

public class FileMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.err.println("First run ");
		File f1 = new File("A.txt");
		File f2 = new File("B.txt");		
		f1.copy(f2);
		System.err.println("Should be "+(f1.getSize()*4));

		f1.resetHandle();
		System.err.println("Second run with native file");
		File f3 = new NativeFile("B.txt");
		f1.copy(f3);		
		System.err.println("Should be "+(f1.getSize()*4));
		
		GroovyObject oa = (GroovyObject)AspectManager.getInstance().getAspect("FileTester");
		int matches = (Integer)oa.invokeMethod("getMatchingPointcutsCounter", new Object[0]);
		System.err.println("macthing pcs="+matches);
	}

}
