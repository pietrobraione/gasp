package gasp.ga.localSearch;

import org.junit.jupiter.api.Test;

import gasp.ga.GeneticAlgorithm;
import gasp.ga.Individual;
import gasp.ga.localSearch.LocalSearchAlgorithmHillClimbing;
import gasp.se.GeneJBSE;
import gasp.se.IndividualGeneratorJBSE;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DisplayName("gasp.localSearch.LocalSearchAlgorithmHillClimbing test suite")
public class TestLocalSearchAlgorithmHillClimbing {
	private static final int POPULATION_SIZE = 5;
	private static final List<Path> CLASSPATH = new ArrayList<Path>(); 
	static {
		CLASSPATH.add(Paths.get("/Users", "pietro", "git", "jbse-examples", "bin"));
	}
	private static final Path JBSE_PATH = Paths.get("/Users", "pietro", "git", "gasp", "jbse", "build", "classes", "java", "main"); 
	private static final Path Z3_PATH = Paths.get("/opt", "local", "bin", "z3");
	private static final String METHOD_CLASS_NAME = "smalldemos/ifx/IfExample";
	private static final String METHOD_DESCRIPTOR = "(I)V";
	private static final String METHOD_NAME = "m";
	
	private IndividualGeneratorJBSE ig() {
		return new IndividualGeneratorJBSE(new Random(0), CLASSPATH, JBSE_PATH, Z3_PATH, METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
	}

	private LocalSearchAlgorithmHillClimbing<GeneJBSE> localSearch() {
		return new LocalSearchAlgorithmHillClimbing<>(ig(), POPULATION_SIZE, new Random(0));
	}

	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch() does not return null")
	public void testLocalSearch1() {
		final Individual<GeneJBSE> indRandom = ig().generateRandomIndividual();
		final Individual<GeneJBSE> indSearch = localSearch().doLocalSearch(indRandom.clone());
		assertNotEquals(indSearch, null);
	}
	
	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch() returns an Individual with a (possibly) higher fitness than the input one")
	public void testLocalSearch3() {
		final Individual<GeneJBSE> indRandom = ig().generateRandomIndividual();
		final Individual<GeneJBSE> indSearch = localSearch().doLocalSearch(indRandom.clone());
		assertTrue(indSearch.getFitness() >= indRandom.getFitness());
	}
	
	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch(), if returns a different individual, this has at least a negative constraint")
	public void testLocalSearch4() {
		final Individual<GeneJBSE> indRandom = ig().generateRandomIndividual();
		final Individual<GeneJBSE> indSearch = localSearch().doLocalSearch(indRandom.clone());
		assumeFalse(indRandom.equals(indSearch));
		boolean hasANegativeConstraint = false;
		for (int i = 0; i < indSearch.getChromosome().size(); ++i) {
			for (int j = 0; j < indRandom.getChromosome().size(); ++j) {
				if (indSearch.getChromosome().get(i).equals(indRandom.getChromosome().get(j).not())) {
					hasANegativeConstraint = true;
				}
			}
		}
		assertTrue(hasANegativeConstraint);
	}
}
