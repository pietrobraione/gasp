package gasp.ga.localSearch;

import gasp.ga.Gene;
import gasp.ga.Individual;

@FunctionalInterface
public interface LocalSearchAlgorithm<T extends Gene<T>> {
	Individual<T> doLocalSearch(Individual<T> individual);
}
