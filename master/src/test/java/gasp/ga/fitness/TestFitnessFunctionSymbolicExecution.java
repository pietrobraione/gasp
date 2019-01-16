package gasp.ga.fitness;

import org.junit.jupiter.api.Test;

import gasp.ga.GeneticAlgorithm;
import gasp.ga.Individual;
import gasp.ga.fitness.FitnessEvaluationException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("gasp.ga.fitness.FitnessFunctionSymbolicExecution test suite")
public class TestFitnessFunctionSymbolicExecution {
	private static final int POPULATION_SIZE = 5;
	private static final int ELITE_SIZE = 5;
	private static final List<Path> CLASSPATH = new ArrayList<Path>(); 
	static {
		CLASSPATH.add(Paths.get("/Users", "pietro", "git", "jbse-examples", "bin"));
	}
	private static final Path JBSE_PATH = Paths.get("/Users", "pietro", "git", "gasp", "jbse", "build", "classes", "java", "main"); 
	private static final Path Z3_PATH = Paths.get("/opt", "local", "bin", "z3");
	private static final String METHOD_CLASS_NAME = "smalldemos/ifx/IfExample";
	private static final String METHOD_DESCRIPTOR = "(I)V";
	private static final String METHOD_NAME = "m";
	
	private GeneticAlgorithm ga() {
		return new GeneticAlgorithm(1, 1, POPULATION_SIZE, ELITE_SIZE, 
									(i1, i2) -> { return new Individual[] { i1, i2 }; }, 
									(li, b) -> 0, i -> i, CLASSPATH, JBSE_PATH, Z3_PATH, 
									METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
	}
	
	private FitnessFunctionSymbolicExecution fitness() {
		return new FitnessFunctionSymbolicExecution(CLASSPATH, JBSE_PATH, Z3_PATH, METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
	}
	
	@Test
	@DisplayName("FitnessFunctionSymbolicExecution.evaluate does not return null")
	public void testEvaluate1() throws FitnessEvaluationException {
		final Individual ind = ga().makeRandomIndividual();
		final FitnessFunctionSymbolicExecution fitnessFunction = fitness();
		assertNotNull(fitnessFunction.evaluate(ind.getConstraintSetClone()));
	}
	
	//TODO this regularly fails, is it correct???
	@Disabled
	@Test
	@DisplayName("FitnessFunctionSymbolicExecution.evaluate returns an output that has different fitness from its input")
	public void testEvaluate2() throws FitnessEvaluationException {
		final Individual ind = ga().makeRandomIndividual();
		final FitnessFunctionSymbolicExecution fitnessFunction = fitness();
		assertNotEquals(fitnessFunction.evaluate(ind.getConstraintSetClone()).getFitness(), ind.getFitness());
	}
	
	@Test
	@DisplayName("FitnessFunctionSymbolicExecution.evaluate returns an output that has different constraint set from its input")
	public void testEvaluate3() throws FitnessEvaluationException {
		final Individual ind = ga().makeRandomIndividual();
		final FitnessFunctionSymbolicExecution fitnessFunction = fitness();
		assertNotEquals(fitnessFunction.evaluate(ind.getConstraintSetClone()).getConstraintSetClone(), ind.getConstraintSetClone());
	}
}
