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
package de.tud.stg.example.aosd2009.metaadvice;

public class Main {
	
	
	public static void main(String[] args) {
		System.out.println("starting Main program");
	}
	
	public static void run() {
		Clazz obj = new Clazz();		
		
		System.out.println("\ncalling foo");
		obj.foo();

		System.out.println("\ncalling baz");
		obj.baz();

		System.out.println("\ncalling bar");
		obj.bar();

		System.out.println("\ncalling qux");
		obj.qux();
    }
	
}
