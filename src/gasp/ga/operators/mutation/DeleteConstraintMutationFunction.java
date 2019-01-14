package gasp.ga.operators.mutation;

import java.util.List;

import gasp.se.Constraint;
import gasp.utils.RandomNumberSupplier;

public class DeleteConstraintMutationFunction extends MutationFunction {

	@Override
	protected void applyMutation(List<Constraint> constraintSet) throws MutationException {
        int index = RandomNumberSupplier._I().nextInt(constraintSet.size());
        constraintSet.remove(index);
	}
}
