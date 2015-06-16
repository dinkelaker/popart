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
package  de.tud.stg.popart.aspect.extensions;

import java.util.Set;

import de.tud.stg.popart.aspect.AdviceDSL;
import de.tud.stg.popart.aspect.CCCombiner;
import de.tud.stg.popart.aspect.PointcutDSL;
import de.tud.stg.popart.dslsupport.DSL;

/**
 * @author olga
 */
public class RuleBasedCCCombiner extends CCCombiner {
	
	public static RuleBasedDSL defaultRuleBasedDSL = new RuleBasedDSL(); 
	private RuleBasedDSL ruleBasedDSL;
	
	public RuleBasedCCCombiner() {
		super();
		ruleBasedDSL = defaultRuleBasedDSL;
		dslDefinitions.add(ruleBasedDSL);
		
	}
	
	public RuleBasedCCCombiner(AdviceDSL _adviceDSL, PointcutDSL _pointcutDSL) {
		super(_adviceDSL,_pointcutDSL);
		ruleBasedDSL = defaultRuleBasedDSL;
		dslDefinitions.add(ruleBasedDSL);
	}
	
	public RuleBasedCCCombiner(Set<DSL> dsls) {
		super(dsls);
		ruleBasedDSL = defaultRuleBasedDSL;
		dslDefinitions.add(ruleBasedDSL);
	}	
}
