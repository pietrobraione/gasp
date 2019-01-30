package gasp.ga.operators.mutation;

import java.util.List;
import java.util.Random;

import gasp.ga.Gene;

public class MutationFunctionDelete<T extends Gene<T>> extends AbstractMutationFunction<T> {
	public MutationFunctionDelete(double mutationProbability, double mutationSizeRatio, Random random) {
		super(mutationProbability, mutationSizeRatio, random);
	}
	
	@Override
	protected void mutateGene(List<T> chromosome, int position) {
		chromosome.remove(position);
	}
}
