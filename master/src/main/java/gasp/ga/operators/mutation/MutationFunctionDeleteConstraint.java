package gasp.ga.operators.mutation;

import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.IndividualGenerator;

public class MutationFunctionDeleteConstraint<T extends Gene<T>> extends AbstractMutationFunction<T> {
	public MutationFunctionDeleteConstraint(IndividualGenerator<T> individualGenerator, double mutationProbability, Random random) {
		super(individualGenerator, mutationProbability, random);
	}
	
	@Override
	protected void mutateGene(List<T> chromosome, int position) {
		chromosome.remove(position);
	}
}
