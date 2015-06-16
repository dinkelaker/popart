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

import java.util.List;

import de.tud.stg.popart.aspect.PointcutAndAdvice;

class ProfilingAspectManager extends CountingAspectManager {

	public ProfilingAspectManager() {
		super();
	}
		
	protected void printAspectDetails(CountingAspect aspect) {
		super.printAspectDetails(aspect);
		ProfilingAspect paspect = (ProfilingAspect)aspect;
		System.out.println("|| Execution Times = ");
		List<PointcutAndAdvice> pas = paspect.findAllPointcutsAndAdvice();
     	for(PointcutAndAdvice pa : pas){
			System.out.print("||   ");
			System.out.print("avg="+paspect.getAvgDurationTime(pa)/1E06+" ms, ");
			System.out.print("sum="+paspect.getDurationTime(pa)/1E06+" ms, ");
			System.out.print("min="+paspect.getMinTime(pa)/1E06+" ms, ");
			System.out.print("max="+paspect.getMaxTime(pa)/1E06+" ms " );
			System.out.println(": "+pa.getPointcut());  	        	
		}
	}	
	
}