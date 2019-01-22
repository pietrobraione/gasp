package gasp.ga.operators.selection;

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
	public int selectIndividual(List<Individual<T>> individuals) {
        final int[] ranking = getRanks(individuals);

        int rankSum = 0;
        for (int i = 0; i < ranking.length; ++i) {
        	rankSum += ranking[i];
        }
        
        final int pick = this.random.nextInt(rankSum);
        int current = 0;
        for (int i = ranking.length - 1; i >= 0; --i){
            current += ranking[i];
            if (current > pick) {
                return i;
            }	
        }
        
        throw new AssertionError("Reached an unreachable statement."); //should never happen
	}

	private int[] getRanks(List<Individual<T>> population) {
		final int[] ranking = new int[population.size()];
		int currRank = 0;
        int currFitness = Integer.MIN_VALUE;
        for (int i = population.size() - 1; i >= 0; --i) {
        	if (population.get(i).getFitness() > currFitness) {
        		currFitness = population.get(i).getFitness();
                currRank = population.size() - i;
        	}
        	ranking[i] = currRank;
        }
        return ranking;
	}
}
