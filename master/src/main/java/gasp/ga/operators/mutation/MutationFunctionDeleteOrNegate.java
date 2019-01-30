package gasp.ga.operators.mutation;

import java.util.List;
import java.util.Random;

import gasp.ga.Gene;

public class MutationFunctionDeleteOrNegate<T extends Gene<T>> extends AbstractMutationFunction<T> {
	public MutationFunctionDeleteOrNegate(double mutationProbability, double mutationSizeRatio, Random random) {
		super(mutationProbability, mutationSizeRatio, random);
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
