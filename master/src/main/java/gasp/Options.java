package gasp;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import gasp.utils.Utils;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(name = "gasp", versionProvider = Options.ManifestVersionProvider.class)
public class Options {
	@Spec
	private CommandSpec spec;
	
	private boolean help = false;
	private boolean version = false;
	private int numberOfThreads = 2;
	private int generations = 5;
	private int localSearchRate = 5;
	private int populationSize = 10;
	private int eliteSize = 5;
	private long maxFitness = 1_000_000;
	private CrossoverFunctionType crossoverFunctionType = CrossoverFunctionType.SINGLE_POINT; 
	private MutationFunctionType mutationFunctionType = MutationFunctionType.DELETE; 
	private SelectionFunctionType selectionFunctionType = SelectionFunctionType.RANK;
	private FitnessFunctionType fitnessFunctionType = FitnessFunctionType.SYMBOLIC_EXECUTION;
	private LocalSearchAlgorithmType localSearchAlgorithmType = LocalSearchAlgorithmType.HILL_CLIMBING;
	private double mutationProbability = 0.1;
	private double mutationSizeRatio = 0.1;
	private long seed = System.currentTimeMillis();		   
	private List<Path> classpath = Collections.singletonList(Paths.get("."));
	private Path jbsePath = Paths.get(".");
	private Path z3Path = Paths.get(".");	
	private String methodClassName = "smalldemos/ifx/IfExample";
	private String methodDescriptor = "(I)V";	
	private String methodName = "m";
	
	public enum CrossoverFunctionType { SINGLE_POINT, TWO_POINTS, PREFIX, UNION } 
	public enum MutationFunctionType { DELETE, DELETE_OR_NEGATE }
	public enum SelectionFunctionType { RANK }
	public enum FitnessFunctionType { SYMBOLIC_EXECUTION }
	public enum LocalSearchAlgorithmType { HILL_CLIMBING, NONE }
	
	@Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message and exit.")
	public void setHelp(boolean help) {
		this.help = help;
	}
	
	public boolean getHelp() {
		return this.help;
	}
	
	@Option(names = {"-V", "--version"}, versionHelp = true, description = "Print version information and exit.")
	public void setVersion(boolean version) {
		this.version = version;
	}
	
	public boolean getVersion() {
		return this.version;
	}
	
	@Option(names = {"-t", "--threads"}, defaultValue = "2", description = "Number of threads for concurrent execution (default: ${DEFAULT-VALUE}).")
	public void setNumberOfThreads(int numberOfThreads) {
		if (numberOfThreads <= 0) {
			throw new ParameterException(this.spec.commandLine(), String.format("Number of threads %d is zero or negative.", numberOfThreads));
		}
		this.numberOfThreads = numberOfThreads;
	}
	
	public int getNumberOfThreads() {
		return this.numberOfThreads;
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
	
	@Option(names = {"-M", "--max-fitness"}, defaultValue = "1000000", description = "Maximum fitness value beyond which the software under analysis is considered to be diverging (default: ${DEFAULT-VALUE}).")
	public void setMaxFitness(long maxFitness) {
		if (maxFitness <= 0) {
			throw new ParameterException(this.spec.commandLine(), String.format("Maximum fitness %d is zero or negative.", maxFitness));
		}
		this.maxFitness = maxFitness;
	}
	
	public long getMaxFitness() {
		return this.maxFitness;
	}
	
	@Option(names = {"-c", "--crossover-function"}, defaultValue = "SINGLE_POINT", description = "The crossover function, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setCrossoverFunctionType(CrossoverFunctionType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Crossover function is null.");
		}
		this.crossoverFunctionType = type;
	}
	
	public CrossoverFunctionType getCrossoverFunctionType() {
		return this.crossoverFunctionType;
	}
	
	@Option(names = {"-m", "--mutation-function"}, defaultValue = "DELETE", description = "The mutation function, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setMutationFunctionType(MutationFunctionType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Mutation function is null.");
		}
		this.mutationFunctionType = type;
	}
	
	public MutationFunctionType getMutationFunctionType() {
		return this.mutationFunctionType;
	}
	
	@Option(names = {"-s", "--selection-function"}, defaultValue = "RANK", description = "The selection function, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setSelectionFunctionType(SelectionFunctionType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Selection function is null.");
		}
		this.selectionFunctionType = type;
	}
	
	public SelectionFunctionType getSelectionFunctionType() {
		return this.selectionFunctionType;
	}
	
	@Option(names = {"-f", "--fitness-function"}, defaultValue = "SYMBOLIC_EXECUTION", description = "The fitness function, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setFitnessFunctionType(FitnessFunctionType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Fitness function is null.");
		}
		this.fitnessFunctionType = type;
	}
	
	public FitnessFunctionType getFitnessFunctionType() {
		return this.fitnessFunctionType;
	}
	
	@Option(names = {"-l", "--local-search-algorithm"}, defaultValue = "HILL_CLIMBING", description = "The local search algorithm, valid values: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
	public void setLocalSearchAlgorithmType(LocalSearchAlgorithmType type) {
		if (type == null) {
			throw new ParameterException(this.spec.commandLine(), "Local search algorithm is null.");
		}
		this.localSearchAlgorithmType = type;
	}
	
	public LocalSearchAlgorithmType getLocalSearchAlgorithmType() {
		return this.localSearchAlgorithmType;
	}
	
	@Option(names = {"-P", "--mutation-probability"}, defaultValue = "0.1", description = "Probability of applying the mutation operator (default: ${DEFAULT-VALUE}).")
	public void setMutationProbability(double mutationProbability) {
		if (mutationProbability < 0 || mutationProbability > 1) {
			throw new ParameterException(this.spec.commandLine(), String.format("Mutation probability %f is less than zero or greater than one.", mutationProbability));
		}
		this.mutationProbability = mutationProbability;
	}
	
	public double getMutationProbability() {
		return this.mutationProbability;
	}
	
	@Option(names = {"-R", "--mutation-size-ratio"}, defaultValue = "0.1", description = "Ratio between the number of individual to which the mutation operator can be applied and the total population size (default: ${DEFAULT-VALUE}).")
	public void setMutationSizeRatio(double mutationSizeRatio) {
		if (mutationSizeRatio < 0 || mutationSizeRatio > 1) {
			throw new ParameterException(this.spec.commandLine(), String.format("Mutation size ratio %f is less than zero or greater than one.", mutationSizeRatio));
		}
		this.mutationSizeRatio = mutationSizeRatio;
	}
	
	public double getMutationSizeRatio() {
		return this.mutationSizeRatio;
	}
	
	@Option(names = {"-S", "--seed"}, description = "Seed for random number generator (by default it is picked from the current time).")
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	@Option(names = {"-C", "--classpath"}, defaultValue = ".", description = "The classpath for the target application (default: current directory).")
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
	
	@Option(names = {"-j", "--jbse-path"}, paramLabel = "<jbsePath>", defaultValue = ".", description = "The path to the JBSE classes (default: current directory).")
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
	
	@Option(names = {"-Z", "--z3-path"}, defaultValue = ".", description = "The path to the Z3 executable (default: current directory).")
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
	
	@Parameters(description = "The signature of the method to analyze, a semicolon-separated list of: class name in internal format, descriptor, method name.")
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
	
	public int estimateFitnessEvaluations() {
	    int g = this.generations;
	    int p = this.populationSize;
	    double mp = this.mutationProbability;
	    return (int) Math.round(p * ((mp + 1) * g + 1));
	}
	
	static class ManifestVersionProvider implements IVersionProvider {
		@Override
		public String[] getVersion() throws Exception {
			final String[] retVal = new String[1];
			retVal[0] = Utils.getName() + " version " + Utils.getVersion();
			return retVal;
		}
	}
}
