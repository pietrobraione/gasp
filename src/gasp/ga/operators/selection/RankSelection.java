package gasp.ga.operators.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gasp.ga.Individual;
import gasp.utils.RandomSingleton;
import gasp.utils.SortIndividuals;

public class RankSelection extends SelectionFunction {

	private List<Integer> rankSelection(List<Individual> population) {
		List<Integer> ranking = new ArrayList<Integer>();
        int currRank = 0;
        int currFitness = 0;
        for(int i = 0; i < population.size(); i++){
            if (population.get(i).getFitness() > currFitness) {
                currFitness = population.get(i).getFitness();
                currRank = i + 1;
                }
            ranking.add(currRank);
            }
        return ranking;
	}
	
	@Override
	protected Individual selectIndividual(List<Individual> individuals) {
		Collections.sort(individuals, new SortIndividuals());
		Collections.reverse(individuals);
        List<Integer> ranking = rankSelection(individuals);
        int rankSum = 0;
        for(int i = 0; i < ranking.size(); i++){
        	rankSum += ranking.get(i);
        }
        int pick = RandomSingleton.getInstance().nextInt(rankSum);
        int current = 0;
        int choosen = 0;
        for(int i = 0; i < ranking.size(); i++){
            current += ranking.get(i);
            if(current > pick) {
                choosen = i;
            }	
        }
        return individuals.get(choosen);
	}


}
