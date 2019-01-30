package gasp.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	private final int numberOfThreads;
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

	public GeneticAlgorithm(IndividualGenerator<T> individualGenerator, int numberOfThreads, 
							int generations, int localSearchRate, int populationSize, int eliteSize, 
							CrossoverFunction<T> crossoverFunction, MutationFunction<T> mutationFunction, 
							SelectionFunction<T> selectionFunction, LocalSearchAlgorithm<T> localSearchAlgorithm) {
		if (individualGenerator == null) {
			throw new IllegalArgumentException("The individual generator cannot be null.");
		}
		if (numberOfThreads <= 0) {
			throw new IllegalArgumentException("Number of threads cannot be less or equal to 0.");
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
		
		this.individualGenerator = individualGenerator;
		this.numberOfThreads = numberOfThreads;
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
		int generated = 0;
		while (generated < this.populationSize) {
			Individual<T> individual = this.individualGenerator.generateRandomIndividual();
			if (individual != null) {
				this.population.add(individual);
				++generated;
			}
		}
		Collections.sort(this.population);
	}
	
	public void evolve() {
		logger.debug("Generation " + this.currentGeneration + ":");
		logIndividuals(this.population);
        logger.debug("Generation fitness summary: " + Utils.logFitnessStats(this.population));

		while (!isFinished()) {
			++this.currentGeneration;
			possiblyDoLocalSearch();
			produceNextGeneration();
			
        	logger.debug("Generation " + this.currentGeneration + ":");
        	logIndividuals(this.population);
            logger.debug("Generation fitness summary: " + Utils.logFitnessStats(this.population));
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

	void produceNextGeneration() {
		final List<Individual<T>> offsprings = breedOffsprings();
        Collections.sort(offsprings);

        logger.debug("Offsprings:");
        logIndividuals(offsprings);
        
        this.population.addAll(offsprings);
        Collections.sort(this.population);

        final List<Individual<T>> elite = elitism();
        
        logger.debug("Elite:");
        logIndividuals(elite);

        this.population = survivingIndividuals();
        this.population.addAll(elite);
        Collections.sort(this.population);
	}

	List<Individual<T>> elitism() {
        final ArrayList<Individual<T>> elite = new ArrayList<>(this.population.subList(0, this.eliteSize));
        final int actualEliteSize = (this.population.size() > this.eliteSize ? eliteSize : 
        	                         this.population.size() > 2 ? this.populationSize - 2 :
        	                         0);
        this.population.subList(0, actualEliteSize).clear();
        return elite;
	}
	
	List<Individual<T>> breedOffsprings() {
		final ExecutorService executor = Executors.newFixedThreadPool(this.numberOfThreads);
		final ExecutorCompletionService<List<Individual<T>>> completionService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < this.populationSize / 2; ++i) {
        	completionService.submit(this::generateOffspringsFromTwoParents);
        }
        executor.shutdown();
        final ArrayList<Individual<T>> offsprings = new ArrayList<>();
        for (int i = 0; i < this.populationSize / 2; ++i) {
        	try {
            	final Future<List<Individual<T>>> f = completionService.take();
				offsprings.addAll(f.get());
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
        }        
        return offsprings;
	}
	
	List<Individual<T>> generateOffspringsFromTwoParents() {
        final ArrayList<Individual<T>> retVal = new ArrayList<>();
        
        //selection
		final List<Integer> parents = this.selectionFunction.select(this.population, 2);
        logger.debug("Selected parents: " + (parents.get(0) + 1) + ", " + (parents.get(1) + 1));
        
        //crossover
		final Individual<T> individual1 = this.population.get(parents.get(0));
		final Individual<T> individual2 = this.population.get(parents.get(1));
        final Pair<List<T>> chromosomesCrossover = this.crossoverFunction.doCrossover(individual1.getChromosome(), individual2.getChromosome());
        if (chromosomesCrossover == null) {
        	//too short chromosomes for the crossover function: no offsprings
        	return retVal;
        }
        
        //mutation
        final List<T> chromosomeCrossoverMutation1 = this.mutationFunction.mutate(chromosomesCrossover.first);
        final List<T> chromosomeCrossoverMutation2 = this.mutationFunction.mutate(chromosomesCrossover.second);
        
        //generation
        final Individual<T> offspring1 = this.individualGenerator.generateRandomIndividual(chromosomeCrossoverMutation1);
        final Individual<T> offspring2 = this.individualGenerator.generateRandomIndividual(chromosomeCrossoverMutation2);

        if (offspring1 != null) {
        	retVal.add(offspring1);
        }
        if (offspring2 != null) {
        	retVal.add(offspring2);
        }
        return retVal;
	}
	
	List<Individual<T>> survivingIndividuals() {
        final List<Integer> indices = this.selectionFunction.select(this.population, this.populationSize - this.eliteSize);
        final List<Individual<T>> oldPopulation = this.population;
        final List<Individual<T>> retVal = new ArrayList<>();
        for (int index : indices) {
        	retVal.add(oldPopulation.get(index));
        }
        return retVal;
	}
}
