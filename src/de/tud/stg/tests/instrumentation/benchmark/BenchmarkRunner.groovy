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
package de.tud.stg.tests.instrumentation.benchmark

import java.lang.management.ManagementFactory
import java.lang.management.GarbageCollectorMXBeanimport java.lang.management.MemoryMXBeanimport java.lang.management.MemoryPoolMXBean
import de.tud.stg.popart.aspect.extensions.Booter;
import de.tud.stg.popart.aspect.extensions.instrumentation.PopartInterestCache;
import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationCache;
import de.tud.stg.popart.aspect.extensions.itd.structuredesignators.StructureDesignator;
import de.tud.stg.popart.pointcuts.Pointcut;
/**
 * @author Jan Stolzenburg
 */
abstract class BenchmarkRunner {
	
	protected boolean debug
	private PrintStream originalStdOut
	private Map<String, Long> result = [:]
	
	private long startGcCount
	private long startGcTime
	private long startTime
	
	private long endTime
	private long endGcTime
	private long endGcCount
	private long endMemUsage
	private long endMemPeakUsage
	private long endMemNonHeapUsage
	private long endMemHeapUsage
	
	private List<GarbageCollectorMXBean> gcInfo = ManagementFactory.garbageCollectorMXBeans
	private MemoryMXBean memoryInfo = ManagementFactory.memoryMXBean
	private List<MemoryPoolMXBean> memoryPoolInfo = ManagementFactory.memoryPoolMXBeans
	
	protected BenchmarkRunner(BenchmarkAspectMode aspectMode, boolean enablePartialEvaluation, boolean enableInterTypeDeclarationCache, boolean debug) {
		this.debug = debug
		
		Booter.initializeOnlyAspectSystem();
		
		switch(aspectMode){
			case BenchmarkAspectMode.MATCHING_ASPECT:
				BenchmarkAspect.registerAspect(getBenchmarkSpecificClassDesignator());
				break;
			case BenchmarkAspectMode.NOT_MATCHING_ASPECT:
				BenchmarkAspect.registerNonMatchingAspect();
				break;
			default:
				//No aspect.
				break;
		}
				
		PopartInterestCache.setEnablePopartOptimization(enablePartialEvaluation);
		InterTypeDeclarationCache.setEnabled(enableInterTypeDeclarationCache);
	}
	
	protected void measure(int warmups) {
		redirectStdOutToStdErr()

		warmups.times{
			println "Starting: Warmup run "+(it+1)+" of $warmups"
			runBenchmark()
			println "Done: Warmup run "+(it+1)+" of $warmups"
		}

		println "Starting: Timed run"
		collectDataPreStart()
		runBenchmark()
		collectDataPostStart()
		println "Done: Timed run"

		printLog()
		restoreStdOut()

		calcResult()
		returnResult()
	}
	
	/**
	 * This method should return a structure designator matching all classes
	 * involved in the benchmarks workload.
	 */
	protected abstract StructureDesignator getBenchmarkSpecificClassDesignator();

	/**
	 * This method runs the bechmark. To be implemented by subclasses.
	 */
	protected abstract void runBenchmark();
	
	/**
	 * We use the original "System.out" to return the result.
	 * Therefore, we cannot use it for debugging output.
	 * But if we exchange "System.out" with "System.err", the debug output can be passed to the parent process.
	 */
	private void redirectStdOutToStdErr() {
		originalStdOut = System.out
		System.out = System.err
	}
	
	private void collectDataPreStart() {
		memoryInfo.gc()
		
		startGcCount = gcInfo*.collectionCount.sum()
		startGcTime = gcInfo*.collectionTime.sum()
		startTime = System.nanoTime()
	}
	
	private void collectDataPostStart() {
		endTime = System.nanoTime()
		endGcTime = gcInfo*.collectionTime.sum()
		endGcCount = gcInfo*.collectionCount.sum()
		
		/*
		 * To get more precise results, we call the GC before we request the memory data to reduce them to the actual minimum.
		 * But as this can take some time (and weight the time data), we call the GC after we requested the time data.
		 */
		memoryInfo.gc()
		
		endMemUsage = memoryPoolInfo*.usage*.used.sum()
		endMemPeakUsage = memoryPoolInfo*.peakUsage*.used.sum()
		endMemNonHeapUsage = memoryInfo.nonHeapMemoryUsage.used
		endMemHeapUsage = memoryInfo.heapMemoryUsage.used
	}
	
	private void printLog() {
		if (!debug) return
		println 'Log:' + BenchmarkAspect.logger;
	}
	
	private void restoreStdOut() {
		System.out = originalStdOut
	}
	
	private void calcResult() {
		result.time = (endTime - startTime)
		result.gcTime = (endGcTime - startGcTime)
		result.gcCount = (endGcCount - startGcCount)
		result.memUsage = endMemUsage
		result.memPeakUsage = endMemPeakUsage
		result.memNonHeapUsage = endMemNonHeapUsage
		result.memHeapUsage = endMemHeapUsage
	}
	
	private void returnResult() {
		//Print results to "System.out", which is intercepted by the parent process.
		println(result)
	}
}