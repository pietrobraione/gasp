package gasp.ga.operators.mutation;

import java.util.List;
import java.util.Random;

import gasp.ga.Constraint;
import gasp.ga.fitness.FitnessFunction;

public class MutationFunctionDeleteConstraint extends AbstractMutationFunction {
	public MutationFunctionDeleteConstraint(FitnessFunction fitnessFunction, double mutationProbability, Random random) {
		super(fitnessFunction, mutationProbability, random);
	}
	
	@Override
	protected void applyMutation(List<Constraint> constraintSet) throws MutationException {
        int index = this.random.nextInt(constraintSet.size());
        constraintSet.remove(index);
	}
}
