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

import groovy.lang.Closure;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClassRegistry;
import java.util.Comparator;
import java.util.Map;

import de.tud.stg.popart.aspect.extensions.definers.RelationDefinerFacade;
import de.tud.stg.popart.dslsupport.ContextDSL;

/**
 * @author Tom Dinkelaker
 */
public abstract class AspectFactory {
	protected static AspectFactory instance;
	static{
		setInstance(new AspectFactoryImpl());
	}

	private static Comparator<? super AspectMember> defaultComparator = new PointcutAndAdviceComparator<AspectMember>();
	
	private static RelationDefinerFacade defaultMediator =  new RelationDefinerFacade(); 

	public static AspectFactory setInstance(AspectFactory newInstance) {
		instance = newInstance;
		instance.initAspectMetaObjectClass();
		return instance;
	}

	public static AspectFactory getInstance() {
		return instance;
	}

	public abstract Aspect createAspect(Map<String,Object> params, ContextDSL interpreter, Closure definition);

	/**
	 * Initialize the aspect metaobject class.
	 */
	public abstract void initAspectMetaObjectClass();

	/**
	 * registers the default {@link MetaAspect} to be the given classes
	 * metaClass.
	 * @param aspectClass the aspect class
	 */
	protected void setDefaultMetaAspectFor(Class<?> aspectClass){
		MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
		synchronized(registry){
			registry.setMetaClass(aspectClass, new MetaAspect(registry, aspectClass));
		}
	}

	/**
	 * Retrieves the default Comparator.
	 * @return the comparator
	 */
	public static Comparator<? super AspectMember> getDefaultComparator() {
		return defaultComparator;
	}
	
	/**
	 * Returns the default Mediator.
	 */
	public static RelationDefinerFacade createMediator() {
		return defaultMediator;
	}

	public static void setDefaultComparator(Comparator<? super AspectMember> comparator){
		defaultComparator = comparator;
	}

	public static void setDefaultMediator(RelationDefinerFacade defaultMediator) {
		AspectFactory.defaultMediator = defaultMediator;
	}

}
