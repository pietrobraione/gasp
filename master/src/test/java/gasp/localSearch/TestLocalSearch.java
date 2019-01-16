package gasp.localSearch;

import org.junit.jupiter.api.Test;

import gasp.ga.Individual;
import gasp.ga.localSearch.LocalSearchAlgorithm;
import gasp.utils.Config;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Local Search test suite")
public class TestLocalSearch {

	@Test
	@DisplayName("LocalSearch does not return null")
	public void testLocalSearch1() {
		final LocalSearchAlgorithm algo = Config.localSearchAlgorithm;
		final Individual indRandom = Individual.makeRandomIndividual();
		final Individual indSearch = algo.doLocalSearch(indRandom.clone());
		assertNotEquals(indSearch, null);
	}
	
	@Test
	@DisplayName("LocalSearch returns an Individual different from the input one")
	public void testLocalSearch2() {
		final LocalSearchAlgorithm algo = Config.localSearchAlgorithm;
		final Individual indRandom = Individual.makeRandomIndividual();
		final Individual indSearch = algo.doLocalSearch(indRandom.clone());
		assertNotEquals(indRandom, indSearch);
	}
	
	@Test
	@DisplayName("LocalSearch returns an Individual with a (possibly) higher fitness than the input one")
	public void testLocalSearch3() {
		final LocalSearchAlgorithm algo = Config.localSearchAlgorithm;
		final Individual indRandom = Individual.makeRandomIndividual();
		final Individual indSearch = algo.doLocalSearch(indRandom.clone());
		assertTrue(indSearch.getFitness() >= indRandom.getFitness());
	}
	
	@Test
	@DisplayName("LocalSearch returns an Individual with at least a negative constraint")
	public void testLocalSearch4() {
		final LocalSearchAlgorithm algo = Config.localSearchAlgorithm;
		final Individual indRandom = Individual.makeRandomIndividual();
		final Individual indSearch = algo.doLocalSearch(indRandom.clone());
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
