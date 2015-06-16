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

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
/**
 * @author Jan Stolzenburg
 */
public class BenchmarkStarter {
	
	private static String JAR_GROOVY = 'lib/groovy-all-1.7.0.jar'.replace('/', File.separator) //Unix-Windows-compatibility
	private static String JAR_ASPECT_J = 'lib/aspectjrt.jar'.replace('/', File.separator)
	private static String DIR_POPART = 'bin'
	private static String ROOT_DIR_RESULT = 'benchmarks/results/'.replace('/', File.separator)
	private static String FILE_CONFIG = 'benchmarks/config/benchmarksConfig.groovy'
	private static Class CLASS_BENCHMARK = Benchmark
	private static boolean DEBUG = true

	private String DIR_RESULT = ROOT_DIR_RESULT+String.format("benchmark_%1\$tY-%1\$tm-%1\$td_%1\$tH-%1\$tM-%1\$tS", Calendar.getInstance())+File.separator
	
	public static void main(String[] args) {
		new BenchmarkStarter().start()
//		def b = new BenchmarkStarter();
//		def allResults = b.reReadResultsFromFiles(ROOT_DIR_RESULT+"benchmark_2010-04-21_21-52-00")
//		b.writeResults(allResults);
//		b.writeResultsVerbose(allResults);
	}
	
	public void start() {
		List<BenchmarkConfiguration> config = loadConfig()
		Map allResults = [:]
		config.eachWithIndex {BenchmarkConfiguration configEntry, int index ->
			println("Benchmark ${index + 1} of ${config.size()}; Setup: $configEntry")
			List<Map> results = runBenchmark(configEntry)
			if (! results.isEmpty()) { //Will be empty when it crashed every time
				allResults[configEntry.toMap()] = summarize(results)
			}
		}
		writeResults(allResults)
		writeResultsVerbose(allResults)
		println("All Benchmarks finished.")
	}
	
	private Map reReadResultsFromFiles(String dir){
		try {
			def allResults = [:]
			def criteria = ["gcCount", "gcTime", "memHeapUsage", "memNonHeapUsage", "memPeakUsage", "memUsage", "time"];
			for(String criterion : criteria){
				File file = new File(dir+File.separator+"${criterion}.verbose.csv")
				def configSettingsHealine =["warmups","benchmark","args","aspectMode","mopMode","enablePartialEvaluation","enableInterTypeDeclarationCache","passes","heapSize","maxWait","passes"];
				def summarizedResultsHealine = ["sum","pass0","pass1","pass2","pass3","mean","median","minAbs","maxAbs","spreadAbs","minRelMean","maxRelMean","spreadRelMean","minRelMedian","maxRelMedian","spreadRelMedian"]
				file.eachLine {String line, int lineNr ->
					if(lineNr != 1){
						StringTokenizer t = new StringTokenizer(line, ',');
						
						Map config = [:];
						configSettingsHealine.each{
							config[it] = t.nextToken();
						}
						if(!allResults[config]) allResults[config] = [:]
						
						Map summarizedResults = [:];
						summarizedResultsHealine.each{
							if(t.hasMoreElements()) summarizedResults[it] = t.nextToken();
						}
						
						assert(!t.hasMoreTokens());
						allResults[config][criterion]= summarizedResults; 
					}
				}
			}
			return allResults;
		} catch(IOException exception) {
			throw new RuntimeException("An error occurred when I tried to create the result file and write the results.", exception)
		}
	}
	
	/**
	 * @param results: List of results from multiple passes of the same benchmark configuration.
	 * @return Map: summarizedResults[criterion => results[summaryType => resultValue]]
	 */
	private Map summarize(List<Map> results) {
		List<String> criteria = results.first().keySet().asList()
		assert results*.keySet()*.asList()*.equals(criteria).every() //Everywhere the same keys and in the same order.
		/*
		 * If the benchmark run crashes, the actual number of results will be smaller than the configured number of passes.
		 * Therefore, we store how many results we actually used to calculate the mean.
		 */
		Map summarizedResults = [:]
		criteria.each {String criterion ->
			Map sumResult = [:]
			List<Long> theResults = results*.get(criterion)
			sumResult.passes = results.size()
			sumResult.sum = theResults*.toLong().sum()
			theResults.eachWithIndex {value, i -> sumResult["pass$i"] = value}
			sumResult.mean = sumResult.sum / sumResult.passes
			sumResult.median = theResults.sort()[(int)(sumResult.passes / 2)]
			if (sumResult.passes > 1) {
				sumResult.minAbs = theResults.min()
				sumResult.maxAbs = theResults.max()
				sumResult.spreadAbs = sumResult.maxAbs - sumResult.minAbs
				if (sumResult.mean != 0) {
					sumResult.minRelMean = sumResult.minAbs / sumResult.mean
					sumResult.maxRelMean = sumResult.maxAbs / sumResult.mean
					sumResult.spreadRelMean = sumResult.spreadAbs / sumResult.mean
				}
				if (sumResult.median != 0) {
					sumResult.minRelMedian = sumResult.minAbs / sumResult.median
					sumResult.maxRelMedian = sumResult.maxAbs / sumResult.median
					sumResult.spreadRelMedian = sumResult.spreadAbs / sumResult.median
				}
			}
			if(criterion == "time" || criterion == "gcTime"){
				//convert time from nanoseconds to readable seconds.milliseconds
				summarizedResults[criterion] = [:]
				sumResult.each{key, value ->
					if(key == "passes")
						summarizedResults[criterion][key] = (value / 1000000) / 1000.0
					else
						summarizedResults[criterion] = sumResult
				}
			}else{
				summarizedResults[criterion] = sumResult
			}
		}
		return summarizedResults
	}
	
	/**
	 * 'allResults' is a map of the form: results[configMap[configOptionKey => configOptionSetting] => resultMap[resultCriterion => resultTypes[summaryType => resultValue]]]
	 * 'summaryType' is for example "mean", "min" or "max"
	 * 'resultCriterion' is for example 'time' or 'GC-count'
	 */
	private void writeResults(Map allResults, String groupBy = 'warmups', summaryType = 'mean') {
		try {
			new File(ROOT_DIR_RESULT).mkdir()
			new File(DIR_RESULT).mkdir()
			def criteria = allResults.values().asList().first().keySet()
			criteria.each {String criterion ->
				File file = new File("${DIR_RESULT}${criterion}.csv")
				Map groupedResults = [:]
				allResults.each {Map configMap, Map resultMap ->
					Map reducedConfig = configMap.findAll {it.key != groupBy}
					if (!groupedResults.containsKey(reducedConfig))
						groupedResults[reducedConfig] = [:]
					groupedResults[reducedConfig][configMap[groupBy]] = resultMap[criterion][summaryType]
				}
				String configSettingsHealine = groupedResults.keySet().asList().first().keySet().join(',')
				String summarizedResultsHealine = groupedResults.values().asList().first().keySet().join(',')
				file << configSettingsHealine << ',' << summarizedResultsHealine << '\n'
				groupedResults.each {Map config, Map results ->
					def configSettings = config.values().join(',')
					def summarizedResults = results.values().join(',')
					file << configSettings << ',' << summarizedResults << '\n'
				}
			}
		} catch(IOException exception) {
			throw new RuntimeException("An error occurred when I tried to create the result file and write the results.", exception)
		}
	}
	
	//Can be used for debugging
	private void writeResultsVerbose(Map allResults) {
		try {
			new File(ROOT_DIR_RESULT).mkdir()
			new File(DIR_RESULT).mkdir()
			def criteria = allResults.values().asList().first().keySet()
			criteria.each {String criterion ->
				File file = new File("${DIR_RESULT}${criterion}.verbose.csv")
				String configSettingsHealine = allResults.keySet().asList().first().keySet().join(',')
				String summarizedResultsHealine = allResults.values().asList().first().values().asList().first().keySet().join(',')
				file << configSettingsHealine << ',' << summarizedResultsHealine << '\n'
				allResults.each {Map config, Map criteriaSortedResults ->
					def configSettings = config.values().join(',')
					def summarizedResults = criteriaSortedResults[criterion].values().join(',')
					file << configSettings << ',' << summarizedResults << '\n'
				}
			}
		} catch(IOException exception) {
			throw new RuntimeException("An error occurred when I tried to create the result file and write the results.", exception)
		}
	}
	
	
	private List<Map> loadConfig() {
		try {
			return Eval.me(new File(FILE_CONFIG).text)
		} catch(IOException exception) {
			throw new RuntimeException("An error occurred when I tried to open and read the configuration file.", exception)
		}
	}
	
	/**
	 * @return A list containing one map per "passes".
	 * Each map contains the results from that run.
	 * If a run crashes, no result is put into the list.
	 * Therefore, the list is empty, if every run chrashed.
	 */
	private List<Map> runBenchmark(BenchmarkConfiguration config) {
		def results = []
		config.passes.times { index ->
			print("\tRun ${index + 1} of $config.passes")
			def start = System.currentTimeMillis()
			//The increased stack size is necessary for the fibonacci benchmark. The recursion in combination with the MOP-Instrumentation causes StackOverflowExceptions even for fib(10).
			String command
			if(config.mopMode == null){
				command = "java -server -Xss4M -Xms$config.heapSize -Xmx$config.heapSize -classpath $JAR_GROOVY:$DIR_POPART:$JAR_ASPECT_J $CLASS_BENCHMARK.canonicalName $config.warmups $config.benchmark $config.aspectMode $config.enablePartialEvaluation $config.enableInterTypeDeclarationCache $config.args"
			}else{
				command = "java -server -Xss4M -Xms$config.heapSize -Xmx$config.heapSize -classpath $JAR_GROOVY:$DIR_POPART:$JAR_ASPECT_J $CLASS_BENCHMARK.canonicalName $config.warmups $config.benchmark $config.aspectMode $config.enablePartialEvaluation $config.enableInterTypeDeclarationCache $config.mopMode $config.args"
			}
//			println "Command is: "+command
			if(File.separator.equals("\\")) command = command.replace(':', ';'); //win needs ; instead of :
			Process process = command.execute()
			def pout = new StringBuffer()
			def perr = new StringBuffer()
			process.consumeProcessOutput(pout, perr)
			process.waitForOrKill(config.maxWait)
			process.waitFor() //Workaround for a bug: Killing the process takes some time (a second or two). But process.exitValue() will fail if it is called and the process has not ended.
			def end = System.currentTimeMillis()
			long duration = end - start
			long seconds = (duration / 1000).toLong() % 60 //"toLong()" prevents a stupid runtime exception: "UnsupportedOperationException: Cannot use mod() on this number type: java.math.BigDecimal"
			long minutes = (duration / (1000*60)).toLong() % 60
			long hours = (duration / (1000*60*60))
			if (process.exitValue() == 0) { //No error
				println(" finished after $hours hours, $minutes minutes and $seconds seconds")
				if (DEBUG) println(perr)
				try {
					results << Eval.me(pout.toString())
				} catch (org.codehaus.groovy.control.MultipleCompilationErrorsException exception) {
					println('StdOut is:')
					println('---')
					println(pout.toString())
					println('---')
					throw exception
				}
				assert(results.last() instanceof Map)
			} else { //An error occured
				println(" FAILED after $hours hours, $minutes minutes and $seconds seconds")
				if (DEBUG) {
					System.err.println("Exit code: ${process.exitValue()}")
					System.err.println(perr)
				}
			}
		}
		return results
	}
}