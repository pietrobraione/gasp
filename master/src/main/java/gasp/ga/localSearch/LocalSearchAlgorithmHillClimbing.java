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
		int index = this.random.nextInt(individual.size());
		
		logger.info("Local search starts at index " + index);
		
		Individual<T> retValue = individual;
		for (int remainingAttempts = this.populationSize / 2; remainingAttempts > 0; --remainingAttempts) {			
			final List<T> currentChromosome = individual.getChromosome();
			final T mutatedGene = currentChromosome.get(index).not();
			currentChromosome.remove(index);
			currentChromosome.add(index, mutatedGene);
			
			final Individual<T> newIndividual = this.individualGenerator.generateRandomIndividual(currentChromosome);
			
			if (newIndividual == null) {
				//unsuccessful mutation
				continue;
			}
			
			logger.info("Local search at index " + index + ": " + retValue.getFitness() + " --> " + newIndividual.getFitness());

			if (newIndividual.getFitness() > retValue.getFitness()) {
				logger.info(" ** Successful");
				retValue = newIndividual;
			}

            index = (index + 1) % retValue.size();
		}
		
		return retValue;
	}
}
