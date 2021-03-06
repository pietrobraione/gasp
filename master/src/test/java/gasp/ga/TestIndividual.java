package gasp.ga;

import static settings.Settings.*;

import org.junit.jupiter.api.Test;

import gasp.ga.individualGenerator.IndividualGeneratorJBSE;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;

@DisplayName("gasp.ga.Individual test suite")
public class TestIndividual {
	private static final long SEED = 0;
	private static final long MAX_FITNESS = 1_000_000;
	private static final String METHOD_CLASS_NAME = "smalldemos/ifx/IfExample";
	private static final String METHOD_DESCRIPTOR = "(I)V";
	private static final String METHOD_NAME = "m";
	
	private IndividualGeneratorJBSE ig() {
		return new IndividualGeneratorJBSE(MAX_FITNESS, CLASSPATH, JBSE_PATH, Z3_PATH, METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
	}

	@Test
	@DisplayName("Individual.clone() generates an individual that is equal to the cloned one")
	public void testCloneIndividual1() throws FoundWorstIndividualException {
		final Individual<?> ind1 = ig().generateRandomIndividual(SEED);
		final Individual<?> ind2 = ind1.clone();
		assertEquals(ind1, ind2);
	}
	
	@Test
	@DisplayName("Individual.clone() has the same chromosome of the cloned individual")
	public void testCloneIndividual2() throws FoundWorstIndividualException {
		final Individual<?> ind1 = ig().generateRandomIndividual(SEED);
		final Individual<?> ind2 = ind1.clone();
		assertEquals(ind1.getChromosome(), ind2.getChromosome());
	}
	
	@Test
	@DisplayName("Individual.clone() has the same fitness of the cloned individual")
	public void testCloneIndividual3() throws FoundWorstIndividualException {
		final Individual<?> ind1 = ig().generateRandomIndividual(SEED);
		final Individual<?> ind2 = ind1.clone();
		assertEquals(ind1.getFitness(), ind2.getFitness());
	}
}
