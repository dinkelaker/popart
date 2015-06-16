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

import de.tud.stg.popart.aspect.AspectFactory;
import de.tud.stg.popart.aspect.AspectManagerFactory;
import de.tud.stg.popart.aspect.extensions.instrumentation.*;
import de.tud.stg.popart.aspect.extensions.itd.*;

/**
 * This class can be changed to customize Popart for user-specific extensions and semantics.
 * <p>The class is user-specific and should not be commited to the SVN repository after changing.</p>
 * <p>only when major features are added and stable the version should be replaced in the repository.</p>
 * @author Tom Dinkelaker
 **/
public class Booter {
	/**
	 * This method will initialize everything.
	 * @see #initializeAspectSystem()
	 * @see #initializeInstrumentationAndMOP()
	 */
	public static void initialize() {
		if(aspectSystemInitialized && joinPointInstrumentationInitialized ) throw new RuntimeException("Do not call initialize multiple times!");
		initializeOnlyAspectSystem();
		initializeOnlyInstrumentationAndMOP();
	}
	
	public static boolean aspectSystemInitialized = false;
	public static boolean isAspectSystemInitialized() {
		return aspectSystemInitialized;
	}
	
	/**
	 * This method will initialize the aspect system. This includes things
	 * like setting the current aspect factory and aspect manager. This
	 * does NOT include setting up the meta classes and join point
	 * instrumentations for groovy! Usually, you want to use
	 * {@link #initialize()} instead.
	 */
	public static void initializeOnlyAspectSystem() {
		if(aspectSystemInitialized) throw new RuntimeException("The aspect system is already initialized!");
		aspectSystemInitialized=true;
		specializeAspectMetaObjectClass();
		specializeAspectManager();
	}

	protected static void specializeAspectMetaObjectClass() {
		//AspectFactory.setInstance(new AspectFactoryImpl());
		//AspectFactory.setInstance(new DynamicAspectFactoryImpl());
		//AspectFactory.setInstance(new CountingAspectFactoryImpl());
		//AspectFactory.setInstance(new ProfilingAspectFactoryImpl());
		//AspectFactory.setInstance(new RuleBasedAspectFactoryImpl()); 
		//AspectFactory.setInstance(new OrderedAspectFactoryImpl());
		AspectFactory.setInstance(new InterTypeDeclarationAspectFactory());
	}
	
	protected static void specializeAspectManager() {
		//AspectManagerFactory.setInstance(new AspectManagerFactoryImpl());
		//AspectManagerFactory.setInstance(new CountingAspectManagerFactoryImpl());
		//AspectManagerFactory.setInstance(new ProfilingAspectManagerFactoryImpl());
		//AspectManagerFactory.setInstance(new InteractionDetectorAspectManagerFactoryImpl());
		//AspectManagerFactory.setInstance(new FaultIsolationAspectManagerFactoryImpl());
		//AspectManagerFactory.setInstance(new OrderedAspectManagerFactoryImpl());
		//AspectManagerFactory.setInstance(new InteractionAwareAspectManagerFactoryImpl());
		AspectManagerFactory.setInstance(new InterTypeDeclarationAspectManagerFactory());
	}
		
	/**
	 * This configures all meta classes and initializes the default join point
	 * instrumentation for groovy. This does NOT set up the aspect system!
	 * Usually, you want to use {@link #initialize()} instead.
	 */
	public static void initializeOnlyInstrumentationAndMOP() {
		configureMetaClassCreationHandle();
		initializeJoinPointInstrumentation();
	}
	
	protected static void configureMetaClassCreationHandle(){
		//InstrumentationMetaClassCreationHandle.replaceMetaClassCreationHandleForInstrumentation();
		//InterTypeDeclarationMetaClassCreationHandle.replaceMetaClassCreationHandleForInterTypeDeclarations();
		InstrumentationInterTypeDeclarationMetaClassCreationHandle.replaceMetaClassCreationHandleForInstrumentationAndInterTypeDeclarations();
	}
	
	private static boolean joinPointInstrumentationInitialized = false;
	public static boolean isJoinPointInstrumentationInitialized() {
		return joinPointInstrumentationInitialized;
	}

	/**
	 * This configures default join point instrumentation for groovy. This
	 * does NOT set up the aspect system, and does NOT configure the meta
	 * classes! Usually, you want to use {@link #initialize()} instead.
	 */
	public static void initializeJoinPointInstrumentation() {
		if(joinPointInstrumentationInitialized) throw new RuntimeException("The join point instrumentation is already initialized!");
		joinPointInstrumentationInitialized = true;
		//Surround each method call with:
		// 1) Execution join point spawn
		InstrumentationActivator.declareJoinPoint(InstrumentationMethodExecution.class);
		// 2) Call join point spawn
		InstrumentationActivator.declareJoinPoint(InstrumentationMethodCall.class);
	}
}
