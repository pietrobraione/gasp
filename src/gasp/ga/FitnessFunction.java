package gasp.ga;

import java.util.List;

import gasp.se.Constraint;
import gasp.se.Symex;

public class FitnessFunction {

	public static Individual evaluate(List<Constraint> constraintSet) throws FitnessEvaluationException {
		Symex se = Symex.makeEngine();
		
		List<Constraint> pc = se.randomWalkSymbolicExecution(constraintSet);
		
        return new Individual(pc, se.getInstructionCount());
	}
		
}
