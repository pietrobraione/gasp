package complexity.ga.operators.crossover;

import java.util.List;

import complexity.ga.Individual;

public abstract class CrossoverFunction{
	
	public abstract List<Individual> crossover(Individual p1, Individual p2); //modificherà i genitori in base al risultato
}
