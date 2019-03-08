package gasp.ga.operators.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;

public abstract class AbstractMutationFunction<T extends Gene<T>> implements MutationFunction<T> {
	protected final double mutationProbability;
	protected final double mutationSizeRatio;
	
	protected AbstractMutationFunction(double mutationProbability, double mutationSizeRatio) {
		if (mutationProbability < 0 || mutationProbability > 1) {
			throw new IllegalArgumentException("The mutation probability cannot be less than 0 or greater than 1.");
		}
		if (mutationSizeRatio < 0 || mutationSizeRatio > 1) {
			throw new IllegalArgumentException("The mutation size ratio cannot be less than 0 or greater than 1.");
		}
		
		this.mutationProbability = mutationProbability;
		this.mutationSizeRatio = mutationSizeRatio;
	}

	@Override
	public final List<T> mutate(long seed, List<T> chromosome) {
		if (chromosome == null) {
			throw new IllegalArgumentException("Chromosome cannot be null.");
		}
		
		final Random random = new Random(seed);
		
		final ArrayList<T> retVal = new ArrayList<>(chromosome);
		final int numOfMutations = (int) Math.round(this.mutationSizeRatio * chromosome.size());
		for (int i = 0; i < numOfMutations; ++i) {
	        if (random.nextDouble() < this.mutationProbability) {
	        	final int position = random.nextInt(retVal.size());
	        	mutateGene(retVal, position, random);
	        }
        }
		return retVal;
	}
	
	protected abstract void mutateGene(List<T> chromosome, int position, Random random);
}
