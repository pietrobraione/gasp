package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.Pair;

public final class CrossoverFunctionSinglePoint<T extends Gene<T>> implements CrossoverFunction<T> {
	private final Random random;
	
	public CrossoverFunctionSinglePoint(Random random) {
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}

		this.random = random;
	}

	@Override
	public Pair<List<T>> doCrossover(List<T> chromosome1, List<T> chromosome2) {
		if (chromosome1 == null || chromosome2 == null) {
			throw new IllegalArgumentException("The chromosomes cannot be null.");
		}
		if (chromosome1.size() < 2 || chromosome2.size() < 2) {
			return null; //failure, chromosomes must have at least length 2
		}
		
		final int cutPoint1 = this.random.nextInt(chromosome1.size() - 1) + 1;
		final int cutPoint2 = this.random.nextInt(chromosome2.size() - 1) + 1;
				
        final List<T> chromosomeCombined1 = combineChromosomes(chromosome1.subList(0, cutPoint1), chromosome2.subList(cutPoint2, chromosome2.size()));
        final List<T> chromosomeCombined2 = combineChromosomes(chromosome2.subList(0, cutPoint2), chromosome1.subList(cutPoint1, chromosome1.size()));	        

        return new Pair<>(chromosomeCombined1, chromosomeCombined2);
	}
	
	private List<T> combineChromosomes(List<T> chromosome1, List<T> chromosome2) {
		final List<T> retVal = new ArrayList<>(chromosome1);
		retVal.addAll(chromosome2);
		return retVal;
	}
}