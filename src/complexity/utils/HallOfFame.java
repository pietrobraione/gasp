package complexity.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import complexity.ga.Individual;

public class HallOfFame {
	
	public int n;
	public List<Individual> bestIndividuals = new ArrayList<>();
	
	public HallOfFame(int n) {
		super();
		this.n = n;
	}
	
	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public List<Individual> getBestIndividuals() {
		return bestIndividuals;
	}

	public void setBestIndividuals(ArrayList<Individual> bestIndividuals) {
		this.bestIndividuals = bestIndividuals;
	}

	public void update(List<Individual> population) {
		bestIndividuals = population;
		Collections.sort(bestIndividuals, new SortIndividuals());
	}
}
