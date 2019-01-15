package gasp.ga.operators.mutation;

import java.util.List;

import gasp.ga.Constraint;
import gasp.utils.RandomNumberSupplier;

public class MutationFunctionDeleteOrNegateConstraint extends MutationFunction {

	@Override
	protected void applyMutation(List<Constraint> constraintSet) throws MutationException {
		if(RandomNumberSupplier._I().nextBoolean()){
			applyDeleteMutation(constraintSet);
		} else {
			applyNegateMutation(constraintSet);
		}
	}
	
	private void applyNegateMutation(List<Constraint> constraintSet) throws MutationException {
        int index = RandomNumberSupplier._I().nextInt(constraintSet.size());

        Constraint neg = constraintSet.get(index).mkNot();

        constraintSet.remove(index);
        constraintSet.add(index, neg);
	}
	
	private void applyDeleteMutation(List<Constraint> constraintSet) throws MutationException {
        int index = RandomNumberSupplier._I().nextInt(constraintSet.size());
 
        constraintSet.remove(index);
	}

}
