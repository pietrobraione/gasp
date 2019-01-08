package gasp.localSearch;

import gasp.ga.Individual;

public abstract class LocalSearchAlgorithm {
	
	public abstract Individual localSearch(Individual individual);
	
	public static LocalSearchAlgorithm makeLocalSearchHillClimbing() {
		return new LocalSearchHillClimbing();
	}
	
}
