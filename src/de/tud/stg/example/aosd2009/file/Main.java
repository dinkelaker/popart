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

public class Main {

	public static void main(String[] args) {
        Main myMain = new Main();
        for (int x=0; x < 10; x++) {
        	myMain.m(x+1);
        }
	}
	
	public void m(int i) {
		//System.out.println("m() called the "+i+"-th time.");
		System.out.print("*");
        for (int y=0; y < 4; y++) {
        	this.n(y+1);
        }
        System.out.println();
	}

	public void n(int j) {
		System.out.print(".");
	}
}
