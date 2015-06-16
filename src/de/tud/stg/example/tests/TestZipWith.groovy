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
package de.tud.stg.example.tests

import de.tud.stg.popart.dslsupport.*;
import de.tud.stg.popart.dslsupport.modulo.*;

class TestZipWith {
	public static void main(String[] args) {
		def m1 = 3
		def m2 = 4
		def m3 = 12
		
		DSL d1 = new ModuloDSL(m1);
		DSL d2 = new ModuloDSL(m2);
		DSL d3 = new ModuloDSL(m3);
		
		def r1
		def r2 
		def r3
		
		d1.eval {
		  r1 = add(5,6)	
		}

		d2.eval {
		  r2 = add(5,6)	
		}

		d3.eval {
		  r3 = add(5,6)	
		}
		
		println "result r1:$r1 r2:$r2 r3:$r3"
		
		def zipWithClosure = {x,y -> (x*y)%12 }
		def d12 = new ZipWithDSL(d1,d2,zipWithClosure,new HashMap())
		  
		def r4

		d12.eval {
			  r4 = add(5,6)	
		}
			
		println "result r4:$r4"
		
		
	}
}