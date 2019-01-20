package gasp.ga.operators.crossover;

import gasp.ga.Gene;
import gasp.ga.Individual;

@FunctionalInterface
public interface CrossoverFunction<T extends Gene<T>> {
	Individual<T>[] crossover(Individual<T> parent1, Individual<T> parent2) throws CrossoverException;
}
