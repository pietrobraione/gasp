package complexity.utils;

import java.util.Comparator;

import complexity.ga.Individual;

public class SortIndividuals implements Comparator<Individual> {

	@Override
	public int compare (Individual ind2, Individual ind1) {
		Integer ind = new Integer(ind1.fitness); 
		return ind.compareTo(ind2.fitness);
	}

}
