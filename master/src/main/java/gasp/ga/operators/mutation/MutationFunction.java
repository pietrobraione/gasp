package gasp.ga.operators.mutation;

import java.util.List;

import gasp.ga.Constraint;

@FunctionalInterface
public interface MutationFunction {
	void applyMutationToConstraintSetPortion(List<Constraint> constraintSet, double portion) throws MutationException;
}
