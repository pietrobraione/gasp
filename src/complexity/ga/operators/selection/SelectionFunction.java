package complexity.ga.operators.selection;

import java.util.ArrayList;
import java.util.List;

import complexity.ga.Individual;

public abstract class SelectionFunction {
	
	protected abstract Individual selectIndividual(List<Individual> individuals);
	
	public ArrayList<Individual> selection(ArrayList<Individual> population, int nPairs) {
		ArrayList<Individual> result = new ArrayList<Individual>();
		ArrayList<Individual> populationCopy = new ArrayList<Individual>();
		for (int i = 0; i < population.size(); i++){
			populationCopy.add(population.get(i).cloneIndividual());
		}
		for (int i = 0; i < nPairs; i++){
		    Individual individual1 = selectIndividual(populationCopy);
		    populationCopy.remove(individual1);
		    Individual individual2 = selectIndividual(populationCopy);
		    populationCopy.remove(individual2);
		    result.add(individual1);
		    result.add(individual2);
		}
		return result;
	}

	public ArrayList<Individual> survivalSelection(ArrayList<Individual> population, int n) {
        ArrayList<Individual> result = new ArrayList<Individual>();
        for(int i = 0; i < n; i++) {
            Individual individual = selectIndividual(population);
            population.remove(individual);
            result.add(individual);
        }
        return result;
	}
	
}
