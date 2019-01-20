package gasp.ga.operators.mutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.Individual;
import gasp.ga.IndividualGenerator;

public abstract class AbstractMutationFunction<T extends Gene<T>> implements MutationFunction<T> {
	protected final IndividualGenerator<T> individualGenerator;
	protected final double mutationProbability;
	protected final Random random;
	
	protected AbstractMutationFunction(IndividualGenerator<T> individualGenerator, double mutationProbability, Random random) {
		if (individualGenerator == null) {
			throw new IllegalArgumentException("The individual generator cannot be null.");
		}
		if (mutationProbability < 0 || mutationProbability > 1) {
			throw new IllegalArgumentException("The mutation probability cannot be less than 0 or greater than 1.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		
		this.individualGenerator = individualGenerator;
		this.mutationProbability = mutationProbability;
		this.random = random;
	}

	@Override
	public List<T> mutate(List<T> chromosome, double percentage) {
		if (chromosome == null) {
			throw new IllegalArgumentException("Chromosome cannot be null.");
		}
		if (percentage < 0 || percentage > 1) {
			throw new IllegalArgumentException("Percentage cannot be less than 0 or greater than 1.");
		}
		
		final ArrayList<T> retVal = new ArrayList<>(chromosome);
		final int numOfMutations = (int) Math.round(percentage * chromosome.size());
		final ArrayList<Integer> positions = new ArrayList<Integer>();
		for (int i = 0; i < chromosome.size(); ++i) {
			positions.add(i);
		}
		Collections.shuffle(positions, this.random);
		for (int i = 0; i < numOfMutations; ++i) {
	        if (this.random.nextDouble() < this.mutationProbability) {
	        	mutateGene(retVal, positions.get(i));
	        }
        }
		return retVal;
	}
	
	protected abstract void mutateGene(List<T> chromosome, int position);
	
	//TODO these are unused; remove?
	
	public final Individual<T> mutateIndividual(Individual<T> individual, double percentage) {
		final List<T> chromosome = individual.getChromosome();
		final List<T> chromosomeMutated = mutate(chromosome, percentage);
		return this.individualGenerator.generateRandomIndividual(chromosomeMutated);
	}

	public Individual<T> mutateIndividual(Individual<T> individual) {
		final List<T> chromosome = individual.getChromosome();
		mutateGene(chromosome, this.random.nextInt(chromosome.size()));
		return this.individualGenerator.generateRandomIndividual(chromosome);
	}
}
