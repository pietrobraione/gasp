package gasp;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("gasp.Options test suite")
public class TestOptions {
	@Test
	@DisplayName("Options.estimateFitnessEvaluations() must always be greater than 0")
	public void testFitnessEval() {
		final Options o = new Options();
		assertTrue(o.estimateFitnessEvaluations() > 0);
		//TODO try on more points
	}
}
