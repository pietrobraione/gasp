package complexity.ga;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import complexity.utils.Config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("FitnessFunction test suite")
public class TestFitnessFunction {
	
	//TestSuite -> evaluate
	@Test
	@DisplayName("evaluate returns is not null")
	public void testEvaluate1(){
		Individual ind = Individual.randomIndividual();
		assertNotNull(FitnessFunction.evaluate(ind.getConstraintSet()));
	}
	
	@Test
	@DisplayName("evaluate returns is different from the input")
	public void testEvaluate2(){
		Individual ind = Individual.randomIndividual();
		assertNotEquals(FitnessFunction.evaluate(ind.getConstraintSet()).getFitness(), ind.getFitness());
		assertNotEquals(FitnessFunction.evaluate(ind.getConstraintSet()).getConstraintSet(), ind.getConstraintSet());
	}
	
	//TestSuite -> estimateFitnessEvaluations
	@Test
	@DisplayName("The number of fitness evaluations is > 0")
	public void testFitnessEval(){
		assertTrue(FitnessFunction.estimateFitnessEvaluations(Config.generations, Config.populationSize, Config.mutationProb) > 0);
	}
}
