package gasp.ga.localSearch;

import gasp.ga.FoundWorstIndividualException;
import gasp.ga.Gene;
import gasp.ga.Individual;

@FunctionalInterface
public interface LocalSearchAlgorithm<T extends Gene<T>, U extends Individual<T>> {
	U doLocalSearch(long seed, U individual) throws FoundWorstIndividualException;
}
