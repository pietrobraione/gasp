package gasp.ga.localSearch;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gasp.ga.FoundWorstIndividualException;
import gasp.ga.Gene;
import gasp.ga.IndividualGenerator;
import gasp.ga.Individual;

public final class LocalSearchAlgorithmHillClimbing<T extends Gene<T>, U extends Individual<T>> implements LocalSearchAlgorithm<T, U> { 
	private static final Logger logger = LogManager.getLogger(LocalSearchAlgorithmHillClimbing.class);

	private final IndividualGenerator<T, U> individualGenerator;
	private final int populationSize;
	private final int attempts;
	private final Random random;
	
	public LocalSearchAlgorithmHillClimbing(IndividualGenerator<T, U> individualGenerator, int populationSize, int attempts, Random random) {
		if (individualGenerator == null) {
			throw new IllegalArgumentException("The individual generator cannot be null.");
		}
		if (populationSize <= 0) {
			throw new IllegalArgumentException("Population size cannot be less or equal to 0.");
		}
		if (attempts <= 0) {
			throw new IllegalArgumentException("Attempts cannot be less or equal to 0.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}

		this.individualGenerator = individualGenerator;
		this.populationSize = populationSize;
		this.attempts = attempts;
		this.random = random;
	}
	
	@Override
	public U doLocalSearch(U individual) throws FoundWorstIndividualException {
		U retValue = individual;
		int index = this.random.nextInt(retValue.size());
		int remainingAttempts = Math.min(retValue.size(), this.attempts);
		
    	logger.info("Performing local search, number of attempts: " + remainingAttempts);

		while (remainingAttempts > 0) {			
			final List<T> currentChromosome = retValue.getChromosome();
			final T mutatedGene = currentChromosome.get(index).not();
			currentChromosome.remove(index);
			currentChromosome.add(index, mutatedGene);
			
			U newIndividual = this.individualGenerator.generateRandomIndividual(currentChromosome);
			boolean found = false;
			int remainingAttemptsIndividual = Math.max(1, this.populationSize * 10 / ((int) retValue.getFitness()));
			while (true) {
				if (newIndividual != null && newIndividual.getFitness() > retValue.getFitness()) {
					found = true;
					break;
				}
				if (remainingAttemptsIndividual == 0) {
					break;
				}
				--remainingAttemptsIndividual;
				newIndividual = this.individualGenerator.generateRandomIndividual(currentChromosome);
			}
			if (found) {
				logger.debug("Local search at index " + index + ": " + retValue.getFitness() + " --> " + newIndividual.getFitness() + " ** Successful");
				retValue = newIndividual;
			} else if (newIndividual == null) {
				logger.debug("Local search at index " + index + ": no individual ** Unsuccessful");
			} else {
				logger.debug("Local search at index " + index + ": " + retValue.getFitness() + " --> " + newIndividual.getFitness() + " ** Unsuccessful");
			}
			--remainingAttempts;
			
            index = (index + 1) % retValue.size();
		}
		
		return retValue;
	}
}
