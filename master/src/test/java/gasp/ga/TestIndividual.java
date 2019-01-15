package gasp.ga;

import org.junit.jupiter.api.Test;

import gasp.ga.Individual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Individual test suite")
public class TestIndividual {
	
	//TestSuite -> randomIndividual
	@Test
	@DisplayName("randomIndividual generates a not null individual")
	public void testRandomIndividual1() {
		assertNotNull(Individual.makeRandomIndividual());
	}
	
	@Test
	@DisplayName("randomIndividual return has a not null constraint set")
	public void testRandomIndividual2() {
		assertNotEquals(Individual.makeRandomIndividual().getConstraintSetClone(), null);
	}
	
	@Test
	@DisplayName("randomIndividual return has a not empty constraint set")
	public void testRandomIndividual3() {
		assertFalse(Individual.makeRandomIndividual().getConstraintSetClone().isEmpty());
	}
	
	@Test
	@DisplayName("randomIndividual return has a fitness value > 0")
	public void testRandomIndividual4() {
		assertFalse(Individual.makeRandomIndividual().getFitness() <= 0);
	}
	
	//TestSuite -> cloneIndividual
	@Test
	@DisplayName("cloneIndividual generates a copy of a given individual successfully ")
	public void testCloneIndividual1() {
		Individual ind1 = Individual.makeRandomIndividual();
		Individual ind2 = ind1.clone();
		assertEquals(ind1, ind2);
	}
	
	@Test
	@DisplayName("cloneIndividual return has the same constraint set and fitness of the given individual")
	public void testCloneIndividual2() {
		Individual ind1 = Individual.makeRandomIndividual();
		Individual ind2 = ind1.clone();
		assertEquals(ind1.getConstraintSetClone(), ind2.getConstraintSetClone());
		assertEquals(ind1.getFitness(), ind2.getFitness());
	}
	
	//TestSuite -> getModel
	@Test
	@DisplayName("getModel return is not null")
	public void testGetModel1() {
		Individual ind = Individual.makeRandomIndividual();
		assertNotNull(ind.getModel());
	}
	
	@Test
	@DisplayName("getModel return has the correct structure")
	public void testGetModel2() {
		Individual ind = Individual.makeRandomIndividual();
		assertEquals(ind.getModel().toString(), "Model [constraints=" + ind.getConstraintSetClone().toString() + "]");
	}
	
	//TestSuite -> minimize
	//TestSuite -> pcToConstraintSet
	//TestSuite -> fromStrings
	
}
