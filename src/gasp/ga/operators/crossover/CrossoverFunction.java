package gasp.ga.operators.crossover;

import java.util.List;

import gasp.ga.Individual;

public abstract class CrossoverFunction{
	
	public abstract List<Individual> crossover(Individual p1, Individual p2); //modificher� i genitori in base al risultato
}
