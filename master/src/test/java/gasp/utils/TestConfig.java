package gasp.utils;

import org.junit.jupiter.api.Test;

import gasp.utils.Config;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Config test suite")
public class TestConfig {
	@Test
	@DisplayName("The number of estimated fitness evaluations is > 0")
	public void testFitnessEval(){
		assertTrue(Config.estimateFitnessEvaluations() > 0);
	}
}
