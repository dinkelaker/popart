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
package de.tud.stg.popart.aspect.extensions;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.pointcuts.*;
import de.tud.stg.popart.joinpoints.*;
import de.tud.stg.popart.dslsupport.*;

import groovy.lang.MetaClassRegistry;

/**
 * This meta aspect does extend the püointcut evaluation semantics with debugging funtionality.
 * I.e., pointcut evaluation is intercepted and information about the evaluation of subexpression is printed on the console, and user input is requested for step wise debugging of pointcuts. 
 * @author Tom Dinkelaker
 */
public class DebugMetaAspect extends MetaAspect implements MetaAspectProtocol {
	
	protected static boolean DEBUG = true;
	
    static InputStreamReader isr = new InputStreamReader( System.in );

    static BufferedReader stdin = new BufferedReader( isr );

    public DebugMetaAspect(final Class theClass){
    	super(theClass);
	}
    
    public DebugMetaAspect(final MetaClassRegistry registry, final Class theClass) {
        super(registry, theClass);
    }
    
	public void receiveAround(Aspect aspect, JoinPoint jp, List applicablePAs) {
		if (DEBUG) println "DebugMetaAspect.receivingAround: \tReceiving around proceed=$jp.context.proceed"
		super.receiveAround(aspect,jp,applicablePAs);
		if (DEBUG) println "DebugMetaAspect.receivingAround: \tAfter advice     proceed=$jp.context.proceed"
	}
	
	/**
	 * Overrides the default semantics for pointcut evaluation.
	 * Pointcut evaluation is intercepted and information about the evaluation of subexpression is printed on the console, and user input is requested for step wise debugging of pointcuts.
	 */
	public boolean matchPointcut(Aspect aspect, JoinPoint jp, Pointcut pc){
		if (DEBUG) println "DebugMetaAspect.matchPointcut: \t"
		boolean result = pc.match(jp);

		println( "DebugMetaAspect: debug breakpoint at '$jp'" );
	    println( "DebugMetaAspect: pointcut = '${pc}'" );
	    println( "DebugMetaAspect: POINTCUT " + (result? "MATCHES" : "DOES NOT MATCH"));
	    print( "DebugMetaAspect, please type in command: \n(s) step over \n(i) step into \n(f) force match \n(p) inspect pointcut \n(j) inspect join point \n(a) inspect aspect \n(m) inspect metaaspect \n(x) exit program" );
		
		boolean readChar = false;
		while (!readChar) {
		    print( " > " );
			String input = stdin.readLine();
		    switch (input) {
		        case "s":
		    		if (DEBUG) println "InputDebugPCD:\tresult=$result"
		    		print """DebugMetaAspect: ${result? "match" : "no match"} """
		    		print "'$pc' "
		    		println "of jp = $jp "
		    		println "of context = $jp.context "
		    		
		    		//new Throwable().printStackTrace();
		    		
		    		println "of aspect = $aspect "
		    		println "of name = $aspect.name "
		    		
		    		println "of $aspect.name at $jp\n" 
		    		//TODO Check this previous line fails because aspect is null in the following case
		    		/* DebugMetaAspect: no match 'initialization (de.tud.stg.tests.popart.extensions.cool.boundedbuf.BoundedBuffer)' of jp = class de.tud.stg.popart.joinpoints.MethodCallJoinPoint loc=call(public java.lang.StringBuilder java.lang.StringBuilder.append(java.lang.Object)):BrokenAStarter.java:21 ctxt=... of context = ["targetObject":error: java.lang.RuntimeException: java.lang.RuntimeException: java.lang.RuntimeException: java.lang.NullPointerException, "result":error: java.lang.RuntimeException: java.lang.RuntimeException: java.lang.RuntimeException: java.lang.NullPointerException, "application":"de.tud.stg.example.aosd2009.metaadvice.Main", "thisObject":null, "thisJoinPoint":class de.tud.stg.popart.joinpoints.MethodCallJoinPoint loc=call(public java.lang.StringBuilder java.lang.StringBuilder.append(java.lang.Object)):BrokenAStarter.java:21 ctxt=..., "args":[java.lang.RuntimeException: java.lang.RuntimeException: java.lang.RuntimeException: java.lang.NullPointerException], "joinPointStack":[class de.tud.stg.popart.joinpoints.MethodExecutionJoinPoint loc=execution(public static void de.tud.stg.tests.popart.extensions.cool.boundedbuf.BrokenAStarter.main(java.lang.String[])):BrokenAStarter.java:14 ctxt=..., class de.tud.stg.popart.joinpoints.MethodCallJoinPoint loc=call(public void de.tud.stg.tests.popart.extensions.cool.boundedbuf.BrokenBoundedBufferTestCase.testSth()):BrokenAStarter.java:19 ctxt=..., class de.tud.stg.popart.joinpoints.MethodExecutionJoinPoint loc=execution(public void de.tud.stg.tests.popart.extensions.cool.boundedbuf.BrokenBoundedBufferTestCase.testSth()):BrokenBoundedBufferTestCase.java:53 ctxt=..., class de.tud.stg.popart.joinpoints.MethodCallJoinPoint loc=call(public java.lang.StringBuilder java.lang.StringBuilder.append(java.lang.Object)):BrokenAStarter.java:21 ctxt=...], "enclosingJoinPoint":class de.tud.stg.popart.joinpoints.MethodExecutionJoinPoint loc=execution(public void de.tud.stg.tests.popart.extensions.cool.boundedbuf.BrokenBoundedBufferTestCase.testSth()):BrokenBoundedBufferTestCase.java:53 ctxt=..., "process":class java.util.HashMap, "system":["applications":["de.tud.stg.example.aosd2009.metaadvice.Main":["process":"de.tud.stg.example.aosd2009.metaadvice.Main"]]], "proceed":de.tud.stg.tests.instrumentation.MethodCallInstrumentation$1@1ee04fd, "context":this Map_, "method":"append", "thread_id":1, "signature":StringBuilder java.lang.StringBuilder.append(Object)]*/

		    		readChar = true;
		    	    break;
		        case "i":
		        	deepDebug(pc,jp,"")
		        	break;
		        case "f":
		    		println("""DebugMetaAspect: Forced pointcut match!""")
		        	return true;
		            break;
		        case "p":
		            groovy.inspect.swingui.ObjectBrowser.inspect(pc)
		        	break;
		        case "j":
		            groovy.inspect.swingui.ObjectBrowser.inspect(jp)
		        	break;
		        case "a":
		            groovy.inspect.swingui.ObjectBrowser.inspect(aspect)
		        	break;
		        case "m":
		            groovy.inspect.swingui.ObjectBrowser.inspect(aspect.metaAspect)
		        	break;
		        case "x":
		        	throw new RuntimeException("Program aborted in pointcut debugger.");
		            break;
		        default:
		    	    println "Unknown command '$input'."
		    	    print( "DebugMetaAspect, please type in command: \n(s) step over \n(i) step into \n(f) force match \n(p) inspect pointcut \n(j) inspect join point \n(a) inspect aspect \n(m) inspect metaaspect \n(x) exit program" );
		    }	
		}
		return result;
	}
	
	/**
	 * @todo Adjust indenting.
	 */
	protected void deepDebug(Pointcut pc, JoinPoint jp, String indent) {
		def result = pc.match(jp); 
		if (pc instanceof AndPCD) { 
			System.err.println(indent+"""${result? "match" : "no match"} '$pc' of $pc.aspect.name at $jp""")
			def andPc = (AndPCD)pc;
			deepDebug(andPc.left,jp,indent+"|   "+"""\\-- left=""");
			if (andPc.left.match(jp)) {
			  deepDebug(andPc.right,jp,indent+"|   "+"""\\-- right=""");
			} else {
				System.err.println(indent+"""$andPc.right.""")
				System.err.println(indent+"|      "+"""\\-<| cannot debug deeper because left expression of and pcd failed.""")
			}
		} else if (pc instanceof OrPCD) { 
			System.err.println(indent+"""\\-- ${result? "match" : "no match"} '$pc' of $pc.aspect.name at $jp""")
			def orPc = (OrPCD)pc;
			deepDebug(orPc.left,jp,indent+"    "+"""\\-- left=""");
			deepDebug(orPc.right,jp,indent+"    "+"""\\-- right=""");
		} else if (pc instanceof CflowBelowPCD) { 
			System.err.println(indent+"""\\-- ${result? "match" : "no match"} '$pc' of $pc.aspect.name at $jp""")
			def cflowBelowPc = (CflowBelowPCD)pc;
			System.err.println(indent+"""\\-- Stack = $jp.context.joinPointStack""")
			Iterator it = jp.context.joinPointStack.iterator();
			int depth = 0;
			it.find { jpOnStack ->
			   depth++;
			   def resultOnStack = cflowBelowPc.pc.match(jpOnStack);
   			   System.err.println(indent+"|   "+"""\\-- Stack[$depth] ${resultOnStack? "match" : "no match"} $jpOnStack""")
   			   return resultOnStack;
			}
		} else if (pc instanceof CflowPCD) { 
			System.err.println(indent+"""\\-- ${result? "match" : "no match"} '$pc' of $pc.aspect.name at $jp""")
			def cflowPc = (CflowPCD)pc;
			System.err.println(indent+"""\\-- Stack = $jp.context.joinPointStack""")
			Iterator it = jp.context.joinPointStack.iterator();
			int depth = 0;
			it.find { jpOnStack ->
			   depth++;
			   def resultOnStack = cflowPc.pc.match(jpOnStack);
   			   System.err.println(indent+"|   "+"""\\-- Stack[$depth] ${resultOnStack? "match" : "no match"} $jpOnStack""")
   			   return resultOnStack;
			}
		} else if (pc instanceof IfPCD) { 
			System.err.println(indent+"""\\-- ${result? "match" : "no match"} '$pc' of $pc.aspect.name at $jp""")
			def ifPc = (IfPCD)pc;
			System.err.println(indent+"    "+"""\\-<| cannot debug deeper if pointcuts.""")
		} else if (pc instanceof NotPCD) { 
			System.err.println(indent+"""\\-- ${result? "match" : "no match"} '$pc' of $pc.aspect.name at $jp""")
			def notPc = (NotPCD)pc;
			deepDebug(notPc.pc,jp,indent+"    ");
		} else if (pc instanceof PrimitivePCD) {
			System.err.println(indent+"""\\-- ${result? "match" : "no match"} '$pc' of $pc.aspect.name at $jp""")
			System.err.println(indent+"|   "+"""\\-<| cannot debug deeper primitive pointcuts.""")
		} else {
			System.err.println(indent+"""\\-- ${result? "match" : "no match"} '$pc' of $pc.aspect.name at $jp""")
			System.err.println(indent+"|   "+"""\\-<| cannot debug deeper unknown pointcut.""")
		}
		
	}

	
}
