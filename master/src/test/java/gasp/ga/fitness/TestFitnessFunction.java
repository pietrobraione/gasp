package gasp.ga.fitness;

import org.junit.jupiter.api.Test;

import gasp.ga.Individual;
import gasp.ga.fitness.FitnessEvaluationException;
import gasp.utils.Config;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("FitnessFunction test suite")
public class TestFitnessFunction {
	
	@Test
	@DisplayName("FitnessFunction.evaluate does not return null")
	public void testEvaluate1() throws FitnessEvaluationException {
		final Individual ind = Individual.makeRandomIndividual();
		assertNotNull(Config.fitnessFunction.evaluate(ind.getConstraintSetClone()));
	}
	
	@Test
	@DisplayName("FitnessFunction.evaluate returns is different from the input")
	public void testEvaluate2() throws FitnessEvaluationException {
		final Individual ind = Individual.makeRandomIndividual();
		assertNotEquals(Config.fitnessFunction.evaluate(ind.getConstraintSetClone()).getFitness(), ind.getFitness());
		assertNotEquals(Config.fitnessFunction.evaluate(ind.getConstraintSetClone()).getConstraintSetClone(), ind.getConstraintSetClone());
	}
}
