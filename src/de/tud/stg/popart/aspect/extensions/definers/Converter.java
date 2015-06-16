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
package de.tud.stg.popart.aspect.extensions.definers;

import java.util.List;
import java.util.Set;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectMember;

public class Converter {
	public static Set<String> convertFromAspectToString(Set<Aspect> setOfInteractingAspects) {
		Set<String> stringSetOfInteractingAspects = new java.util.HashSet<String>();
		for(Aspect a : setOfInteractingAspects){
			stringSetOfInteractingAspects.add(a.getName());
		}
		return stringSetOfInteractingAspects;
	}

	public static Set<String> convertFromAMToString(List<? extends AspectMember> aspectMembers) {
		Set<String> stringSetOfInteractingAspects = new java.util.HashSet<String>();

		for(AspectMember am : aspectMembers){
			Aspect a = am.getAspect();
			String aspectName = a.getName();
			stringSetOfInteractingAspects.add(aspectName);
		}
		return stringSetOfInteractingAspects;
	}

	public static Set<Aspect> convertFromPAToAspect(List<? extends AspectMember> aspectMembers) {
		Set<Aspect> listOfInteractingAspects = new java.util.HashSet<Aspect>();

		for(AspectMember am : aspectMembers){
			Aspect a = am.getAspect();
			listOfInteractingAspects.add(a);
		}
		return listOfInteractingAspects;
	}

}
