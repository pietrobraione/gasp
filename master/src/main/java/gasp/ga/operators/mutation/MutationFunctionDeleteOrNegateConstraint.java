package gasp.ga.operators.mutation;

import java.util.List;
import java.util.Random;

import gasp.ga.Constraint;
import gasp.ga.fitness.FitnessFunction;

public class MutationFunctionDeleteOrNegateConstraint extends AbstractMutationFunction {
	public MutationFunctionDeleteOrNegateConstraint(FitnessFunction fitnessFunction, double mutationProbability, Random random) {
		super(fitnessFunction, mutationProbability, random);
	}
	

	@Override
	protected void applyMutation(List<Constraint> constraintSet) throws MutationException {
		if (this.random.nextBoolean()){
			applyDeleteMutation(constraintSet);
		} else {
			applyNegateMutation(constraintSet);
		}
	}
	
	private void applyNegateMutation(List<Constraint> constraintSet) throws MutationException {
		final int index = this.random.nextInt(constraintSet.size());
        Constraint neg = constraintSet.get(index).not();
        constraintSet.remove(index);
        constraintSet.add(index, neg);
	}
	
	private void applyDeleteMutation(List<Constraint> constraintSet) throws MutationException {
        final int index = this.random.nextInt(constraintSet.size());
        constraintSet.remove(index);
	}

}
