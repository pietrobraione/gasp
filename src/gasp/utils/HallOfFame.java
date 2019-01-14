package gasp.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gasp.ga.Individual;

public class HallOfFame {
	
	private List<Individual> bestIndividuals = new ArrayList<>();
	
	public List<Individual> getBestIndividuals() {
		return bestIndividuals;
	}

	public void setBestIndividuals(ArrayList<Individual> bestIndividuals) {
		this.bestIndividuals = bestIndividuals;
	}

	public void update(List<Individual> population) {
		bestIndividuals.addAll(population);
		Collections.sort(bestIndividuals);
		bestIndividuals = new ArrayList<>(bestIndividuals.subList(0, Config.eliteSize));
	}
}
