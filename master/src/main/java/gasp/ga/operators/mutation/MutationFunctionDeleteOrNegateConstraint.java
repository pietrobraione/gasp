package gasp.ga.operators.mutation;

import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.IndividualGenerator;

public class MutationFunctionDeleteOrNegateConstraint<T extends Gene<T>> extends AbstractMutationFunction<T> {
	public MutationFunctionDeleteOrNegateConstraint(IndividualGenerator<T> individualGenerator, double mutationProbability, Random random) {
		super(individualGenerator, mutationProbability, random);
	}
	

	@Override
	protected void mutateGene(List<T> chromosome, int position) {
		if (this.random.nextBoolean()){
			applyDeleteMutation(chromosome, position);
		} else {
			applyNegateMutation(chromosome, position);
		}
	}
	
	private void applyDeleteMutation(List<T> chromosome, int position) {
        chromosome.remove(position);
	}

	private void applyNegateMutation(List<T> chromosome, int position) {
        final T neg = chromosome.get(position).not();
        chromosome.remove(position);
        chromosome.add(position, neg);
	}
}
