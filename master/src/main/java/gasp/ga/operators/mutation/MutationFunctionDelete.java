package gasp.ga.operators.mutation;

import java.util.List;
import java.util.Random;

import gasp.ga.Gene;

public class MutationFunctionDelete<T extends Gene<T>> extends AbstractMutationFunction<T> {
	public MutationFunctionDelete(double mutationProbability, double mutationSizeRatio) {
		super(mutationProbability, mutationSizeRatio);
	}
	
	@Override
	protected void mutateGene(List<T> chromosome, int position, Random random) {
		chromosome.remove(position);
	}
}
