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

public class DeepDebugPCD extends Pointcut {
	
	Closure debugClosure
	
	DeepDebugPCD(Closure debugClosure) {
		super('deep_debug')
		this.debugClosure = debugClosure
	}
	
	void deepDebug(Pointcut pc, JoinPoint jp, String indent) {
		def result = pc.match(jp)
		if (pc instanceof AndPCD) { 
			System.err.println("""$indent\\-- DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
			deepDebug(pc.left,jp,"$indent    ")
			if (pc.left.match(jp)) {
			  deepDebug(pc.right,jp,"$indent    ")
			} else {
				System.err.println("""$indent    \\-- DEEP_DEBUG: right=$pc.right.""")
				System.err.println("""$indent        \\-| DEEP_DEBUG: cannot debug deeper because left expression of and pcd failed.""")
			}
		} else if (pc instanceof OrPCD) { 
			System.err.println("""$indent\\-- DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
			deepDebug(pc.left,jp,"$indent    ")
			deepDebug(pc.right,jp,"$indent    ")
		} else if (pc instanceof CflowBelowPCD) { 
			System.err.println("""$indent\\-- DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
			System.err.println("""$indent\\-- DEEP_DEBUG: Stack = $jp.context.joinPointStack""")
			int depth = 0
			jp.context.joinPointStack.find { jpOnStack ->
			   depth++
			   def resultOnStack = pc.pc.match(jpOnStack)
   			   System.err.println("""$indent    \\-- Stack[$depth] ${resultOnStack? "match" : "no match"} $jpOnStack""")
   			   return resultOnStack
			}
		} else if (pc instanceof CflowPCD) { 
			System.err.println("""$indent\\-- DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
			System.err.println("""$indent\\-- DEEP_DEBUG: Stack = $jp.context.joinPointStack""")
			int depth = 0
			Iterator it = jp.context.joinPointStack.find { jpOnStack ->
			   depth++
			   def resultOnStack = pc.pc.match(jpOnStack)
   			   System.err.println("""$indent    \\-- Stack[$depth] ${resultOnStack? "match" : "no match"} $jpOnStack""")
   			   return resultOnStack
			}
		} else if (pc instanceof IfPCD) { 
			System.err.println("""$indent\\-- DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
			System.err.println("""$indent    \\-| DEEP_DEBUG: cannot debug deeper if pointcuts.""")
		} else if (pc instanceof NotPCD) { 
			System.err.println("""$indent\\-- DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
			deepDebug(pc.pc,jp,"$indent    ")
		} else if (pc instanceof PrimitivePCD) {
			System.err.println("""$indent\\-- DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
			System.err.println("""$indent    \\-| DEEP_DEBUG: cannot debug deeper primitive pointcuts.""")
		} else {
			System.err.println("""$indent\\-- DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
			System.err.println("""$indent    \\-| DEEP_DEBUG: cannot debug deeper unknown pointcut.""")
		}
		
	}
	
	boolean match(JoinPoint jp) {
		if (DEBUG) println "DeepDebugPCD:\tcontext=$jp.context"
		debugClosure.delegate = jp.context
		Pointcut pc = (Pointcut)debugClosure.call()
		
		boolean result = pc.match(jp)
		
		System.err.println("""DEEP_DEBUG: ${result? "match" : "no match"} '$pc' of $jp.context.thisAspect.name at $jp""")
        deepDebug(pc,jp,"    ")
		
		if (DEBUG) println "DeepDebugPCD:\tresult=$result"
		result
	}
	
	String toString() {
		"deep_debug ( {${debugClosure.call()}} )"
	}
}