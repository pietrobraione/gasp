package gasp.ga.localSearch;

import gasp.ga.Individual;

@FunctionalInterface
public interface LocalSearchAlgorithm {
	Individual doLocalSearch(Individual individual);
}
