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
package de.tud.stg.tests.dslsupport;

import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import junit.framework.Assert;
import de.tud.stg.popart.dslsupport.*;
import de.tud.stg.popart.dslsupport.analysis.syntax.UsedKeywordsAnalysis;

/**
 * @author dinkelaker
 *
 */
public class TestSyntaxAnalysis extends TestCase {
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception{
	}

	public void testTestUsedKeywords() {
		System.out.println("testTestUsedKeywords");
		UsedKeywordsAnalysis dsl = new UsedKeywordsAnalysis();
	    Closure cl = { 
	    	// A DSL PROGRAM - BEGIN
	    	
	    	Object x;
	    	x = primitiveLiteral;
    		primitiveLiteral2 = 2;
	    	
	    	def y = getAccessorLiteral1(); //accessing through getter
	    	setAccessorLiteral2(y); //accessing through setter
	    	
	    	operation(x,y);

	    	nestedAbstraction() { //using a block 
	    		def z = primitiveLiteral3;
	    		primitiveLiteral4 = 4;

	    		operation2(x,y);
	    	}
	    	
	    	// A DSL PROGRAM - END
	    }
	    cl.delegate = dsl;
	    cl.resolveStrategy = Closure.DELEGATE_FIRST;
	    cl.call();
	    
	    def DETAILED_PRINT_OUT = true;
	    
	    if (DETAILED_PRINT_OUT) System.out.println("Checking the produced sets");

	    if (DETAILED_PRINT_OUT) System.out.println("keywords(${dsl.getKeywords().size()})="+dsl.getKeywords());
	    assert(dsl.getKeywords().contains("primitiveLiteral"));
	    assert(dsl.getKeywords().contains("primitiveLiteral2"));
	    assert(dsl.getKeywords().contains("accessorLiteral1"));
	    assert(dsl.getKeywords().contains("accessorLiteral2"));
	    assert(dsl.getKeywords().contains("operation"));
	    assert(dsl.getKeywords().contains("nestedAbstraction"));
	    assert(dsl.getKeywords().contains("primitiveLiteral3"));
	    assert(dsl.getKeywords().contains("primitiveLiteral4"));
	    assert(dsl.getKeywords().contains("operation2"));
	    assert(dsl.getKeywords().size()==9);

	    if (DETAILED_PRINT_OUT) System.out.println("literals(${dsl.getLiterals().size()})="+dsl.getLiterals());
	    assert(dsl.getLiterals().contains("primitiveLiteral"));
	    assert(dsl.getLiterals().contains("primitiveLiteral2"));
	    assert(dsl.getLiterals().contains("accessorLiteral1"));
	    assert(dsl.getLiterals().contains("accessorLiteral2"));
	    assert(dsl.getLiterals().contains("primitiveLiteral3"));
	    assert(dsl.getLiterals().contains("primitiveLiteral4"));
	    assert(dsl.getLiterals().size()==6);

	    if (DETAILED_PRINT_OUT) System.out.println("operations(${dsl.getOperations().size()})="+dsl.getOperations());
	    assert(dsl.getOperations().contains("operation"));
	    assert(dsl.getOperations().contains("operation2"));
	    assert(dsl.getOperations().size()==2);

	    if (DETAILED_PRINT_OUT) System.out.println("abstractions(${dsl.getAbstractions().size()})="+dsl.getAbstractions());
	    assert(dsl.getAbstractions().contains("nestedAbstraction"));
	    assert(dsl.getAbstractions().size()==1);
	}
	
	
	

	
	
}
