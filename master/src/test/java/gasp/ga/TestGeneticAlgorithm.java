package gasp.ga;

import org.junit.jupiter.api.Test;

import gasp.ga.GeneticAlgorithm;
import gasp.ga.Individual;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("gasp.ga.GeneticAlgorithm test suite")
public class TestGeneticAlgorithm {
	private static final int POPULATION_SIZE = 5;
	private static final int ELITE_SIZE = 5;
	private static final List<Path> CLASSPATH = new ArrayList<Path>(); 
	static {
		CLASSPATH.add(Paths.get("/Users", "pietro", "git", "jbse-examples", "bin"));
	}
	
	@SuppressWarnings("unchecked")
	private GeneticAlgorithm<GeneStub> ga() {
		return new GeneticAlgorithm<GeneStub>(new IndividualGeneratorStub(new Random()), 1, 1, POPULATION_SIZE, ELITE_SIZE, 
									(i1, i2) -> { return new Individual[] { i1, i2 }; }, 
									(li, b) -> 0, i -> i);
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.elitism does not return an empty list")
	public void testElitism1() {
		final GeneticAlgorithm<?> algo = ga();
		algo.generateInitialPopulation();
		assertFalse(algo.elitism().isEmpty());
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.elitism does not return null")
	public void testElitism2() {
		final GeneticAlgorithm<?> algo = ga();
		algo.generateInitialPopulation();
		assertNotEquals(algo.elitism(), null);
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.elitism returns a list with size of the corresponding configuration parameter")
	public void testElitism5() {
		final GeneticAlgorithm<?> algo = ga();
		algo.generateInitialPopulation();
		assertEquals(algo.elitism().size(), ELITE_SIZE);
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.elitism returns a list sorted by fitness")
	public void testElitism4() {
		final GeneticAlgorithm<?> algo = ga();
		algo.generateInitialPopulation();
		final List<? extends Individual<?>> elite = algo.elitism();
		boolean eliteIsSortedByFitness = true;
		for (int i = 0; i < elite.size() - 1; ++i) {
			if (elite.get(i).getFitness() < elite.get(i + 1).getFitness()) {
				eliteIsSortedByFitness = false;
				break;
			};
		}
		assertTrue(eliteIsSortedByFitness);
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.getBestSolutions does not return null")
	public void testAlgorithm1() throws IOException {
		final GeneticAlgorithm<?> algo = ga();
 		algo.evolve();
		assertNotEquals(algo.getBestIndividuals(POPULATION_SIZE), null);
	}

	@Test
	@DisplayName("GeneticAlgorithm.getBestSolutions does not return an empty list")
	public void testAlgorithm2() throws IOException {
		final GeneticAlgorithm<?> algo = ga();
 		algo.evolve();
 		assertFalse(algo.getBestIndividuals(POPULATION_SIZE).isEmpty());
	}

}
