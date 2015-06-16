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
package de.tud.stg.tests.interactions.popart.rules

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectManagerFactory;
import de.tud.stg.popart.aspect.AspectMember;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManager;
import de.tud.stg.popart.aspect.extensions.OrderedAspectFactoryImpl;
import de.tud.stg.popart.aspect.CCCombiner;
import de.tud.stg.popart.aspect.extensions.OrderedAspectManager;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManagerFactoryImpl;
import de.tud.stg.popart.aspect.extensions.InteractionAwareAspectManagerFactoryImpl;
import de.tud.stg.popart.aspect.extensions.OrderedAspectManagerFactoryImpl;
import de.tud.stg.popart.aspect.extensions.RuleBasedCCCombiner;
import de.tud.stg.popart.aspect.extensions.definers.IRelationExaminer;
import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.exceptions.RuleInconsistencyException;
import de.tud.stg.popart.aspect.extensions.comparators.PrecedenceComparator;


/**
 * @author olga
 *
 */

void initializeNoRules() {
	AspectFactory.setInstance(new OrderedAspectFactoryImpl());
	AspectManagerFactory.setInstance(new InteractionAwareAspectManagerFactoryImpl());	
}

void initializeRules() {
	AspectFactory.setInstance(new OrderedAspectFactoryImpl());
	AspectManagerFactory.setInstance(new InteractionAwareAspectManagerFactoryImpl());
	
	AspectFactory.setDefaultComparator(new PrecedenceComparator());
	AspectFactory.setDefaultMediator(new RelationDefinerFacade());
}



def n = 200;
def aspectBaseName = "a";
double taskTime = 0.0;
double taskTimeRules = 0.0;
double taskTimeSek = 0.0;
double taskTimeRulesSek = 0.0;
boolean R = true;
OverheadRules rules = new OverheadRules();
OverheadNoRules noRules = new OverheadNoRules();
if (R) {
	initializeRules();
	taskTimeRules = rules.getTaskTime(n, aspectBaseName);
	taskTimeRulesSek = taskTimeRules/1000;
	println "Task time RULES[s]: " + taskTimeRulesSek;
	println("-----------------------------")
	taskTime = noRules.getTaskTime(n, aspectBaseName);
	taskTimeSek = taskTime/1000;
	println "Task time NO RULES(RuleBasedAM)[s]: " + taskTimeSek;
	println("-----------------------------")
	double overhead = (taskTimeRules- taskTime)*100 / taskTime;
	println "Overhead[%]: " + overhead;
}
else {
	initializeNoRules();
	taskTime = noRules.getTaskTime(n, aspectBaseName);
	taskTimeSek = taskTime/1000;
	println "Task time NO RULES(RuleBasedAM)[s]: " + taskTimeSek;
}




