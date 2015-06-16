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
package de.tud.stg.example.aosd2009.phone;

import java.util.Map;

import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdviceComparator;
import de.tud.stg.popart.aspect.extensions.comparators.ApplicationSpecificComparator;

/**
 * Defines an application-specific partial order for the forwarding features of a phone management system.
 */
public class PhoneManagementSystemComparator<T extends AspectMember> extends ApplicationSpecificComparator<T> {

	private static final int LOWER_PRECEDENCE = 1;
	private static final int HIGHER_PRECEDENCE = -1;
	
	public int compare(T o1, T o2) {
		if (DEBUG) System.out.print(
				"PhoneManagementSystemComparator: o1="+
				  o1.getAspect().getName()+
				  " p="+o1.getAspect().getPriority()+
				  ", o2="+o2.getAspect().getName()+
				  " p="+o2.getAspect().getPriority());
				  
		String name1 = o1.getAspect().getName();
		String name2 = o2.getAspect().getName();
		int result;
	    if (name1.equals("AliceToAnswerMachineAlice") && 
	        name2.equals("AliceToBob")) {
	        Map<String,Object> context = AspectManager.getInstance().getCurrentJoinPointStack().getLast().context;
	    	Phone fromPhone = (Phone)context.get("targetObject");
	        Phone toPhone = fromPhone.getForwardPhone();
	        if (toPhone.getAnswerMachine().isOn()) { 
	            result = HIGHER_PRECEDENCE;
	        } else { 
	            result = LOWER_PRECEDENCE; 
	        } 
	    } else if (name1.equals("AliceToBob") && 
			       name2.equals("AliceToAnswerMachineAlice")) {
			Map<String,Object> context = AspectManager.getInstance().getCurrentJoinPointStack().getLast().context;
			Phone fromPhone = (Phone)context.get("targetObject");
			Phone toPhone = fromPhone.getForwardPhone();
			if (toPhone.getAnswerMachine().isOn()) { 
			    result = LOWER_PRECEDENCE; 
			} else { 
			    result = HIGHER_PRECEDENCE;
			} 
	    } else {
	        result = new PointcutAndAdviceComparator<T>().compare(o1,o2); 
	    }
	    if (DEBUG) System.out.println(", o1.compareTo(o2)="+result);
		return result;
	}
	
}