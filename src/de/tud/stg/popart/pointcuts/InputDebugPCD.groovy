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
package de.tud.stg.popart.pointcuts

import de.tud.stg.popart.joinpoints.JoinPoint
import de.tud.stg.popart.joinpoints.StaticJoinPoint

public class InputDebugPCD extends Pointcut {
	
    static InputStreamReader isr = new InputStreamReader( System.in )
    
    static BufferedReader stdin = new BufferedReader( isr )
	
	Closure debugClosure
	
	InputDebugPCD(Closure debugClosure) {
		super('input_debug')
		this.debugClosure = debugClosure
	}
	
	boolean match(JoinPoint jp) {
		if (DEBUG) println "InputDebugPCD:\tcontext=$jp.context"
		debugClosure.delegate = jp.context
		Pointcut pc = (Pointcut)debugClosure.call()
		boolean result = pc.match(jp)
		
	    println( "INPUT_DEBUG: debug breakpoint at '$jp'" )
	    println( "INPUT_DEBUG: pointcut = '${debugClosure.call()}'" )
		
		boolean readChar = false
		while (!readChar) {
  	        print( "INPUT_DEBUG, please type in command: \n(s) step \n(f) force match \n(p) inspect pointcut \n(j) inspect join point \n(a) inspect aspect \n(x) exit program >" )
		    String input = stdin.readLine()
		    //System.out.println( "input = " + input )
		    switch (input) {
		        case 's':
		    		if (DEBUG) println "InputDebugPCD:\tresult=$result"
		    		println("""INPUT_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp\n""")
		    		readChar = true
		    	    break
		        case 'f':
		    		println("""INPUT_DEBUG: Forced pointcut match!""")
		        	return true
		            break
		        case 'p':
		            groovy.inspect.swingui.ObjectBrowser.inspect(pc)
		        	break
		        case 'j':
		            groovy.inspect.swingui.ObjectBrowser.inspect(jp)
		        	break
		        case 'a':
		            groovy.inspect.swingui.ObjectBrowser.inspect(jp.context.thisAspect)
		        	break
		        case 'x':
		        	throw new RuntimeException("Program aborted in pointcut debugger.")
		        default:
		    	    println "Unknown command $input."
		    }
		}
		result
	}
	
	String toString() {
		"input_debug ( {${debugClosure.call()}} )"
	}
}