package gasp.ga.operators.selection;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.Individual;

public class SelectionFunctionRank<T extends Gene<T>> implements SelectionFunction<T> {
	private final Random random;
	
	public SelectionFunctionRank(Random random) {
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		
		this.random = random;
	}

	@Override
	public int selectIndividual(List<Individual<T>> individuals, boolean populationIsSorted) {
		if (!populationIsSorted) {
			Collections.sort(individuals);
		}
		
		Collections.reverse(individuals);
		
        final int[] ranking = getRanks(individuals);

        int rankSum = 0;
        for (int i = 0; i < ranking.length; ++i){
        	rankSum += ranking[i];
        }
        
        final int pick = this.random.nextInt(rankSum);
        int current = 0;
        for (int i = 0; i < ranking.length; ++i){
            current += ranking[i];
            if (current > pick) {
                return i;
            }	
        }
        
        return ranking.length - 1; //should never happen
	}

	private int[] getRanks(List<Individual<T>> population) {
		int[] ranking = new int[population.size()];
		int currRank = 0;
        int currFitness = 0;
        for (int i = 0; i < population.size(); ++i){
        	if (population.get(i).getFitness() > currFitness) {
        		currFitness = population.get(i).getFitness();
                currRank = i + 1;
        	}
        	ranking[i] = currRank;
        }
        return ranking;
	}

}
