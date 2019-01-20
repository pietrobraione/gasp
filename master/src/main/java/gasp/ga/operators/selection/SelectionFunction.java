package gasp.ga.operators.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gasp.ga.Gene;
import gasp.ga.Individual;

@FunctionalInterface
public interface SelectionFunction<T extends Gene<T>> {
	int selectIndividual(List<Individual<T>> population, boolean populationIsSorted);
	
	default Pair<Individual<T>> selectPairDistinct(List<Individual<T>> population, boolean populationIsSorted) {
		final List<Individual<T>> populationCopy = new ArrayList<>(population);		
		final Pair<Individual<T>> retValue = new Pair<>();

		int index = selectIndividual(populationCopy, populationIsSorted);
		retValue.ind1 = populationCopy.get(index);
		
		populationCopy.remove(index);

		index = selectIndividual(populationCopy, true /* sorted at previous step */);
		retValue.ind2 = populationCopy.get(index);
		
		return retValue;
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
