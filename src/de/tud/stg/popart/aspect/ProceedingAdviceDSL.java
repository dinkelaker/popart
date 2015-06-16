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
package de.tud.stg.popart.aspect;

import java.util.List;
import java.util.Map;

import de.tud.stg.popart.dslsupport.*;

/**
 * This class defines an AdviceDSL implementing the default.
 * @author Tom Dinkelaker
 */
public class ProceedingAdviceDSL extends AdviceDSL {

	private final static boolean DEBUG = false; 
	
	protected Map<String, Object> context = null;
	
	public void setContext(Map<String, Object> _context) {
		context = _context;
	}
	 
	public Map<String, Object> getContext() {
		return context;
	}
	 
	public static Interpreter getInterpreter(Map<String, Object> context) {
		return DSLCreator.getInterpreter(new ProceedingAdviceDSL(),context);
	}
	
	/* Literals */
	
	
	/* Operations */
	public Object proceed() {
		Map<String, Object> ctxt = getContext(); // safe context before advice call 

		if (DEBUG) System.out.println("ProceedingAdviceDSL.proceed ctxt="+ctxt);
		List<Object> args = (List<Object>) ctxt.get("args");
		if (DEBUG) System.out.println("ProceedingAdviceDSL.proceed args="+args);
		IProceed currentProceed = (IProceed) ctxt.get("proceed");
		if (DEBUG) System.out.println("ProceedingAdviceDSL.proceed calling closure: "+currentProceed);
		
		assert (currentProceed != null);
		
		Object result = currentProceed.call(args);
		if (DEBUG) if (DEBUG) System.out.println("ProceedingAdviceDSL.proceed closure executed: "+currentProceed);
		ctxt.put("result",result);
		
		setContext(ctxt); // restore context after advice call 

		/*
		 * It is important to keep a local reference to the current context here 
		 * because other join points are fire in advice, which will again set the context of the same ProceedingAdviceDSL instance.
		 */		
		if (DEBUG) System.out.println("ProceedingAdviceDSL.proceed result="+result);
		return result;
	}
	
}
