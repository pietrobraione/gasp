package gasp.ga.fitness;

import java.util.List;

import gasp.ga.Constraint;
import gasp.ga.Individual;

@FunctionalInterface
public interface FitnessFunction {
	Individual evaluate(List<Constraint> constraintSet) throws FitnessEvaluationException;
}
