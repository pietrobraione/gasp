package gasp.ga.operators.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;

public abstract class AbstractMutationFunction<T extends Gene<T>> implements MutationFunction<T> {
	protected final double mutationProbability;
	protected final double mutationSizeRatio;
	protected final Random random;
	
	protected AbstractMutationFunction(double mutationProbability, double mutationSizeRatio, Random random) {
		if (mutationProbability < 0 || mutationProbability > 1) {
			throw new IllegalArgumentException("The mutation probability cannot be less than 0 or greater than 1.");
		}
		if (mutationSizeRatio < 0 || mutationSizeRatio > 1) {
			throw new IllegalArgumentException("The mutation size ratio cannot be less than 0 or greater than 1.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		
		this.mutationProbability = mutationProbability;
		this.mutationSizeRatio = mutationSizeRatio;
		this.random = random;
	}

	@Override
	public List<T> mutate(List<T> chromosome) {
		if (chromosome == null) {
			throw new IllegalArgumentException("Chromosome cannot be null.");
		}
		
		final ArrayList<T> retVal = new ArrayList<>(chromosome);
		final int numOfMutations = (int) Math.round(this.mutationSizeRatio * chromosome.size());
		for (int i = 0; i < numOfMutations; ++i) {
	        if (this.random.nextDouble() < this.mutationProbability) {
	        	final int position = this.random.nextInt(retVal.size());
	        	mutateGene(retVal, position);
	        }
        }
		return retVal;
	}
	
	protected abstract void mutateGene(List<T> chromosome, int position);
}
