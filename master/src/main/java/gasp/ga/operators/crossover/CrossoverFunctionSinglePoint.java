package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.IndividualGenerator;
import gasp.ga.Individual;
import gasp.ga.operators.mutation.MutationFunction;

public final class CrossoverFunctionSinglePoint<T extends Gene<T>> implements CrossoverFunction<T> {
	private final IndividualGenerator<T> individualGenerator;
	private final MutationFunction<T> mutationFunction;
	private final double mutationSizeRatio;
	private final Random random;
	
	public CrossoverFunctionSinglePoint(IndividualGenerator<T> individualGenerator, MutationFunction<T> mutationFunction, 
										double mutationSizeRatio, Random random) {
		if (individualGenerator == null) {
			throw new IllegalArgumentException("The individual generator cannot be null.");
		}
		if (mutationFunction == null) {
			throw new IllegalArgumentException("Mutation function cannot be null.");
		}
		if (mutationSizeRatio < 0 || mutationSizeRatio > 1) {
			throw new IllegalArgumentException("Mutation size ratio cannot be less than 0 or greater than 1.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}

		this.individualGenerator = individualGenerator;
		this.mutationFunction = mutationFunction;
		this.mutationSizeRatio = mutationSizeRatio;
		this.random = random;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Individual<T>[] crossover(Individual<T> parent1, Individual<T> parent2) throws CrossoverException {
		final List<T> chromosomeParent1 = parent1.getChromosome();
		final List<T> chromosomeParent2 = parent2.getChromosome();
		final int cutPoint1, cutPoint2;
		
		if (chromosomeParent1.size() > 0) {
			cutPoint1 = this.random.nextInt(chromosomeParent1.size());
		} else {
			throw new CrossoverException("Crossover produced no children: parent1 has no constraints");
		}
		
		if (chromosomeParent2.size() > 0) {
			cutPoint2 = this.random.nextInt(chromosomeParent2.size());
		} else {
			throw new CrossoverException("Crossover produced no children: parent2 has no constraints");
		}
				
        final ArrayList<Individual<T>> children = new ArrayList<>();

        final List<T> chromosomeCombined1 = combineChromosomes(chromosomeParent1.subList(0, cutPoint1), chromosomeParent2.subList(cutPoint2, chromosomeParent2.size()));
        final List<T> chromosomeCombinedAndMutated1 = this.mutationFunction.mutate(chromosomeCombined1, this.mutationSizeRatio);
        final Individual<T> child1 = this.individualGenerator.generateRandomIndividual(chromosomeCombinedAndMutated1);
        if (child1 != null) {
        	children.add(child1);
        }

        final List<T> chromosomeCombined2 = combineChromosomes(chromosomeParent2.subList(0, cutPoint2), chromosomeParent1.subList(cutPoint1, chromosomeParent1.size()));	        
        final List<T> chromosomeCombinedAndMutated2 = this.mutationFunction.mutate(chromosomeCombined2, this.mutationSizeRatio);
        final Individual<T> child2 = this.individualGenerator.generateRandomIndividual(chromosomeCombinedAndMutated2);
        if (child2 != null) {
        	children.add(child2);
        }

		if (children.isEmpty()) {
			throw new CrossoverException("Crossover produced no children.");
		}

        return children.toArray(new Individual[children.size()]);
	}
	
	private List<T> combineChromosomes(List<T> chromosome1, List<T> chromosome2) {
		final List<T> result = new ArrayList<>(chromosome1);
		for (T gene : chromosome2) {
			/* TODO
			final List<T> slice = this.individualGenerator.slice(result, gene);
			if (!Utils.isInconsistent(c, slice) && !Utils.isRedundant(c, slice)) { */
				result.add(gene);
			/*}*/
		}
	
		return result;
	}
}