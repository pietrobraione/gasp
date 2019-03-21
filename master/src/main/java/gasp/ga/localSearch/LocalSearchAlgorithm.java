package gasp.ga.localSearch;

import java.util.function.Supplier;

import gasp.ga.FoundWorstIndividualException;
import gasp.ga.Gene;
import gasp.ga.Individual;

@FunctionalInterface
public interface LocalSearchAlgorithm<T extends Gene<T>, U extends Individual<T>> {
	U doLocalSearch(long seed, U individual, Supplier<Boolean> timedOut) throws FoundWorstIndividualException;
}
