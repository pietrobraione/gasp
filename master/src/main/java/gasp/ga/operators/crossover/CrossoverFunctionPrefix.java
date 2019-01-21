package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.Pair;

public class CrossoverFunctionPrefix<T extends Gene<T>> implements CrossoverFunction<T> {
	private final Random random;
	
	public CrossoverFunctionPrefix(Random random) {		
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
			return null; //failure
		}
		if (chromosome1.size() < 3 || chromosome2.size() < 3) {
			final CrossoverFunctionSinglePoint<T> delegate = new CrossoverFunctionSinglePoint<>(this.random);
			return delegate.doCrossover(chromosome1, chromosome2);
		}
		
    	final int cutPoint11 = this.random.nextInt(chromosome1.size() - 2) + 1;
    	final int cutPoint12 = this.random.nextInt(chromosome1.size() - cutPoint11) + cutPoint11 + 1;
    	final int cutPoint21 = this.random.nextInt(chromosome2.size() - 2) + 1;
    	final int cutPoint22 = this.random.nextInt(chromosome2.size() - cutPoint21) + cutPoint21 + 1;
		
		final List<T> chromosomeCombined1 = combineChromosomes(chromosome1.subList(0, cutPoint11), (this.random.nextBoolean() ? chromosome2.subList(cutPoint21, cutPoint22) : chromosome2.subList(cutPoint22, chromosome2.size())));
		final List<T> chromosomeCombined2 = combineChromosomes(chromosome2.subList(0, cutPoint21), (this.random.nextBoolean() ? chromosome1.subList(cutPoint11, cutPoint12) : chromosome1.subList(cutPoint12, chromosome1.size())));
  
        return new Pair<>(chromosomeCombined1, chromosomeCombined2);
	}
	
	private List<T> combineChromosomes(List<T> chromosome1, List<T> chromosome2) {
		final List<T> result = new ArrayList<>(chromosome1);
		result.addAll(chromosome2);
        return result;
	}	
}
