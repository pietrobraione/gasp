package gasp.ga;

import org.junit.jupiter.api.Test;

import gasp.ga.GeneticAlgorithm;
import gasp.ga.Individual;
import gasp.utils.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GeneticAlgorithm test suite")
public class TestGeneticAlgorithm {
	
	private ArrayList<Individual> population() {
		final ArrayList<Individual> population = new ArrayList<>();
		for (int i = 0; i < Config.populationSize; ++i) {
			population.add(Individual.makeRandomIndividual());
		}
		return population;
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.elitism does not return an empty list")
	public void testElitism1() {
		final GeneticAlgorithm algo = new GeneticAlgorithm(population());
		assertFalse(algo.elitism().isEmpty());
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.elitism does not return null")
	public void testElitism2() {
		final GeneticAlgorithm algo = new GeneticAlgorithm(population());
		assertNotEquals(algo.elitism(), null);
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.elitism returns a list with size of the corresponding configuration parameter")
	public void testElitism5() {
		final GeneticAlgorithm algo = new GeneticAlgorithm(population());
		assertEquals(algo.elitism().size(), Config.eliteSize);
	}
	
	@Test
	@DisplayName("GeneticAlgorithm.elitism returns a list sorted by fitness")
	public void testElitism4() {
		final GeneticAlgorithm algo = new GeneticAlgorithm(population());
		final List<Individual> elite = algo.elitism();
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
		final GeneticAlgorithm algo = new GeneticAlgorithm(Config.selectionFunction, Config.crossoverFunction, Config.localSearchAlgorithm);
 		algo.generateSolution();
		assertNotEquals(algo.getBestSolutions(Config.populationSize), null);
	}

	@Test
	@DisplayName("GeneticAlgorithm.getBestSolutions does not return an empty list")
	public void testAlgorithm2() throws IOException {
		final GeneticAlgorithm algo = new GeneticAlgorithm(Config.selectionFunction, Config.crossoverFunction, Config.localSearchAlgorithm);
 		algo.generateSolution();
 		assertFalse(algo.getBestSolutions(Config.populationSize).isEmpty());
	}

}
