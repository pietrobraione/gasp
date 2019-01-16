package gasp.ga.operators.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gasp.ga.Individual;

@FunctionalInterface
public interface SelectionFunction {
	int selectIndividual(List<Individual> population, boolean populationIsSorted);
	
	public static class Pair {
		public Individual ind1;
		public Individual ind2;
	}
	
	default Pair selectPairDistinct(List<Individual> population, boolean populationIsSorted) {
		final List<Individual> populationCopy = new ArrayList<>(population);
		
		final Pair retValue = new Pair();
		
		int index = selectIndividual(populationCopy, populationIsSorted);
		retValue.ind1 = populationCopy.get(index);
		
		populationCopy.remove(index);

		index = selectIndividual(populationCopy, true /* sorted at previous step */);
		retValue.ind2 = populationCopy.get(index);
		
		return retValue;
	}

	default List<Individual> survivalSelection(List<Individual> population, int n) {
		final List<Individual> populationCopy = new ArrayList<>(population);
		Collections.sort(populationCopy);

		final List<Individual> retValue = new ArrayList<Individual>();
        for(int i = 0; i < n; i++) {
            int index = selectIndividual(populationCopy, true);

            retValue.add(populationCopy.get(index));
            
            populationCopy.remove(index);
        }
        
        return retValue;
	}
}
