package gasp;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import gasp.ga.fitness.FitnessFunction;
import gasp.ga.fitness.FitnessFunctionSymbolicExecution;
import gasp.ga.localSearch.LocalSearchAlgorithm;
import gasp.ga.localSearch.LocalSearchAlgorithmHillClimbing;
import gasp.ga.operators.crossover.CrossoverFunction;
import gasp.ga.operators.crossover.CrossoverFunctionExclude_NotUsedYet;
import gasp.ga.operators.crossover.CrossoverFunctionPrefix_NotUsedYet;
import gasp.ga.operators.crossover.CrossoverFunctionSinglePoint;
import gasp.ga.operators.crossover.CrossoverFunctionUnion_NotUsedYet;
import gasp.ga.operators.mutation.MutationFunctionDeleteConstraint;
import gasp.ga.operators.mutation.MutationFunctionDeleteOrNegateConstraint;
import gasp.ga.operators.mutation.MutationFunction;
import gasp.ga.operators.selection.SelectionFunctionRank;
import gasp.ga.operators.selection.SelectionFunction;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

public class Options {
	@Spec
	private CommandSpec spec;
	
	private boolean help = false;
	private int generations = 5;
	private int localSearchRate = 5;
	private int populationSize = 10;
	private int eliteSize = 5;
	private CrossoverFunctionType crossoverFunctionType = CrossoverFunctionType.SINGLE_POINT; 
	private MutationFunctionType mutationFunctionType = MutationFunctionType.DELETE_CONSTRAINT; 
	private SelectionFunctionType selectionFunctionType = SelectionFunctionType.RANK;
	private FitnessFunctionType fitnessFunctionType = FitnessFunctionType.SYMBOLIC_EXECUTION;
	private LocalSearchAlgorithmType localSearchAlgorithmType = LocalSearchAlgorithmType.HILL_CLIMBING;
	private double mutationProbability = 0.1;
	private double mutationSizeRatio = 0.1;
	private Random random = new Random(System.currentTimeMillis());		   
	private List<Path> classpath = Collections.singletonList(Paths.get("."));
	private Path jbsePath = Paths.get(".");
	private Path z3Path = Paths.get(".");	
	private String methodClassName = "smalldemos/ifx/IfExample";
	private String methodDescriptor = "(I)V";	
	private String methodName = "m";

	/*
	//Giovanni's settings
	public static final String z3Path = Paths.get("/Users", "denaro", "Desktop", "RTools", "Z3", "z3-4.3.2.d548c51a984e-x64-osx-10.8.5", "bin", "z3").toString();	
	public static final String classTarget="/Users/denaro/git/gasp/jbse/build/classes/java/main";
	public static final String programPath="/Users/denaro/git/jbse-examples/bin";

	//Pietro's settings
	public static final Path z3Path = Paths.get("/opt", "local", "bin", "z3").toString();	
	public static final String classTarget="/Users/pietro/git/gasp/jbse/build/classes/java/main";
	public static final String programPath="/Users/pietro/git/jbse-examples/bin";*/

	
	public enum CrossoverFunctionType { EXCLUDE, PREFIX, SINGLE_POINT, UNION } 
	public enum MutationFunctionType { DELETE_CONSTRAINT, DELETE_OR_NEGATE_CONSTRAINT }
	public enum SelectionFunctionType { RANK }
	public enum FitnessFunctionType { SYMBOLIC_EXECUTION }
	public enum LocalSearchAlgorithmType { HILL_CLIMBING, NONE }
	
	@Option(names = {"-h", "--help"}, usageHelp = true, defaultValue = "false", description = "Prints usage and exits.")
	public void setHelp(boolean help) {
		this.help = help;
	}
	
	public boolean getHelp() {
		return this.help;
	}
	
	@Option(names = {"-g", "--generations"}, defaultValue = "5", description = "Number of generations to be executed (default: ${DEFAULT-VALUE}).")
	public void setGenerations(int generations) {
		if (generations <= 0) {
			throw new ParameterException(this.spec.commandLine(), String.format("Number of generations %d is zero or negative.", generations));
		}
		this.generations = generations;
	}
	
	public int getGenerations() {
		return this.generations;
	}
	
	@Option(names = {"-r", "--local-search-rate"}, defaultValue = "5", description = "Number of generations between two different applications of local search (default: ${DEFAULT-VALUE}).")
	public void setLocalSearchRate(int localSearchRate) {
		if (localSearchRate <= 0) {
			throw new ParameterException(this.spec.commandLine(), String.format("Local search rate %d is zero or negative.", localSearchRate));
		}
		this.localSearchRate = localSearchRate;
	}
	
	public int getLocalSearchRate() {
		return this.localSearchRate;
	}
	
	@Option(names = {"-p", "--population-size"}, defaultValue = "10", description = "Number of individuals in the population (default: ${DEFAULT-VALUE}).")
	public void setPopulationSize(int populationSize) {
		if (populationSize <= 0) {
			throw new ParameterException(this.spec.commandLine(), String.format("Population size %d is zero or negative.", populationSize));
		}
		this.populationSize = populationSize;
		this.eliteSize = Math.min(populationSize, this.eliteSize);
	}
	
	public int getPopulationSize() {
		return this.populationSize;
	}
	
	@Option(names = {"-e", "--elite-size"}, defaultValue = "5", description = "Number of the best individuals in the population that are preserved across generations (default: ${DEFAULT-VALUE}).")
	public void setEliteSize(int eliteSize) {
		if (eliteSize <= 0) { //TODO can eliteSize be == 0?
			throw new ParameterException(this.spec.commandLine(), String.format("Elite size %d is zero or negative.", populationSize));
		}
		this.eliteSize = Math.min(this.populationSize, eliteSize);
	}
	
	public int getEliteSize() {
		return this.eliteSize;
	}
	
	
	@Option(names = {"-c", "--crossover-function"}, defaultValue = "SINGLE_POINT", description = "The crossover function, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setCrossoverFunction(CrossoverFunctionType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Crossover function is null.");
		}
		this.crossoverFunctionType = type;
	}
	
	public CrossoverFunction getCrossoverFunction() {
		switch (this.crossoverFunctionType) {
		case EXCLUDE:
			return new CrossoverFunctionExclude_NotUsedYet(this.random);
		case PREFIX:
			return new CrossoverFunctionPrefix_NotUsedYet(getFitnessFunction(),
													      this.random);
		case SINGLE_POINT:
			return new CrossoverFunctionSinglePoint(getMutationFunction(),
													getFitnessFunction(),
													this.mutationSizeRatio,
													this.random,
													this.classpath,
													this.jbsePath,
													this.z3Path,
													this.methodClassName,
													this.methodDescriptor,
													this.methodName);
		case UNION:
			return new CrossoverFunctionUnion_NotUsedYet(getFitnessFunction());
		default:
			throw new AssertionError("Reached unreachable point: Possibly a crossover function case was not handled.");
		}
	}
	
	@Option(names = {"-m", "--mutation-function"}, defaultValue = "DELETE_CONSTRAINT", description = "The mutation function, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setMutationFunction(MutationFunctionType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Mutation function is null.");
		}
		this.mutationFunctionType = type;
	}
	
	public MutationFunction getMutationFunction() {
		switch (this.mutationFunctionType) {
		case DELETE_CONSTRAINT:
			return new MutationFunctionDeleteConstraint(getFitnessFunction(), 
														this.mutationProbability, 
														this.random);
		case DELETE_OR_NEGATE_CONSTRAINT:
			return new MutationFunctionDeleteOrNegateConstraint(getFitnessFunction(), 
																this.mutationProbability, 
																this.random);
		default:
			throw new AssertionError("Reached unreachable point: Possibly a mutation function case was not handled.");
		}
	}
	
	@Option(names = {"-s", "--selection-function"}, defaultValue = "RANK", description = "The selection function, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setSelectionFunction(SelectionFunctionType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Selection function is null.");
		}
		this.selectionFunctionType = type;
	}
	
	public SelectionFunction getSelectionFunction() {
		switch (this.selectionFunctionType) {
		case RANK:
			return new SelectionFunctionRank(this.random);
		default:
			throw new AssertionError("Reached unreachable point: Possibly a selection function case was not handled.");
		}
	}
	
	@Option(names = {"-f", "--fitness-function"}, defaultValue = "SYMBOLIC_EXECUTION", description = "The fitness function, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setFitnessFunction(FitnessFunctionType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Fitness function is null.");
		}
		this.fitnessFunctionType = type;
	}
	
	public FitnessFunction getFitnessFunction() {
		switch (this.fitnessFunctionType) {
		case SYMBOLIC_EXECUTION:
			return new FitnessFunctionSymbolicExecution(this.classpath, 
                                                        this.jbsePath, 
                                                        this.z3Path, 
                                                        this.methodClassName, 
                                                        this.methodDescriptor, 
                                                        this.methodName);
		default:
			throw new AssertionError("Reached unreachable point: Possibly a fitness function case was not handled.");
		}
	}
	
	@Option(names = {"-l", "--local-search-algorithm"}, defaultValue = "HILL_CLIMBING", description = "The local search algorithm, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setLocalSearchAlgorithm(LocalSearchAlgorithmType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Local search algorithm is null.");
		}
		this.localSearchAlgorithmType = type;
	}
	
	public LocalSearchAlgorithm getLocalSearchAlgorithm() {
		switch (this.localSearchAlgorithmType) {
		case HILL_CLIMBING:
			return new LocalSearchAlgorithmHillClimbing(this.populationSize,
														this.random,
														this.classpath, 
                    									this.jbsePath, 
                    									this.z3Path, 
                    									this.methodClassName, 
                    									this.methodDescriptor, 
                    									this.methodName);
		case NONE:
			return null;
		default:
			throw new AssertionError("Reached unreachable point: Possibly a local search algorithm case was not handled.");
		}
	}
	
	@Option(names = {"-mp", "--mutation-probability"}, defaultValue = "0.1", description = "Probability of applying the mutation operator (default: ${DEFAULT-VALUE}).")
	public void setMutationProbability(double mutationProbability) {
		if (mutationProbability < 0 || mutationProbability > 1) {
			throw new ParameterException(this.spec.commandLine(), String.format("Mutation probability %f is less than zero or greater than one.", mutationProbability));
		}
		this.mutationProbability = mutationProbability;
	}
	
	public double getMutationProbability() {
		return this.mutationProbability;
	}
	
	@Option(names = {"-mr", "--mutation-size-ratio"}, defaultValue = "0.1", description = "Ratio between the number of individual to which the mutation operator can be applied and the total population size (default: ${DEFAULT-VALUE}).")
	public void setMutationSizeRatio(double mutationSizeRatio) {
		if (mutationSizeRatio < 0 || mutationSizeRatio > 1) {
			throw new ParameterException(this.spec.commandLine(), String.format("Mutation size ratio %f is less than zero or greater than one.", mutationSizeRatio));
		}
		this.mutationSizeRatio = mutationSizeRatio;
	}
	
	public double getMutationSizeRatio() {
		return this.mutationSizeRatio;
	}
	
	@Option(names = {"-rs", "--seed"}, description = "Seed for random number generator (by default it is picked from the current time).")
	public void setSeed(long seed) {
		this.random = new Random(seed);
	}
	
	public Random getRandom() {
		return this.random;
	}
	
	@Option(names = {"-cp", "--classpath"}, defaultValue = ".", description = "The classpath for the target application (default: current directory).")
	public void setClasspath(String classpath) {
		if (classpath == null) {
			throw new ParameterException(this.spec.commandLine(), "Classpath is null.");
		}
		final String[] paths = classpath.split(File.pathSeparator);
		this.classpath = new ArrayList<>();
		try {
			this.classpath.addAll(Arrays.stream(paths).map(s -> Paths.get(s)).collect(Collectors.toList()));
		} catch (InvalidPathException e) {
			throw new ParameterException(this.spec.commandLine(), "Classpath contains an invalid path.");
		}
	}
	
	public List<Path> getClasspath() {
		return this.classpath;
	}
	
	@Option(names = {"-j", "--jbse-path"}, defaultValue = ".", description = "The path to the JBSE classes (default: current directory).")
	public void setJBSEPath(String jbsePath) {
		if (jbsePath == null) {
			throw new ParameterException(this.spec.commandLine(), "Path to JBSE is null.");
		}
		try {
			this.jbsePath = Paths.get(jbsePath);
		} catch (InvalidPathException e) {
			throw new ParameterException(this.spec.commandLine(), "Path to JBSE is invalid.");
		}
	}
	
	public Path getJBSEPath() {
		return this.jbsePath;
	}
	
	@Option(names = {"-z3", "--z3-path"}, defaultValue = ".", description = "The path to the Z3 executable (default: current directory).")
	public void setZ3Path(String z3Path) {
		if (z3Path == null) {
			throw new ParameterException(this.spec.commandLine(), "Path to Z3 is null.");
		}
		try {
			this.z3Path = Paths.get(z3Path);
		} catch (InvalidPathException e) {
			throw new ParameterException(this.spec.commandLine(), "Path to Z3 is invalid.");
		}
	}
	
	public Path getZ3Path() {
		return this.z3Path;
	}
	
	@Parameters(arity = "1", description = "The signature of the method to analyze, a semicolon-separated list of: class name in internal format, descriptor, method name.")
	public void setMethodSignature(String signature) {
		if (signature == null) {
			throw new ParameterException(this.spec.commandLine(), "The signature of the method to analyze is null.");
		}
		final String[] splitSignature = signature.split(":");
		if (splitSignature.length != 3) {
			throw new ParameterException(this.spec.commandLine(), "The signature of the method to analyze " + signature + " is invalid.");
		}
		this.methodClassName = splitSignature[0];
		this.methodDescriptor = splitSignature[1];
		this.methodName = splitSignature[2];
	}
	
	public String getMethodClassName() {
		return this.methodClassName;
	}
	
	public String getMethodDescriptor() {
		return this.methodDescriptor;
	}
	
	public String getMethodName() {
		return this.methodName;
	}
	
	//Returns an estimation of the number of fitness evaluations when running with this configuration
	public int estimateFitnessEvaluations() {
	    int g = this.generations;
	    int p = this.populationSize;
	    double mp = this.mutationProbability;
	    return (int) Math.round(p * ((mp + 1) * g + 1));
	}
}
