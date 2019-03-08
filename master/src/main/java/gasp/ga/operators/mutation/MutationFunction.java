package gasp.ga.operators.mutation;

import java.util.List;

import gasp.ga.Gene;

@FunctionalInterface
public interface MutationFunction<T extends Gene<T>> {
	List<T> mutate(long seed, List<T> chromosome);
}
