package gasp.ga.operators.mutation;

import java.util.List;

import gasp.ga.FitnessEvaluationException;
import gasp.ga.FitnessFunction;
import gasp.ga.Individual;
import gasp.se.Constraint;
import gasp.utils.Config;
import gasp.utils.RandomNumberSupplier;

public abstract class MutationFunction {
	
	protected abstract void applyMutation(List<Constraint> constraintSet) throws MutationException;

	public void applySingleMutation(List<Constraint> constraintSet) throws MutationException {
        if(RandomNumberSupplier._I().nextDouble() < Config.mutationProb) {
        	applyMutation(constraintSet);
        }
	}

	public void applyMutationToConstraintSetPortion(List<Constraint> constraintSet, double portion) throws MutationException {
		if (portion <= 0d || portion >= 1d) {
			throw new MutationException("Cannot mutate (" + portion + " * constraintSetSize) constraints");
		}
		
		int numOfMutations = (int) Math.round(portion * constraintSet.size());
        
		for(int i = 0; i < numOfMutations; i++){
			applySingleMutation(constraintSet);
        }
	}
	
	public Individual mutateIndividuaByApplyingMutationToConstraintSetPortion(Individual individual, double portion) throws MutationException {
		List<Constraint> constraintSet = individual.getConstraintSetClone();
		
		applyMutationToConstraintSetPortion(constraintSet, portion);
		
		return makeIndividual(constraintSet);
	}

	public Individual mutateIndividuaByApplyingSingleMutation(Individual individual, double portion) throws MutationException {
		List<Constraint> constraintSet = individual.getConstraintSetClone();
		
		applySingleMutation(constraintSet);
		
		return makeIndividual(constraintSet);
	}
	
	private Individual makeIndividual(List<Constraint> constraintSet) throws MutationException {
        try {
			Individual newIndividual = FitnessFunction.evaluate(constraintSet);
	        return newIndividual;
		} catch (FitnessEvaluationException e) {
			throw new MutationException(e);
		}
		
	}
}
