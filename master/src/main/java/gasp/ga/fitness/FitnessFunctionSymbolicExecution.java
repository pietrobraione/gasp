package gasp.ga.fitness;

import java.util.List;

import gasp.ga.Constraint;
import gasp.ga.Individual;
import gasp.se.Symex;

public class FitnessFunctionSymbolicExecution implements FitnessFunction {
	public Individual evaluate(List<Constraint> constraintSet) throws FitnessEvaluationException {
		final Symex se = Symex.makeEngine();
		final List<Constraint> pc = se.randomWalkSymbolicExecution(constraintSet);
        return new Individual(pc, se.getInstructionCount());
	}
}
