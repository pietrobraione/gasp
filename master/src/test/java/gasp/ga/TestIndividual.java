package gasp.ga;

import org.junit.jupiter.api.Test;

import gasp.ga.Individual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;

@DisplayName("gasp.ga.Individual test suite")
public class TestIndividual {
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
	@DisplayName("Individual.clone() generates an individual that is equal to the cloned one")
	public void testCloneIndividual1() {
		final Individual ind1 = ga().makeRandomIndividual();
		final Individual ind2 = ind1.clone();
		assertEquals(ind1, ind2);
	}
	
	@Test
	@DisplayName("Individual.clone() has the same constraint set of the cloned individual")
	public void testCloneIndividual2() {
		final Individual ind1 = ga().makeRandomIndividual();
		final Individual ind2 = ind1.clone();
		assertEquals(ind1.getConstraintSetClone(), ind2.getConstraintSetClone());
	}
	
	@Test
	@DisplayName("Individual.clone() has the same fitness of the cloned individual")
	public void testCloneIndividual3() {
		final Individual ind1 = ga().makeRandomIndividual();
		final Individual ind2 = ind1.clone();
		assertEquals(ind1.getFitness(), ind2.getFitness());
	}
	
	@Test
	@DisplayName("Individual.getModel() does not return null")
	public void testGetModel1() {
		final Individual ind = ga().makeRandomIndividual();
		assertNotNull(ind.getModel());
	}
}
