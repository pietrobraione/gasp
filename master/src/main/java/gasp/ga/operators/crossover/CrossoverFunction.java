package gasp.ga.operators.crossover;

import java.util.List;

import gasp.ga.Gene;
import gasp.ga.Pair;

@FunctionalInterface
public interface CrossoverFunction<T extends Gene<T>> {
	Pair<List<T>> doCrossover(List<T> chromosome1, List<T> chromosome2);
}
