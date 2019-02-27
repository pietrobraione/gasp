package gasp;

import static gasp.utils.Utils.getName;
import static gasp.utils.Utils.getVendor;
import static gasp.utils.Utils.getVersion;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;
import gasp.ga.Gene;
import gasp.ga.IndividualGenerator;
import gasp.ga.jbse.GeneJBSE;
import gasp.ga.jbse.IndividualGeneratorJBSE;
import gasp.ga.jbse.IndividualJBSE;
import gasp.ga.GeneticAlgorithm;
import gasp.ga.Individual;
import gasp.ga.localSearch.LocalSearchAlgorithm;
import gasp.ga.localSearch.LocalSearchAlgorithmHillClimbing;
import gasp.ga.operators.crossover.CrossoverFunction;
import gasp.ga.operators.crossover.CrossoverFunctionTwoPoints;
import gasp.ga.operators.crossover.CrossoverFunctionPrefix;
import gasp.ga.operators.crossover.CrossoverFunctionSinglePoint;
import gasp.ga.operators.crossover.CrossoverFunctionUnion;
import gasp.ga.operators.mutation.MutationFunction;
import gasp.ga.operators.mutation.MutationFunctionDelete;
import gasp.ga.operators.mutation.MutationFunctionDeleteOrNegate;
import gasp.ga.operators.selection.SelectionFunction;
import gasp.ga.operators.selection.SelectionFunctionRank;

public class Main {
	private final Options o;
	
	public static void main(String[] args) throws IOException {
		final Options o = new Options();
		final CommandLine commandLine = new CommandLine(o);
		try {
			commandLine.parse(args);
		} catch (MissingParameterException e) {
			System.out.println("Error: Missing method signature.");
			commandLine.usage(System.out);
			System.exit(1);
		}
		if (o.getHelp()) {
			commandLine.usage(System.out);
			System.exit(0);
		}
		if (o.getVersion()) {
			commandLine.printVersionHelp(System.out);
			System.exit(0);
		}
		final Main m = new Main(o);
		m.run();
	}
	
	public Main(Options o) {
		this.o = o;
	}
	
	public void run() {
		configureLogger();
		final Logger logger = LogManager.getLogger(Main.class);
		
		logger.info(getName() + " version " + getVersion() + ", © 2019 " + getVendor());
		logger.info("Analyzing method " + o.getMethodClassName() + ":" + o.getMethodDescriptor() + ":" + o.getMethodName());
		logger.info("Going to evolve " + o.getGenerations()  + " generations, timeout " + readableDuration(this.o.getTimeout()));
		logger.info("Random seed " + this.o.getSeed());
		logger.info("Estimated number of fitness evaluations: " + o.estimateFitnessEvaluations());

		final GeneticAlgorithm<?, ?> ga = geneticAlgorithm();
		ga.evolve();
		final Individual<?> bestIndividual = ga.getBestIndividuals(1).get(0);
		
		final boolean bestIndividualHasMaxFitness = bestIndividual.getFitness() >= this.o.getMaxFitness();		
		if (bestIndividualHasMaxFitness) {
			logger.info("The worst case individual has maximum fitness (possibly diverging execution).");
		}
		logger.info("Worst case chromosome: " + bestIndividual.getChromosome());
		logger.info("Worst case model: " + bestIndividual.getModel());
		logger.info("Worst case cost: " + bestIndividual.getFitness());
		logger.info(getName() + " ended");
	}
	
	private static String readableDuration(Duration d) {
		return d.toString().substring(2).replace("H", " h ").replace("M", " min ").replace("S", " sec ");
	}
	
	private void configureLogger() {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setStatusLevel(Level.WARN);
		AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE");
		appenderBuilder.addAttribute("target",ConsoleAppender.Target.SYSTEM_OUT);
		appenderBuilder.add(builder.newLayout("PatternLayout")
			                       .addAttribute("pattern", "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"));
		builder.add(appenderBuilder);
		LoggerComponentBuilder loggerBuilder = builder.newLogger("gasp.ga.GeneticAlgorithm", Level.TRACE);
		loggerBuilder.addAttribute("additivity", false);
		loggerBuilder.add(builder.newAppenderRef("Stdout"));
		builder.add(loggerBuilder);
		RootLoggerComponentBuilder rootLoggerBuilder = builder.newRootLogger(Level.DEBUG);
		rootLoggerBuilder.add(builder.newAppenderRef("Stdout"));
		builder.add(rootLoggerBuilder);
		Configurator.initialize(builder.build());
	}
	
	private GeneticAlgorithm<?, ?> geneticAlgorithm() {
		final Random random = new Random(this.o.getSeed());
		final IndividualGenerator<GeneJBSE, IndividualJBSE> ig = 
				new IndividualGeneratorJBSE(this.o.getMaxFitness(),
											random,
										    this.o.getClasspath(),
										    this.o.getJBSEPath(), 
										    this.o.getZ3Path(),
										    this.o.getMethodClassName(), 
										    this.o.getMethodDescriptor(), 
										    this.o.getMethodName());
		final GeneticAlgorithm<GeneJBSE, IndividualJBSE> retVal = 
				new GeneticAlgorithm<GeneJBSE, IndividualJBSE>(ig,
											   				   this.o.getNumberOfThreads(),
											   				   this.o.getGenerations(),
											   				   this.o.getTimeout(),
											   				   this.o.getLocalSearchPeriod(),
											   				   this.o.getPopulationSize(),
											   				   this.o.getEliteSize(),
											   				   crossoverFunction(random),
											   				   mutationFunction(random),
											   				   selectionFunction(random),
											   				   localSearchAlgorithm(ig, random));	
		return retVal;
	}
	
	private <T extends Gene<T>> CrossoverFunction<T> crossoverFunction(Random random) {
		switch (this.o.getCrossoverFunctionType()) {
		case SINGLE_POINT:
			return new CrossoverFunctionSinglePoint<T>(random);
		case TWO_POINTS:
			return new CrossoverFunctionTwoPoints<T>(random);
		case PREFIX:
			return new CrossoverFunctionPrefix<T>(random);
		case UNION:
			return new CrossoverFunctionUnion<T>(random);
		default:
			throw new AssertionError("Reached unreachable point: Possibly a crossover function case was not handled.");
		}
	}
	
	private <T extends Gene<T>> MutationFunction<T> mutationFunction(Random random) {
		switch (this.o.getMutationFunctionType()) {
		case DELETE:
			return new MutationFunctionDelete<T>(this.o.getMutationProbability(),
												 this.o.getMutationSizeRatio(),
												 random);
		case DELETE_OR_NEGATE:
			return new MutationFunctionDeleteOrNegate<T>(this.o.getMutationProbability(), 
														 this.o.getMutationSizeRatio(),
														 random);
		default:
			throw new AssertionError("Reached unreachable point: Possibly a mutation function case was not handled.");
		}
	}
	
	private <T extends Gene<T>, U extends Individual<T>> SelectionFunction<T, U> selectionFunction(Random random) {
		switch (this.o.getSelectionFunctionType()) {
		case RANK:
			return new SelectionFunctionRank<T, U>(random);
		default:
			throw new AssertionError("Reached unreachable point: Possibly a selection function case was not handled.");
		}
	}
	
	private <T extends Gene<T>, U extends Individual<T>> LocalSearchAlgorithm<T, U> localSearchAlgorithm(IndividualGenerator<T, U> ig, Random random) {
		switch (this.o.getLocalSearchAlgorithmType()) {
		case HILL_CLIMBING:
			return new LocalSearchAlgorithmHillClimbing<T, U>(ig,
														   this.o.getPopulationSize(),
														   this.o.getLocalSearchAttempts(),
														   random);
		case NONE:
			return null;
		default:
			throw new AssertionError("Reached unreachable point: Possibly a local search algorithm case was not handled.");
		}
	}
}













