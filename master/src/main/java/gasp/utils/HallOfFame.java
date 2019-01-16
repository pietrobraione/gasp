package gasp.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gasp.ga.Individual;

public class HallOfFame {
	private final int eliteSize;
	
	public HallOfFame(int eliteSize) {
		if (eliteSize <= 0) { //TODO can eliteSize be == 0?
			throw new IllegalArgumentException("Elite size cannot be less or equal to 0.");
		}
		
		this.eliteSize = eliteSize;
	}
	
	private List<Individual> bestIndividuals = new ArrayList<>();
	
	public List<Individual> getBestIndividuals() {
		return this.bestIndividuals;
	}

	public void setBestIndividuals(ArrayList<Individual> bestIndividuals) {
		this.bestIndividuals = bestIndividuals;
	}

	public void update(List<Individual> population) {
		this.bestIndividuals.addAll(population);
		Collections.sort(this.bestIndividuals);
		this.bestIndividuals = new ArrayList<>(this.bestIndividuals.subList(0, this.eliteSize));
	}
}
