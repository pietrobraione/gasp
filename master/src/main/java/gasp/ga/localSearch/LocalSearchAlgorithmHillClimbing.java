package gasp.ga.localSearch;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gasp.ga.Gene;
import gasp.ga.IndividualGenerator;
import gasp.ga.Individual;

public final class LocalSearchAlgorithmHillClimbing<T extends Gene<T>> implements LocalSearchAlgorithm<T> { 
	private static final Logger logger = LogManager.getLogger(LocalSearchAlgorithmHillClimbing.class);

	private final IndividualGenerator<T> individualGenerator;
	private final int populationSize;
	private final Random random;
	
	public LocalSearchAlgorithmHillClimbing(IndividualGenerator<T> individualGenerator, int populationSize, Random random) {
		if (individualGenerator == null) {
			throw new IllegalArgumentException("The individual generator cannot be null.");
		}
		if (populationSize <= 0) {
			throw new IllegalArgumentException("Population size cannot be less or equal to 0.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}

		this.individualGenerator = individualGenerator;
		this.populationSize = populationSize;
		this.random = random;
	}
	
	@Override
	public Individual<T> doLocalSearch(Individual<T> individual) {
		Individual<T> retValue = individual;
		int index = this.random.nextInt(retValue.size());
		int remainingAttempts = retValue.size();
		while (remainingAttempts > 0) {			
			final List<T> currentChromosome = retValue.getChromosome();
			final T mutatedGene = currentChromosome.get(index).not();
			currentChromosome.remove(index);
			currentChromosome.add(index, mutatedGene);
			
			Individual<T> newIndividual = this.individualGenerator.generateRandomIndividual(currentChromosome);
			if (newIndividual == null) {
				logger.info("Local search at index " + index + ": no individual generated");
				--remainingAttempts;
			} else {
				boolean found = false;
				int remainingAttemptsIndividual = this.populationSize / 2;
				while (true) {
					if (newIndividual.getFitness() > retValue.getFitness()) {
						found = true;
						break;
					}
					--remainingAttemptsIndividual;
					if (remainingAttemptsIndividual == 0) {
						break;
					}
					newIndividual = this.individualGenerator.generateRandomIndividual(currentChromosome);
				}
				if (found) {
					logger.info("Local search at index " + index + ": " + retValue.getFitness() + " --> " + newIndividual.getFitness() + " ** Successful");
					retValue = newIndividual;
				} else {
					logger.info("Local search at index " + index + ": " + retValue.getFitness() + " --> " + newIndividual.getFitness() + " ** Unsuccessful");
					--remainingAttempts;
				}
			}
			
            index = (index + 1) % retValue.size();
		}
		
		return retValue;
	}
}
