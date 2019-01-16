package gasp.ga;

import org.junit.jupiter.api.Test;

import gasp.ga.Individual;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;

@DisplayName("gasp.ga.Model test suite")
public class TestModel {
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
	
	@Test
	@DisplayName("Model.toString() returns the right representation")
	public void testGetModel2() {
		final Individual ind = ga().makeRandomIndividual();
		assertEquals(ind.getModel().toString(), "Model [constraints=" + ind.getConstraintSetClone().toString() + "]");
	}
}
