package complexity.ga.algo;

import org.junit.jupiter.api.Test;

import complexity.ga.Individual;
import complexity.utils.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Genetic Executor test suite")
public class TestGeneticExecutor {
	
	public ArrayList<Individual> population() {
		ArrayList<Individual> population = new ArrayList<>();
		for (int i = 0; i < Config.populationSize; i++) {
			population.add(Individual.randomIndividual());
		}
		return population;
	}
	
	@Test
	@DisplayName("Elitism returnis not empty")
	public void testElitism1() {
		assertFalse(GeneticExecutor.elitism(population(), population().size()).isEmpty());
	}
	
	@Test
	@DisplayName("Elitism return is not null")
	public void testElitism2() {
		assertNotEquals(GeneticExecutor.elitism(population(), population().size()), null);
	}
	
	@Test
	@DisplayName("Elitism returns the input sorted by fitness")
	public void testElitism4() {
		ArrayList<Individual> popSorted = GeneticExecutor.elitism(population(), population().size());
		boolean assertLoop = true;
		for(int i = 0; i < popSorted.size() - 1; i++) {
			if(popSorted.get(i).getFitness() < popSorted.get(i+1).getFitness()) {
				assertLoop = false;
			};
		}
		assertTrue(assertLoop);
	}
	
	@Test
	@DisplayName("Elitism returns the elite size of input after sorting it by fitness")
	public void testElitism5() {
		int eliteSize = (int) Config.eliteRatio * Config.populationSize;
		ArrayList<Individual> popSorted = GeneticExecutor.elitism(population(), eliteSize);
		assertEquals(popSorted.size(), eliteSize);
	}
	
	@Test
	@DisplayName("WcetGenerator return is not null")
	public void testAlgorithm1() throws IOException {
		List<Individual> profiles = GeneticExecutor.wcetGenerator();
		assertNotEquals(profiles, null);
	}

	@Test
	@DisplayName("WcetGenerator return is not empty")
	public void testAlgorithm2() throws IOException {
		List<Individual> profiles = GeneticExecutor.wcetGenerator();
		assertFalse(profiles.isEmpty());
	}

}
