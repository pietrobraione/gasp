package gasp.ga.operators.selection;

import java.util.ArrayList;
import java.util.List;

import gasp.ga.Gene;
import gasp.ga.Individual;

@FunctionalInterface
public interface SelectionFunction<T extends Gene<T>, U extends Individual<T>> {
	int selectIndividual(List<U> population);
	
	default List<Integer> select(List<U> population, int n) {
		final ArrayList<U> populationCopy = new ArrayList<>(population);

		final ArrayList<Integer> indices = new ArrayList<>();
		for (int i = 0; i < population.size(); ++i) {
			indices.add(i);
		}
		final ArrayList<Integer> retValue = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            final int index = selectIndividual(populationCopy);
            retValue.add(indices.get(index));
            populationCopy.remove(index);
            indices.remove(index);
        }
        
        return retValue;
	}
}
