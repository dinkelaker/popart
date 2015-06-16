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

import java.util.Iterator;
import java.util.List;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.PointcutAndAdvice;

class CountingAspectManager extends AspectManager {

	public CountingAspectManager() {
		super();
	}

	public void finalize() {	
		System.out.println("//================================");
		System.out.println("|| CountingAspectManager Results =");
		System.out.println("||================================");
		int index = 0;
		for(Aspect aspect : getAspects()){
			if(aspect instanceof CountingAspect)
				index ++;
			printAspectDetails((CountingAspect)aspect,index);
		}
		System.out.println("\\\\================================");
	} 

	protected void printAspectDetails(CountingAspect aspect, int index) {
		System.out.println("|| Aspect no. "+index);
		System.out.println("|| Name = "+aspect.getName());

		if (aspect.isDeployed()) {
			System.out.println("|| Instances = "+aspect.getInstanceCounter());
			System.out.println("|| Received JPs = "+aspect.receivedJoinPointCounter);
			System.out.println("|| Matching PCs = "+aspect.matchingPointcutCounter);
			printAspectDetails(aspect);
		} else {
			System.out.println("|| (Aspect instance is not deployed.)");
		}

		System.out.println("||--------------------------------");
	}

	protected void printAspectDetails(CountingAspect aspect) {
		System.out.println("|| Match Ratio  = ");
		List<PointcutAndAdvice> pas = aspect.findAllPointcutsAndAdvice();
		//System.out.println "pas = $pas"
		for(PointcutAndAdvice pa : pas){
			//System.out.println "pa = $pa"
			//System.out.println "ppmc = $aspect.perPointcutMatchCount"
			//System.out.println "ppnmp = $aspect.perPointcutNotMatchCount"
			Integer match = aspect.perPointcutMatchCount.get(pa);
			match = (match == null)? 0 : match;
			Integer notMatch = aspect.perPointcutNotMatchCount.get(pa);
			notMatch = (notMatch == null)? 0 : notMatch;
			if ((match+notMatch) > 0) {
				double ratio = Math.round(100.0*match/(match+notMatch))/100.0;
				System.out.println("||   "+ratio+" ("+match+",!"+notMatch+"): "+pa.getPointcut()); 	        	
			} else {
				System.out.println("||   (No join points were reported to this aspect instance.)");  	
			}
		}

	}	

}