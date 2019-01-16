package gasp.ga.operators.mutation;

import java.util.List;
import java.util.Random;

import gasp.ga.Constraint;
import gasp.ga.Individual;
import gasp.ga.fitness.FitnessEvaluationException;
import gasp.ga.fitness.FitnessFunction;

public abstract class AbstractMutationFunction implements MutationFunction {
	protected final FitnessFunction fitnessFunction;
	protected final double mutationProbability;
	protected final Random random;
	
	protected AbstractMutationFunction(FitnessFunction fitnessFunction, double mutationProbability, Random random) {
		if (fitnessFunction == null) {
			throw new IllegalArgumentException("Fitness function cannot be null.");
		}
		if (mutationProbability < 0 || mutationProbability > 1) {
			throw new IllegalArgumentException("The mutation probability cannot be less than 0 or greater than 1.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		
		this.fitnessFunction = fitnessFunction;
		this.mutationProbability = mutationProbability;
		this.random = random;
	}

	@Override
	public void applyMutationToConstraintSetPortion(List<Constraint> constraintSet, double portion) throws MutationException {
		if (portion <= 0d || portion >= 1d) {
			throw new MutationException("Cannot mutate (" + portion + " * constraintSetSize) constraints");
		}
		
		int numOfMutations = (int) Math.round(portion * constraintSet.size());
        
		for(int i = 0; i < numOfMutations; i++){
			applySingleMutation(constraintSet);
        }
	}
	
	private void applySingleMutation(List<Constraint> constraintSet) throws MutationException {
        if (this.random.nextDouble() < this.mutationProbability) {
        	applyMutation(constraintSet);
        }
	}
	
	protected abstract void applyMutation(List<Constraint> constraintSet) throws MutationException;

	//TODO the methods that follow seem unused; delete them?
	
	public Individual mutateIndividualByApplyingMutationToConstraintSetPortion(Individual individual, double portion) throws MutationException {
		List<Constraint> constraintSet = individual.getConstraintSetClone();
		
		applyMutationToConstraintSetPortion(constraintSet, portion);
		
		return makeIndividual(constraintSet);
	}

	public Individual mutateIndividualByApplyingSingleMutation(Individual individual, double portion) throws MutationException {
		List<Constraint> constraintSet = individual.getConstraintSetClone();
		
		applySingleMutation(constraintSet);
		
		return makeIndividual(constraintSet);
	}
	
	private Individual makeIndividual(List<Constraint> constraintSet) throws MutationException {
        try {
			final Individual newIndividual = this.fitnessFunction.evaluate(constraintSet);
	        return newIndividual;
		} catch (FitnessEvaluationException e) {
			throw new MutationException(e);
		}
	}
}
