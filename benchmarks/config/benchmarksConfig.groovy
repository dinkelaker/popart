/**
 * @author Joscha Drechsler
 */
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkAspectMode;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkConfiguration;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkConfigurationBuilder;
import de.tud.stg.tests.instrumentation.benchmark.BenchmarkMOPMode;

//Aspect mode selection
boolean includeMatchingAspect = true
boolean includeNonMatchingAspect = true
boolean includeNoAspect = true

// partial evaluation setup selection
boolean includeEnabledPartialEvaluation = true
boolean includeDisabledPartialEvaluation = true

// inter-type declaration cache setup selection
boolean includeEnabledInterTypeDeclarationCache = true
boolean includeDisabledInterTypeDeclarationCache = true

// MOP setup selection, only used for mop instrumented benchmarks.
boolean includeDisabledBoth = true
boolean includeEnabledInstrumentationOnly = true
boolean includeEnabledInterTypeDeclarationsOnly = true
boolean includeEnabledBoth = true

List<Integer> warmups = [0, 2, 5]

// MOP benchmark setups
// Default small parameters result in about 20 to 30 seconds duration
// Default large parameters result in about 2 or 3 minutes duration
Map<String,List<String>> mopBenchmarks = [
	'FibonacciGroovyMOP':["30", "34"],
	'CreditProcessMOP':["30000 4", "200000 4"],
	'BuilderPerfTestMOP':["64000", "1000000"],
	'spectralnorm':["150", "350"],
	'recursive':["8", "9"],
	'raytracer':["2 40", "2 75"],
	'partialsums':["200000", "1250000"],
	'fannkuch':["8", "9"],
	'binarytrees':["13", "15"],
]
// non-MOP benchmark setups
Map<String,List<String>> otherBenchmarks = [
	'FibonacciJavaAspectJ':mopBenchmarks['FibonacciGroovyMOP'],
	'CreditProcessAspectJ':mopBenchmarks['CreditProcessMOP'],
]
	
	
//number of repeats to average results
BenchmarkConfigurationBuilder.passes = 4
//heap sizes to test
List<String> heapSizes = ["64M", "256M", "1024M"]
//timeout limit per iteration (each warmup and timedrun)
int minutesPerIteration = 10;

Closure forAllWarmups = {Closure loop ->
	for(int warmup : warmups){
		BenchmarkConfigurationBuilder.warmups = warmup;
		BenchmarkConfigurationBuilder.maxWait = 1000*60*minutesPerIteration;
		loop();
	}
}

Closure forAllHeapSizes = {Closure loop ->
	for(String heapSize : heapSizes){
		BenchmarkConfigurationBuilder.heapSize=heapSize;
		loop();
	}
}

Closure forAllPartialEvaluationSetups = {Closure loop ->
	if(includeEnabledPartialEvaluation){
		BenchmarkConfigurationBuilder.enablePartialEvaluation = true
		loop()
	}
	if(includeDisabledPartialEvaluation){
		BenchmarkConfigurationBuilder.enablePartialEvaluation = false
		loop()
	}
}

Closure forAllInterTypeDeclarationCacheSetups = {Closure loop ->
	if(includeEnabledInterTypeDeclarationCache){
		BenchmarkConfigurationBuilder.enableInterTypeDeclarationCache = true
		loop()
	}
	if(includeDisabledPartialEvaluation){
		BenchmarkConfigurationBuilder.enableInterTypeDeclarationCache = false
		loop()
	}
}

Closure forAllAspectModes = {Closure loop ->
	if(includeMatchingAspect){
		BenchmarkConfigurationBuilder.aspectMode = BenchmarkAspectMode.MATCHING_ASPECT
		loop()
	}
	if(includeNonMatchingAspect){
		BenchmarkConfigurationBuilder.aspectMode = BenchmarkAspectMode.NOT_MATCHING_ASPECT
		loop()
	}
	if(includeNoAspect){
		BenchmarkConfigurationBuilder.aspectMode = BenchmarkAspectMode.NO_ASPECT
		loop()
	}
}

Closure forAllMOPSetups = {Closure loop ->
	if(includeDisabledBoth){
		BenchmarkConfigurationBuilder.mopMode = BenchmarkMOPMode.DISABLE_BOTH
		loop()
	}
	if(includeEnabledInstrumentationOnly){
		BenchmarkConfigurationBuilder.mopMode = BenchmarkMOPMode.ENABLE_INSTRUMENTATION_ONLY
		loop()
	}
	if(includeEnabledInterTypeDeclarationsOnly){
		BenchmarkConfigurationBuilder.mopMode = BenchmarkMOPMode.ENABLE_INTER_TYPE_DECLARATIONS_ONLY
		loop()
	}
	if(includeEnabledBoth){
		BenchmarkConfigurationBuilder.mopMode = BenchmarkMOPMode.ENABLE_BOTH
		loop()
	}
}

Closure forAllParameters = {List<String> parameters, Closure loop ->
	for(String parameter : parameters){
		BenchmarkConfigurationBuilder.args = parameter;
		loop();
	}
}

Closure forAllBenchmarks = {Closure loop ->
	mopBenchmarks.each{String benchmark, List<String> parameters ->
		BenchmarkConfigurationBuilder.benchmark = benchmark
		forAllMOPSetups{
			forAllParameters(parameters){
				loop()
			}
		}
	}
	otherBenchmarks.each{String benchmark, List<String> parameters ->
		BenchmarkConfigurationBuilder.mopMode = null
		BenchmarkConfigurationBuilder.benchmark = benchmark
		forAllParameters(parameters){
			loop()
		}
	}
}


List<BenchmarkConfiguration> allBenchmarks = []
forAllHeapSizes{
	forAllPartialEvaluationSetups{
		forAllInterTypeDeclarationCacheSetups{
			forAllAspectModes{
				forAllWarmups(){
					forAllBenchmarks{
						allBenchmarks += BenchmarkConfigurationBuilder.createConfiguration()
					}
				}
			}
		}
	}
}
return allBenchmarks


