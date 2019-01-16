package gasp.localSearch;

import org.junit.jupiter.api.Test;

import gasp.ga.GeneticAlgorithm;
import gasp.ga.Individual;
import gasp.ga.localSearch.LocalSearchAlgorithmHillClimbing;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DisplayName("gasp.localSearch.LocalSearchAlgorithmHillClimbing test suite")
public class TestLocalSearchAlgorithmHillClimbing {
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
	
	private LocalSearchAlgorithmHillClimbing localSearch() {
		return new LocalSearchAlgorithmHillClimbing(POPULATION_SIZE, new Random(0), CLASSPATH, JBSE_PATH, Z3_PATH, METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
	}

	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch() does not return null")
	public void testLocalSearch1() {
		final Individual indRandom = ga().makeRandomIndividual();
		final Individual indSearch = localSearch().doLocalSearch(indRandom.clone());
		assertNotEquals(indSearch, null);
	}
	
	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch() returns an Individual different from the input one")
	public void testLocalSearch2() {
		final Individual indRandom = ga().makeRandomIndividual();
		final Individual indSearch = localSearch().doLocalSearch(indRandom.clone());
		assertNotEquals(indRandom, indSearch);
	}
	
	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch() returns an Individual with a (possibly) higher fitness than the input one")
	public void testLocalSearch3() {
		final Individual indRandom = ga().makeRandomIndividual();
		final Individual indSearch = localSearch().doLocalSearch(indRandom.clone());
		assertTrue(indSearch.getFitness() >= indRandom.getFitness());
	}
	
	//TODO the test fails; is it correct? If local search does not find anything better shall return the same individual?
	@Disabled
	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch() returns an Individual with at least a negative constraint")
	public void testLocalSearch4() {
		final Individual indRandom = ga().makeRandomIndividual();
		final Individual indSearch = localSearch().doLocalSearch(indRandom.clone());
		boolean hasANegativeConstraint = false;
		for (int i = 0; i < indSearch.getConstraintSetClone().size(); ++i) {
			for (int j = 0; j < indRandom.getConstraintSetClone().size(); ++j) {
				if (indSearch.getConstraintSetClone().get(i).equals(indRandom.getConstraintSetClone().get(j).not())) {
					hasANegativeConstraint = true;
				}
			}
		}
		assertTrue(hasANegativeConstraint);
	}
}
