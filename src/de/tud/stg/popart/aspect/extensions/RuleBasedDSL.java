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

import java.util.ArrayList;
import java.util.HashMap;

import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;
import de.tud.stg.popart.aspect.extensions.definers.IRelationDefinerFacade;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.dslsupport.DSL;

/**
 * @author Olga Gusyeva
 */
public class RuleBasedDSL implements DSL {
	
	/**
	 * 
	 */
	public void declare_precedence(String... someAspects) {
		PrecedenceComparator<?> comparator = (PrecedenceComparator) AspectFactory.getDefaultComparator();
		ArrayList<String> precedenceList = new ArrayList<String>();
		for (int i = 0; i < someAspects.length; i++) {
			precedenceList.add(someAspects[i]);
		}
		comparator.addRule(precedenceList);
	}	
	
	
	public void declare_precedence(ArrayList<String> someAspects) {
		PrecedenceComparator<?> comparator = (PrecedenceComparator) AspectFactory.getDefaultComparator();
		comparator.addRule(someAspects);
	}
	
	public void declare_exclusion(ArrayList<String> listOfAspectNames) {
		declare_mutex(listOfAspectNames);
	}
	
	public void declare_mutex(ArrayList<String> listOfAspectNames) {
		IRelationDefinerFacade mediator = AspectFactory.createMediator();
		mediator.addRule_mutex(listOfAspectNames);
		
	}
	
	public void declare_exclusion(String... someAspects) {
		declare_mutex(someAspects);
	}
	
	public void declare_mutex(String... someAspects) {
		IRelationDefinerFacade mediator = AspectFactory.createMediator();
		ArrayList<String> listOfAspectNames = new ArrayList<String>();
		for (int i = 0; i < someAspects.length; i++) {
			listOfAspectNames.add(someAspects[i]);
		}
		mediator.addRule_mutex(listOfAspectNames);
				
	}
	
	public void declare_implies(HashMap<String, String> params) {
		declare_dependency(params);
	}

	public void declare_dependency(HashMap<String, String> params) {
		IRelationDefinerFacade mediator = AspectFactory.createMediator();
		mediator.addRule_dependency(params);
		
	}
	
	public void declare_independency(ArrayList<String> listOfAspectNames) {
		IRelationDefinerFacade mediator = AspectFactory.createMediator();
		mediator.addRule_choice(listOfAspectNames);
	}
	
	public void declare_independency(String... someAspects) {
		IRelationDefinerFacade mediator = AspectFactory.createMediator();
		ArrayList<String> listOfAspectNames = new ArrayList<String>();
		for (int i = 0; i < someAspects.length; i++) {
			listOfAspectNames.add(someAspects[i]);
		}
		mediator.addRule_choice(listOfAspectNames);
		
	}
	

	
	
	
	
	
}
