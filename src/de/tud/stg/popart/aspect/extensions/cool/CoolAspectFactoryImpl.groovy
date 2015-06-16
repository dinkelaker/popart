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
package de.tud.stg.popart.aspect.extensions.cool;

import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.MetaAspect;
import de.tud.stg.popart.aspect.PointcutAndAdvice;
import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.PointcutAndAdviceComparator;
import de.tud.stg.popart.aspect.extensions.cool.CoolAspect;
import de.tud.stg.popart.aspect.extensions.comparators.*;

/**
 * @author Tom Dinkelaker
 **/
public class CoolAspectFactoryImpl extends AspectFactory {
	
	/**
	 * Singleton comparator that contains the domainspecific precedence rules.
	 */
	private static PointcutAndAdviceComparator<PointcutAndAdvice> comparator =
		new RuleBasedPointcutAndAdviceComparator<PointcutAndAdvice>();
	
	public static PointcutAndAdviceComparator<PointcutAndAdvice> getComparator() {
		return comparator;
	}
	
	public static void setComparator(PointcutAndAdviceComparator<PointcutAndAdvice> cmp) {
		CoolAspectFactoryImpl.comparator = cmp;
	}
	
	public Aspect createAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition) {
		return new CoolAspect(params, interpreter, definition);
	}
	
	public void initAspectMetaObjectClass() {
		setDefaultMetaAspectFor CoolAspect
	}	

}
