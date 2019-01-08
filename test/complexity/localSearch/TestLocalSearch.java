package complexity.localSearch;


import org.junit.jupiter.api.Test;

import complexity.ga.Individual;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Local Search test suite")
public class TestLocalSearch {

	@Test
	@DisplayName("LocalSearch return is not null")
	public void testLocalSearch1() {
		LocalSearchAlgorithm ls = LocalSearchAlgorithm.makeLocalSearchHillClimbing();
		Individual ind1 = Individual.randomIndividual();
		Individual ind2 = ls.localSearch(ind1.cloneIndividual());
		assertNotEquals(ind2, null);
	}
	
	@Test
	@DisplayName("LocalSearch returns an Individual different from the one in input")
	public void testLocalSearch2() {
		LocalSearchAlgorithm ls = LocalSearchAlgorithm.makeLocalSearchHillClimbing();
		Individual ind1 = Individual.randomIndividual();
		Individual ind2 = ls.localSearch(ind1.cloneIndividual());
		assertNotEquals(ind1, ind2);
	}
	
	@Test
	@DisplayName("LocalSearch returns an Individual with an higher fitness than the input one")
	public void testLocalSearch3() {
		LocalSearchAlgorithm ls = LocalSearchAlgorithm.makeLocalSearchHillClimbing();
		Individual ind1 = Individual.randomIndividual();
		Individual ind2 = ls.localSearch(ind1.cloneIndividual());
		assertTrue(ind2.getFitness() > ind1.getFitness());
	}
	
	@Test
	@DisplayName("LocalSearch returns an Individual with at least a negative constraint")
	public void testLocalSearch4() {
		LocalSearchAlgorithm ls = LocalSearchAlgorithm.makeLocalSearchHillClimbing();
		Individual ind1 = Individual.randomIndividual();
		Individual ind2 = ls.localSearch(ind1.cloneIndividual());
		boolean flag = false;
		for(int i = 0; i < ind2.getConstraintSet().size(); i++) {
			for(int j = 0; j < ind1.getConstraintSet().size(); j++) {
				if(ind2.getConstraintSet().get(i) == ind1.getConstraintSet().get(j).mkNot()) {
					flag = true;
				}
			}
		}
		assertTrue(flag);
	}
	
}
