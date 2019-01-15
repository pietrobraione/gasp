package gasp.ga.operators.crossover;

import gasp.ga.Individual;

public abstract class CrossoverFunction {
	
	public abstract Individual[] crossover(Individual parent1, Individual parent2) throws CrossoverException;
}
