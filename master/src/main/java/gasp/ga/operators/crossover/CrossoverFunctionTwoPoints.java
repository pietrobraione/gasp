package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.Pair;

public final class CrossoverFunctionTwoPoints<T extends Gene<T>> implements CrossoverFunction<T> {
	@Override
	public Pair<List<T>> doCrossover(long seed, List<T> chromosome1, List<T> chromosome2) {
		if (chromosome1 == null || chromosome2 == null) {
			throw new IllegalArgumentException("The chromosomes cannot be null.");
		}
		if (chromosome1.size() < 2 || chromosome2.size() < 2) {
			return null; //failure
		}
		if (chromosome1.size() < 3 || chromosome2.size() < 3) {
			final CrossoverFunctionSinglePoint<T> delegate = new CrossoverFunctionSinglePoint<>();
			return delegate.doCrossover(seed, chromosome1, chromosome2);
		}
		
		final Random random = new Random(seed);
		
    	final int cutPoint11 = random.nextInt(chromosome1.size() - 2) + 1;
    	final int cutPoint12 = random.nextInt(chromosome1.size() - cutPoint11) + cutPoint11 + 1;
    	final int cutPoint21 = random.nextInt(chromosome2.size() - 2) + 1;
    	final int cutPoint22 = random.nextInt(chromosome2.size() - cutPoint21) + cutPoint21 + 1;
		
        final List<T> chromosomeCombined1 = combineChromosomes(chromosome1.subList(0, cutPoint11), chromosome2.subList(cutPoint21, cutPoint22), chromosome1.subList(cutPoint12, chromosome1.size()));
        final List<T> chromosomeCombined2 = combineChromosomes(chromosome2.subList(0, cutPoint21), chromosome1.subList(cutPoint11, cutPoint12), chromosome2.subList(cutPoint22, chromosome2.size()));	        

        return new Pair<>(chromosomeCombined1, chromosomeCombined2);
	}
	
	private List<T> combineChromosomes(List<T> chromosome1, List<T> chromosome2, List<T> chromosome3) {
		final List<T> retVal = new ArrayList<>(chromosome1);
		retVal.addAll(chromosome2);
		retVal.addAll(chromosome3);
		return retVal;
	}
}
