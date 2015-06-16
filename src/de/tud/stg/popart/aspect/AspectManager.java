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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import de.tud.stg.popart.dslsupport.ContextDSL;
import de.tud.stg.popart.joinpoints.AdviceExecutionJoinPoint;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;
import de.tud.stg.popart.pointcuts.BooleanPCD;
import de.tud.stg.popart.pointcuts.Pointcut;

/**
 * A test to invoke a Groovy script from Java.
 * 
 * @author Tom Dinkelaker
 */
public class AspectManager implements IAspectManager { 

	protected final static boolean DEBUG = true;

	protected final static boolean DEBUG_EXCEPTIONS = true;

	/** Singleton variable . **/
	private static AspectManager aspectManager = null;

	/**
	 * Stores all the registered aspects in the order they were registered,
	 * preventing duplicate registrations.
	 */
	private LinkedHashSet<Aspect> aspects = 
		new LinkedHashSet<Aspect>();;

	/**
	 * Stores the aspect change listeners.
	 */
	private Set<AspectChangeListener> aspectChangeListeners = new java.util.HashSet<AspectChangeListener>();
	
	// keeps a separate JoinPoint Stack for each thread
	private Map<Thread,LinkedList<JoinPoint>> joinPointsOnStackPerThread = 
		new java.util.WeakHashMap<Thread,LinkedList<JoinPoint>>();

//	// stores the current advice interpreter relative to its thread id
//	private Map<Thread, ContextDSL> currentAdviceInterpreters =
//		new java.util.HashMap<Thread, ContextDSL>();

	// stores the current advice interpretation context relative to its thread id
	private Map<Thread, Map<String,Object>> adviceInterpretationContext =
		new java.util.WeakHashMap<Thread, Map<String,Object>>();

	// explicit lock to protect advice interpretation
	private ReentrantLock adviceInterpretationLock = new ReentrantLock();
	// saves condition variables for use with 'adviceInterpretationLock'
	private Map<Object, java.util.concurrent.locks.Condition> lockConditions =
		new java.util.WeakHashMap<Object, java.util.concurrent.locks.Condition>();
	
	/**
	 * A set of threads, for whose executions are currently not to
	 * spawn any join points.
	 */
	private Set<Thread> noJoinPointThreadSet = Collections.synchronizedSet(new java.util.HashSet<Thread>());
	
	/**
	 * disables spawning of join points within the current thread.
	 */
	public void disableJoinPointSpawningForCurrentThread(){
		noJoinPointThreadSet.add(Thread.currentThread());
	}
	
	/**
	 * enables spawning of join points within the current thread.
	 */
	public void enableJoinPointSpawningForCurrentThread(){
		noJoinPointThreadSet.remove(Thread.currentThread());
	}
	
	/**
	 * determines, whether the current thread should spawn new join points
	 * during its execution. This must be checked before any of the methods
	 * {@link #fireJoinPointBeforeToAspects(JoinPoint)},
	 * {@link #fireJoinPointAroundToAspects(JoinPoint)} or
	 * {@link #fireJoinPointAfterToAspects(JoinPoint)} are called. These
	 * methods must not be called, if join point spawning is disabled.
	 * @return <code>true</code> if join points should be spawned, 
	 * 		<code>false</code> otherwise.
	 */
	public boolean isJoinPointSpawningEnabledForCurrentThread(){
		return !noJoinPointThreadSet.contains(Thread.currentThread());
	}

	/**
	 * This Method suspends a thread on behalf of an advice.
	 * 
	 * If an advice wants to suspend a thread, intrinsic locking as done with
	 * a synchronized block would cause the AspectManager to deadlock. The reason
	 * is, that currently an adviceInterpretationLock is used to make the call
	 * to the advice interpreter atomic to its context setting (otherwise another
	 * thread may "steal" the context resulting in wrong behaviour). So while
	 * the advice is interpreted, the adviceInterpretationLock is set. If the
	 * advice itself now suspends a thread, the adviceInterpretationLock would
	 * never be freed. In order to allow an advice to suspend anyway, this method
	 * here releases the adviceInterpretationLock before it suspends the current
	 * thread such that other threads may interpret advice.
	 *
	 * However, all locks including the ones used in an advice body must ALWAYS
	 * be acquired in the same order. Otherwise there are potential deadlock
	 * situations. By intrinsic locking inside the advice, this order would be
	 * different. So an advice must use an explicit lock (= 'externalLock').
	 * For each external lock, a new condition exists for use with
	 * 'adviceInterpretationLock' which is used to wait on.
	 * @param externalLock  the lock used inside an advice
	 * @author Oliver Rehor
	 * @throws InterruptedException when the Thread is woken up. In that case
	 *         all locks are acquired in correct order as well (finally block).
	 */ 
	public void wait(ReentrantLock externalLock) throws InterruptedException {
		if (!lockConditions.containsKey(externalLock))
			lockConditions.put(externalLock, adviceInterpretationLock.newCondition());
		externalLock.unlock();
		try {
			// await releases and reaquires 'adviceInterpretationLock' atomically!
			lockConditions.get(externalLock).await();
		} finally {
			externalLock.lock();
//			Thread currentThread = Thread.currentThread();
//			currentAdviceInterpreters.get(currentThread).setContext(adviceInterpretationContext.get(currentThread));
		}
	}

	/**
	 * This method wakes up all threads waiting on the condition associated
	 * with the externalLock.
	 * @param externalLock  the lock for which threads waiting on 
	 *                      'adviceInterpretationLock'  should be notified
	 */
	public void notifyAll(ReentrantLock externalLock) {
		if (lockConditions.containsKey(externalLock))
			lockConditions.get(externalLock).signalAll();
	}

	/**
	 * Sets the 'adviceInterpretationLock' (for use in 'WrappingProceed')
	 */
	public void acquireAdviceInterpretationLock() {
		adviceInterpretationLock.lock();
	}

	/**
	 * Releases the 'adviceInterpretationLock' and restores the current
	 * interpreter (for use in 'WrappingProceed')
	 * @param theCurrentInterpreter  the interpreter which should be the current
	 */
	public void releaseAdviceInterpretationLock() {
//	public void releaseAdviceInterpretationLock(ContextDSL theCurrentInterpreter) {
//		currentAdviceInterpreters.put(Thread.currentThread(), theCurrentInterpreter);
		if (adviceInterpretationLock.isHeldByCurrentThread())
			adviceInterpretationLock.unlock();
	}
	// END:::[ OR-2009-08-03: Thread-Safety Modification ]::::::::::::::::::::::

//	final private static String PATH_TO_ASPECT_DIR = "src/de/tud/stg/example/aop/".replace("/", File.separator);

	protected AspectManager() {
	}

	public static synchronized AspectManager getInstance() {
		if (aspectManager == null) {
			aspectManager = AspectManagerFactory.getInstance().createAspectManager();
		}
		return aspectManager;
	}

	public synchronized LinkedList<JoinPoint> getCurrentJoinPointStack() {
		LinkedList<JoinPoint> currentJPStack = joinPointsOnStackPerThread.get(Thread.currentThread());
		if (currentJPStack == null) {
			currentJPStack = new LinkedList<JoinPoint>();
			joinPointsOnStackPerThread.put(Thread.currentThread(), currentJPStack);
		}
		return currentJPStack;
	}
	// =====================================================================

	/**
	 * Note that there may be multiple aspects with the same name!
	 * This implementation will return the aspect, which has the given
	 * name and was registered the earliest.
	 * @param name the name
	 * @return the aspect, or <code>null</code> if none.
	 */
	public Aspect getAspect(String name) {
		synchronized (aspects) {
			for(Aspect aspect : aspects){
				if(aspect.getName().equals(name)) return aspect;
			}
			return null;
		}
	}

	public boolean hasAspect(String name) {
		synchronized (aspects) {
			return getAspect(name) != null;
		}
	}

	/**
	 * Retrieves a copy of the list of all registered aspects.
	 * @return Returns a list of all registered aspects.
	 */
	public List<Aspect> getAspects() {
		synchronized (aspects) {
			return new java.util.ArrayList<Aspect>(aspects);
		}
	}

	public void register(Aspect aspect) {
		synchronized (aspects) {
			aspects.add(aspect);
		}
		notifyAspectChangeListeners();
	}

	public void unregister(Aspect aspect) {
		synchronized (aspects) {
			aspects.remove(aspect);
		}
		notifyAspectChangeListeners();
	}

	/**
	 * This metod remove all registered aspect from the aspect manager.
	 */
	public void unregisterAllAspects() {
		synchronized (aspects) {
			aspects.clear();
		}
	}
	
	private Set<Aspect> calculateAspectInterferenceSet(List<PointcutAndAdvice> applicablePAs) {
		Set<Aspect> aspectInterferenceSet = new java.util.HashSet<Aspect>();
		for(PointcutAndAdvice pa : applicablePAs){
			aspectInterferenceSet.add(pa.getAspect());
		}
		if (DEBUG) {
			System.out.print("AspectManager.calculateAspectInterferenceSet: \t\t interfering aspects={");
			for(Aspect aspect : aspectInterferenceSet){
				System.out.print(aspect.getName() + ",");
			}
			System.out.println("}");
		}
		return aspectInterferenceSet;
	}

	private void reportInterferenceToAllConcerningAspects(JoinPoint jp,
			Set<Aspect> aspectInterferenceSet, List<PointcutAndAdvice> applicablePAs) {
		if (aspectInterferenceSet.size() > 1) {
			interactionAtJoinPoint(jp, aspectInterferenceSet, applicablePAs);
		}
	}

	/**
	 * Invokes all pointcut-and-advice that match for teh current join point.
	 */
	public void invokeAllApplicablePointcutAndAdvice(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {
		if (DEBUG)System.out.println("AspectManager.fireJoinPoint*ToAspects: \t\t applicablePAs=" + applicablePAs);

		// First determine if we are currently handling around advice
		boolean receivingAround = applicablePAs.get(0) instanceof AroundPointcutAndAdvice;

		if (!receivingAround) {
			invokeAllApplicablePointcutAndAdviceBeforeOrAfter(jp, applicablePAs);
		} else {
			// Around advice are treated specially because a wrapping proceed is
			// necessary to compose the advice.
			invokeAllApplicablePointcutAndAdviceAround(jp, applicablePAs);
		}
	}

	/**
	 * Invokes all (before or after) pointcut-and-advice that match for teh
	 * current join point.
	 */
	public void invokeAllApplicablePointcutAndAdviceBeforeOrAfter(JoinPoint jp, List<PointcutAndAdvice> applicablePAs) {

		if(applicablePAs.get(0) instanceof AfterPointcutAndAdvice) Collections.reverse(applicablePAs);

		if (DEBUG) System.out.println("AspectManager.invokeAll*BeforeOrAfter: \t\t applicablePAs=" + applicablePAs);
		for(PointcutAndAdvice pa : applicablePAs) {
			Aspect aspect = pa.getAspect();
			jp.context.put("thisAspect", aspect);
			jp.context.put("thisPointcut", pa.getPointcut());
			if (DEBUG) System.out.println("AspectManager.fireJoinPoint*ToAspects: \t\t executing advice " + pa);

			aspect.beforeCallingAdvice(jp, pa);
			//result of before or after advices is irrelevant
			invokeAdvice(jp, pa);
			aspect.afterCallingAdvice(jp, pa);
		}
	}

	/**
	 * Invokes all around pointcut-and-advice that match for teh current join
	 * point.
	 */
	public void invokeAllApplicablePointcutAndAdviceAround(final JoinPoint jp,
			List<PointcutAndAdvice> applicablePAs) {
		// if (DEBUG)
		// System.out.println("AspectManager.invokeAll*Around: \t\t applicablePAs="+applicablePAs);
		jp.context.put("applicablePAs", applicablePAs);

		IProceed proceedToShadow = (IProceed) jp.context.get("proceed");
		
		// Replaces the proceed in the context trough a wrapping proceed;
		WrappingProceed wrappedProceed = new WrappingProceed(proceedToShadow, this, jp, applicablePAs);
		jp.context.put("proceed", wrappedProceed);
		wrappedProceed.call(Collections.emptyList());

		// restore old unwrapped proceed
		jp.context.put("proceed", proceedToShadow);
	}

	public Object invokeAdvice(JoinPoint jp, PointcutAndAdvice pa) {
		AdviceExecutionJoinPoint aejp = new AdviceExecutionJoinPoint(pa, jp, "unknown", jp.context);
		
		LinkedList<JoinPoint> currentJPStack = null;
		synchronized (this) {
			currentJPStack = getCurrentJoinPointStack();
			currentJPStack.addLast(aejp);
		}

		// BEGIN:[ OR-2009-07-25: Thread-Safety Modification ]:::::::::::::::::::
		Object result;
		if (DEBUG) System.out.println("AspectManager.invokeAdvice(): try to set adviceInterpretationLock for " + Thread.currentThread().getId());
		adviceInterpretationLock.lock();
		// try|finally needed for safe use of 'adviceInterpretationLock'
		try {
			if (DEBUG) System.out.println("AspectManager.invokeAdvice(): have set adviceInterpretationLock for " + Thread.currentThread().getId());

			Aspect aspect = pa.getAspect();
			Closure advice = (Closure)pa.getAdvice().clone();
			ContextDSL interpreter = aspect.getInterpreter();

			advice.setDelegate(interpreter);
			// OWNER_FIRST:
			//  (+) owner-method-calls in advice works
			//  (-) POPART variables like "targetObject" don't, if owner itself
			//      implements getProperty/setProperty
			// DELEGATE_FIRST:
			//  (+) variables like targetObject work
			//  (-) owner-method-calls in advice don't, treated as missing methods
			//advice.setResolveStrategy(Closure.OWNER_FIRST);
			advice.setResolveStrategy(Closure.OWNER_FIRST);

//			currentAdviceInterpreters.put(Thread.currentThread(), interpreter);
			adviceInterpretationContext.put(Thread.currentThread(), jp.context);

			interpreter.setContext(jp.context);
			result = advice.call();
			if (DEBUG) System.out.println("AspectManager.invokeAdvice(): release adviceInterpretationLock for " + Thread.currentThread().getId());
		} catch (RuntimeException ex) {
			if (DEBUG_EXCEPTIONS) {
				System.out.println("AspectManager.invokeAdvice(): error nduring advice execution for " + Thread.currentThread().getId());
				ex.printStackTrace();
			}
			throw ex;
		} finally {
			adviceInterpretationLock.unlock();
			if (DEBUG) System.out.println("AspectManager.invokeAdvice(): done releasing adviceInterpretationLock for " + Thread.currentThread().getId());
		}
		// END:::[ OR-2009-07-25: Thread-Safety Modification ]:::::::::::::::::::

		synchronized (this) {
			currentJPStack.removeLast();
		}

		return result;
	}
	
	private void handleApplicablePAs(JoinPoint jp, List<PointcutAndAdvice> applicablePAs){
		// Calculate Aspect Interference Set
		Set<Aspect> aspectInterferenceSet = calculateAspectInterferenceSet(applicablePAs);
		if (DEBUG) System.out.println("AspectMananager.fireJoinPointAroundToAspects aspectInterferenceSet=" + aspectInterferenceSet);

		// Report interference to all concerning aspects
		if (DEBUG) System.out.println("AspectMananager.fireJoinPointAroundToAspects reporting interefence to all aspects");
		reportInterferenceToAllConcerningAspects(jp, aspectInterferenceSet, applicablePAs);

		// Invoke all applicable Advice
		if (DEBUG) System.out.println("AspectMananager.fireJoinPointAroundToAspects invoke all applicable advice applicablePAs=" + applicablePAs);
		invokeAllApplicablePointcutAndAdvice(jp, applicablePAs);
	}

	/**
	 * Fires a JoinPoint from the application before executing it.
	 * <p>
	 * An entry point to aspect world.
	 * </p>
	 * 
	 * @param shadowType
	 * @param codeLocation
	 * @param context
	 */
	public void fireJoinPointBeforeToAspects(JoinPoint jp) {
		if (DEBUG) System.out.println("---> AspectManager: Receiving before JP=" + jp);
		// EnclosingJoinPoint Context
		JoinPoint enclosingJoinPoint = null;

		synchronized (this) {
			LinkedList<JoinPoint> currentJPStack = getCurrentJoinPointStack();
			if (currentJPStack.size() > 0) {
				enclosingJoinPoint = currentJPStack.getLast();
			}
			currentJPStack.addLast(jp);

			jp.context.put("enclosingJoinPoint", enclosingJoinPoint);
			jp.context.put("joinPointStack", currentJPStack);//new java.util.ArrayList<JoinPoint>(currentJPStack));
		}


		List<PointcutAndAdvice> applicablePAs = new java.util.LinkedList<PointcutAndAdvice>();
		try {
			synchronized (aspects) {
				for(Aspect aspect : aspects){
					aspect.receiveBefore(jp, applicablePAs);
				}
			}
		} catch (RuntimeException e) {
			if (DEBUG) System.out.println("AspectManager: \t\t ERROR!");
			if (DEBUG) System.out.println("AspectManager: \t\t AspectManager.fireJoinPointBeforeToAspects(jp=" + jp + ").");
			if (DEBUG_EXCEPTIONS) e.printStackTrace();
			throw e;
		}
		
		if(applicablePAs.isEmpty()) return;
		handleApplicablePAs(jp, applicablePAs);
	}

	/**
	 * Fires a JoinPoint from the application around executing it.
	 * <p>
	 * An entry point to aspect world.
	 * </p>
	 * 
	 * @param shadowType
	 * @param codeLocation
	 * @param context
	 */
	public void fireJoinPointAroundToAspects(JoinPoint jp) {
		if (DEBUG) System.out.println("---> AspectManager: Receiving around JP=" + jp);
		List<PointcutAndAdvice> applicablePAs = new java.util.LinkedList<PointcutAndAdvice>();

		try {
			synchronized (aspects) {
				for(Aspect aspect : aspects){
					aspect.receiveAround(jp, applicablePAs);
				}
			}
		} catch (RuntimeException e) {
			if (DEBUG) System.out.println("AspectManager: \t\t ERROR!");
			if (DEBUG) System.out.println("AspectManager: \t\t AspectManager.fireJoinPointAroundToAspects(jp=" + jp + ").");
			if (DEBUG_EXCEPTIONS) e.printStackTrace();
			throw e;
		}

		if (applicablePAs.isEmpty()) {
			// (only) if the join point has not been advised (at least once)
			// the original join point actions must be executed implicitly
			// This Debug statement lead to an endless loop!!! //if (DEBUG)
			// System.out.println("AspectMananager.fireJoinPointAroundToAspects (implicit proceed) ctxt="+jp.context);
			List<Object> args = (List<Object>) jp.context.get("args");
			if (DEBUG) System.out.println("AspectMananager.fireJoinPointAroundToAspects (implicit proceed) args=" + args);
			Object result = ((IProceed) jp.context.get("proceed")).call(args);
			jp.context.put("result", result);
			if (DEBUG) System.out.println("AspectMananager.fireJoinPointAroundToAspects (implicit proceed) result=" + result);
			return;
		}
		if (DEBUG) System.out.println("AspectMananager.fireJoinPointAroundToAspects jp was advised (no implict proceed)");
		handleApplicablePAs(jp, applicablePAs);
	}

	/**
	 * Fires a JoinPoint from the application after executing it.
	 * <p>
	 * An entry point to aspect world.
	 * </p>
	 * 
	 * @param shadowType
	 * @param codeLocation
	 * @param context
	 */
	public void fireJoinPointAfterToAspects(JoinPoint jp) {
		if (DEBUG) System.out.println("---> AspectManager: Receiving after JP=" + jp);
		List<PointcutAndAdvice> applicablePAs = new java.util.LinkedList<PointcutAndAdvice>();

		try {
			synchronized (aspects) {
				for(Aspect aspect : aspects){
					aspect.receiveAfter(jp, applicablePAs);
				}
			}
		} catch (RuntimeException e) {
			if (DEBUG) System.out.println("AspectManager: \t\t ERROR!");
			if (DEBUG) System.out.println("AspectManager: \t\t AspectManager.fireJoinPointAfterToAspects(jp=" + jp + ").");
			if (DEBUG_EXCEPTIONS) e.printStackTrace();
			throw e;
		} finally {
			synchronized (this) {
				LinkedList<JoinPoint> currentJPStack = getCurrentJoinPointStack();
				currentJPStack.removeLast();
			}
		}
		
		if(applicablePAs.isEmpty()) return;
		handleApplicablePAs(jp, applicablePAs);
	}

	/**
	 * Partial evaluates every pointcut for the combination of this class and method.
	 * The result is stored/cached in the pointcuts.
	 * @return Is at least one of the pointcuts potentially interested in this method?
	 * @author Jan Stolzenburg
	 */
	public boolean partialEval(StaticJoinPoint joinPoint) {
		boolean interested = false;
		for (Aspect aspect : aspects) {
			for (PointcutAndAdvice pointcutAndAdvice : aspect.findAllPointcutsAndAdvice()) {
				Pointcut residual = pointcutAndAdvice.getPointcut().partialEval(joinPoint);
				if (residual == BooleanPCD.ALWAYS) interested = true;
			}
		}
		return interested;
	}

	// METHODS FOR EXTENSIONS

	/**
	 * The default implementation only reports intercations to aspect instances.
	 */
	public void interactionAtJoinPoint(JoinPoint jp, Set<Aspect> aspectInterferenceSet, List<PointcutAndAdvice> applicablePAs) {
		jp.context.put("aspectInteractionSet",aspectInterferenceSet);
		for(Aspect aspect : aspectInterferenceSet){
			aspect.interactionAtJoinPoint(jp, aspectInterferenceSet, applicablePAs);
		}
	}

	/**
	 * This method must be called by aspects, when a new advice is added to them,
	 * or an existing advice is removed.
	 * A kind of observer pattern. Modified to work with closures and unregistration.
	 */
	public void aspectChanged() {
		notifyAspectChangeListeners();
	}

	/**
	 * Informs all aspect change listeners that a new aspect or advice was added, or
	 * an existing aspect or advice removed.
	 * This method is intended to be called or overridden by subclasses, when appropriate.
	 * Other classes must not call this method.
	 */
	protected void notifyAspectChangeListeners() {
		for(AspectChangeListener listener : aspectChangeListeners){
			listener.aspectsChanged();
		}
	}

	/**
	 * Registers the listener for aspect change events.
	 * The listenerCallback is called whenever a new aspect or advice is defined,
	 * or an aspect or advice is deleted.
	 * @param key The key is only used to identify the listenerCallback when it should be unregistered
	 * @param listenerCallback The closure that should be called on aspect change events
	 */
	public void registerAspectChangeListener(AspectChangeListener listener) {
		aspectChangeListeners.add(listener);
	}

	/**
	 * Unregisters the listener registered under the given key.
	 * @param key The same key that was used when the listener was registered
	 */
	public void unregisterAspectChangeListener(AspectChangeListener listener) {
		aspectChangeListeners.remove(listener);
	}

	/**
	 * Should be called before application shuts down.
	 */
	public void initialize() {
		// nothing to do in the default implementation
	}

	/**
	 * Should be called before application shuts down.
	 */
	public void finalize() {
		// nothing to do in the default implementation
	}

}
