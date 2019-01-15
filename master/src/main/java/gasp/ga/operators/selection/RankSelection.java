package gasp.ga.operators.selection;

import java.util.Collections;
import java.util.List;

import gasp.ga.Individual;
import gasp.utils.RandomNumberSupplier;

public class RankSelection extends SelectionFunction {

	@Override
	protected int selectIndividual(List<Individual> individuals, boolean populationIsSorted) {
		if (!populationIsSorted) {
			Collections.sort(individuals);
		}
		
		Collections.reverse(individuals);
		
        int[] ranking = getRanks(individuals);

        int rankSum = 0;
        for(int i = 0; i < ranking.length; i++){
        	rankSum += ranking[i];
        }
        
        int pick = RandomNumberSupplier._I().nextInt(rankSum);
        int current = 0;
        for(int i = 0; i < ranking.length; i++){
            current += ranking[i];
            if(current > pick) {
                return i;
            }	
        }
        
        return ranking.length - 1; /*should neve happen */
	}

	private int[] getRanks(List<Individual> population) {
		int[] ranking = new int[population.size()];
        
		int currRank = 0;
        int currFitness = 0;
        for(int i = 0; i < population.size(); i++){
        
        	if (population.get(i).getFitness() > currFitness) {
        		currFitness = population.get(i).getFitness();
                currRank = i + 1;
        	}
            
        	ranking[i] = currRank;
        }
        
        return ranking;
	}

}
