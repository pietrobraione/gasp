package gasp.ga.operators.crossover;

import gasp.ga.Individual;

@FunctionalInterface
public interface CrossoverFunction {
	Individual[] crossover(Individual parent1, Individual parent2) throws CrossoverException;
}
