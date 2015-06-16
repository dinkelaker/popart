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

import java.util.Collections;
import java.util.Comparator;

public class PointcutAndAdviceComparator<T extends AspectMember> implements Comparator<T> {
	
	protected final boolean DEBUG = false;
	
	public int compare(T o1, T o2) {
		if (DEBUG) System.out.print("PointcutAndAdviceComparator: o1="+o1.getAspect().getName()+" p="+o1.getAspect().getPriority()+
				  ", o2="+o2.getAspect().getName()+" p="+o2.getAspect().getPriority());
		if((o1 instanceof PointcutAndAdvice) && (o2 instanceof PointcutAndAdvice)){
			int result = ((PointcutAndAdvice)o1).compareTo((PointcutAndAdvice)o2);
			if (DEBUG) System.out.println(", o1.compareTo(o2)="+result);
			return result;
		}else{
			return Collections.reverseOrder().compare(o1.getAspect().getPriority(), o2.getAspect().getPriority());
		}
	}
}
