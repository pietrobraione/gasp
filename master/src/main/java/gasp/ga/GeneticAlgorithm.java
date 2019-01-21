package gasp.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gasp.ga.localSearch.LocalSearchAlgorithm;
import gasp.ga.operators.crossover.CrossoverFunction;
import gasp.ga.operators.mutation.MutationFunction;
import gasp.ga.operators.selection.SelectionFunction;
import gasp.utils.Utils;

public final class GeneticAlgorithm<T extends Gene<T>> {
	private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);

	private final IndividualGenerator<T> individualGenerator;
	private final int generations;
	private final int localSearchRate;
	private final int populationSize;
	private final int eliteSize;
	private final CrossoverFunction<T> crossoverFunction;
	private final MutationFunction<T> mutationFunction;
	private final SelectionFunction<T> selectionFunction;
	private final LocalSearchAlgorithm<T> localSearchAlgorithm;
	
	private List<Individual<T>> population = new ArrayList<>();
	private int currentGeneration = 0;

	public GeneticAlgorithm(IndividualGenerator<T> constraintManager, int generations, 
							int localSearchRate, int populationSize, int eliteSize, 
							CrossoverFunction<T> crossoverFunction, MutationFunction<T> mutationFunction, 
							SelectionFunction<T> selectionFunction, LocalSearchAlgorithm<T> localSearchAlgorithm) {
		if (constraintManager == null) {
			throw new IllegalArgumentException("Constraint manager cannot be null.");
		}
		if (generations <= 0) {
			throw new IllegalArgumentException("Number of generations cannot be less or equal to 0.");
		}
		if (localSearchRate <= 0) {
			throw new IllegalArgumentException("Local search rate cannot be less or equal to 0.");
		}
		if (populationSize <= 0) {
			throw new IllegalArgumentException("Population size cannot be less or equal to 0.");
		}
		if (eliteSize <= 0) {
			throw new IllegalArgumentException("Elite size cannot be less or equal to 0.");
		}
		if (eliteSize > populationSize) {
			throw new IllegalArgumentException("Elite size cannot be greater than population size.");
		}
		if (crossoverFunction == null) {
			throw new IllegalArgumentException("Crossover function cannot be null.");
		}
		if (mutationFunction == null) {
			throw new IllegalArgumentException("Mutation function cannot be null.");
		}
		if (selectionFunction == null) {
			throw new IllegalArgumentException("Selection function cannot be null.");
		}
		if (localSearchAlgorithm == null) {
			throw new IllegalArgumentException("Local search algorithm cannot be null.");
		}
		
		this.individualGenerator = constraintManager;
		this.generations = generations;
		this.localSearchRate = localSearchRate;
		this.populationSize = populationSize;
		this.eliteSize = eliteSize;
		this.crossoverFunction = crossoverFunction;
		this.mutationFunction = mutationFunction;
		this.selectionFunction = selectionFunction;
		this.localSearchAlgorithm = localSearchAlgorithm;
		
		generateInitialPopulation();
	}
	
	void generateInitialPopulation() {
		for (int i = 0; i < this.populationSize; ++i) {
			this.population.add(this.individualGenerator.generateRandomIndividual());
		}
	}
	
	public void evolve() {
		logger.debug("Generation " + this.currentGeneration + ":");
		logIndividuals(this.population);

		while (!isFinished()) {
			++this.currentGeneration;
			possiblyDoLocalSearch();
			deriveNextGeneration();
			
        	logger.debug("Generation " + this.currentGeneration + ":");
        	logIndividuals(this.population);
		}
	}
	
	public List<Individual<T>> getBestIndividuals(int n) {
		if (n < 0 || n > this.population.size()) {
			throw new IllegalArgumentException();
		}
		
		Collections.sort(this.population);
        return new ArrayList<>(this.population.subList(0, n));        
	}
	
	void logIndividuals(List<Individual<T>> individuals) {
		int id = 1;
		for (Individual<?> ind : individuals) {
			logger.debug("" + (id++) + ". "+ ind);
		}
	}

	void possiblyDoLocalSearch() {
		if (this.localSearchAlgorithm == null) {
			return;
		}
		
        if (this.currentGeneration % this.localSearchRate != 0) {
        	return; //not this time
        }
        
    	logger.debug("Performing local search");

    	Collections.sort(this.population);
        final Individual<T> best = this.population.get(0);
        final Individual<T> optimizedBest = this.localSearchAlgorithm.doLocalSearch(best);
        
        if (optimizedBest.getFitness() > best.getFitness()) {
        	logger.debug("Local search found a better individual: " + optimizedBest);
        	
        	this.population.remove(0);
        	this.population.add(0, optimizedBest);
        } else {
        	logger.debug("Local search did not find a better individual");
        }
	}

	boolean isFinished() {
		return this.currentGeneration > this.generations;
	}

	void deriveNextGeneration() {
		final List<Individual<T>> offsprings = breedOffsprings();
        Collections.sort(offsprings);
        this.population.addAll(offsprings);

        logger.debug("Offsprings after crossover and mutation:");
        logIndividuals(offsprings);
        
        final List<Individual<T>> elite = elitism();
        
        logger.debug("Best individual: " + elite.get(0));

        this.population = this.selectionFunction.survivalSelection(this.population, this.populationSize - elite.size());
        this.population.addAll(elite);

        logger.debug("Generation fitness summary: " + Utils.logFitnessStats(this.population));
		
	}

	List<Individual<T>> elitism() {
        Collections.sort(this.population);
        final ArrayList<Individual<T>> elite = new ArrayList<>(this.population.subList(0, this.eliteSize));
        this.population.subList(0, this.eliteSize).clear();
        return elite;
	}
	
	List<Individual<T>> breedOffsprings() {
        Collections.sort(this.population);
        
        final List<Individual<T>> offsprings = new ArrayList<>();
        for (int i = 0; i < this.populationSize / 2; ++i) {
        	offsprings.addAll(doCrossoverOnPair());
        }
        
        return offsprings;
	}
	
	List<Individual<T>> doCrossoverOnPair() {
		final Pair<Individual<T>> parents = this.selectionFunction.selectPairDistinct(this.population, true);
        logger.debug("Selected parents: [" + parents.ind1.getFitness() + "], [" + parents.ind2.getFitness() + "]");
        
        final Pair<List<T>> chromosomesCrossover = this.crossoverFunction.crossover(parents.ind1.getChromosome(), parents.ind2.getChromosome());
        final List<T> chromosomeCombinedAndMutated1 = this.mutationFunction.mutate(chromosomesCrossover.ind1);
        final Individual<T> child1 = this.individualGenerator.generateRandomIndividual(chromosomeCombinedAndMutated1);
        final List<T> chromosomeCombinedAndMutated2 = this.mutationFunction.mutate(chromosomesCrossover.ind2);
        final Individual<T> child2 = this.individualGenerator.generateRandomIndividual(chromosomeCombinedAndMutated2);

        final ArrayList<Individual<T>> retVal = new ArrayList<>();
        if (child1 != null) {
        	retVal.add(child1);
        }
        if (child2 != null) {
        	retVal.add(child2);
        }
        return retVal;
	}
}
