package gasp.ga.operators.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gasp.ga.Gene;
import gasp.ga.Individual;
import gasp.ga.Pair;

@FunctionalInterface
public interface SelectionFunction<T extends Gene<T>> {
	int selectIndividual(List<Individual<T>> population, boolean populationIsSorted);
	
	default Pair<Individual<T>> selectPairDistinct(List<Individual<T>> population, boolean populationIsSorted) {
		final List<Individual<T>> populationCopy = new ArrayList<>(population);		

		final int index1 = selectIndividual(populationCopy, populationIsSorted);
		final Individual<T> individual1 = populationCopy.get(index1);
		
		populationCopy.remove(index1);

		final int index2 = selectIndividual(populationCopy, true /* sorted at previous step */);
		final Individual<T> individual2 = populationCopy.get(index2);
		
		return new Pair<>(individual1, individual2);
	}

	default List<Individual<T>> survivalSelection(List<Individual<T>> population, int n) {
		final List<Individual<T>> populationCopy = new ArrayList<>(population);
		Collections.sort(populationCopy);

		final List<Individual<T>> retValue = new ArrayList<Individual<T>>();
        for (int i = 0; i < n; ++i) {
            final int index = selectIndividual(populationCopy, true);
            retValue.add(populationCopy.get(index));
            populationCopy.remove(index);
        }
        
        return retValue;
	}
}
