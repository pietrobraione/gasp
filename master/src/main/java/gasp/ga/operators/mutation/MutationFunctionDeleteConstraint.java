package gasp.ga.operators.mutation;

import java.util.List;

import gasp.ga.Constraint;
import gasp.utils.RandomNumberSupplier;

public class MutationFunctionDeleteConstraint extends MutationFunction {
	@Override
	protected void applyMutation(List<Constraint> constraintSet) throws MutationException {
        int index = RandomNumberSupplier._I().nextInt(constraintSet.size());
        constraintSet.remove(index);
	}
}
