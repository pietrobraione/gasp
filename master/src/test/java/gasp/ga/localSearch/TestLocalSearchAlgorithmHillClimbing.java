package gasp.ga.localSearch;

import static settings.Settings.*;

import org.junit.jupiter.api.Test;

import gasp.ga.FoundWorstIndividualException;
import gasp.ga.jbse.GeneJBSE;
import gasp.ga.jbse.IndividualGeneratorJBSE;
import gasp.ga.jbse.IndividualJBSE;
import gasp.ga.localSearch.LocalSearchAlgorithmHillClimbing;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DisplayName("gasp.ga.localSearch.LocalSearchAlgorithmHillClimbing test suite")
public class TestLocalSearchAlgorithmHillClimbing {
	private static final long SEED = 0;
	private static final long MAX_FITNESS = 1_000_000;
	private static final int POPULATION_SIZE = 5;
	private static final int ATTEMPTS = 5;
	private static final String METHOD_CLASS_NAME = "smalldemos/ifx/IfExample";
	private static final String METHOD_DESCRIPTOR = "(I)V";
	private static final String METHOD_NAME = "m";
	
	private IndividualGeneratorJBSE ig() {
		return new IndividualGeneratorJBSE(MAX_FITNESS, CLASSPATH, JBSE_PATH, Z3_PATH, METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
	}

	private LocalSearchAlgorithmHillClimbing<GeneJBSE, IndividualJBSE> localSearch() {
		return new LocalSearchAlgorithmHillClimbing<>(ig(), POPULATION_SIZE, ATTEMPTS);
	}

	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch() does not return null")
	public void testLocalSearch1() throws FoundWorstIndividualException {
		final IndividualJBSE indRandom = ig().generateRandomIndividual(SEED);
		final IndividualJBSE indSearch = localSearch().doLocalSearch(SEED, indRandom.clone());
		assertNotEquals(indSearch, null);
	}
	
	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch(), if returns a different Individual, this has a higher fitness than the input one")
	public void testLocalSearch3() throws FoundWorstIndividualException {
		final IndividualJBSE indRandom = ig().generateRandomIndividual(SEED);
		final IndividualJBSE indSearch = localSearch().doLocalSearch(SEED, indRandom.clone());
		assumeFalse(indSearch == null);
		assumeFalse(indRandom.equals(indSearch));
		assertTrue(indSearch.getFitness() >= indRandom.getFitness());
	}
	
	@Test
	@DisplayName("LocalSearchAlgorithmHillClimbing.doLocalSearch(), if returns a different individual, this has at least a negative constraint")
	public void testLocalSearch4() throws FoundWorstIndividualException {
		final IndividualJBSE indRandom = ig().generateRandomIndividual(SEED);
		final IndividualJBSE indSearch = localSearch().doLocalSearch(SEED, indRandom.clone());
		assumeFalse(indSearch == null);
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
