package gasp.ga;

import static gasp.utils.Utils.logFitnessStats;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

public final class GeneticAlgorithm<T extends Gene<T>, U extends Individual<T>, R extends Model<T>> {
	private static final Logger logger = LogManager.getFormatterLogger(GeneticAlgorithm.class);

	private final Random random;
	private final IndividualGenerator<T, U> individualGenerator;
	private final ModelGenerator<T, U, R> modelGenerator;
	private final int numberOfThreads;
	private final int generations;
	private final Duration timeout;
	private final int localSearchRate;
	private final int populationSize;
	private final int eliteSize;
	private final CrossoverFunction<T> crossoverFunction;
	private final MutationFunction<T> mutationFunction;
	private final SelectionFunction<T, U> selectionFunction;
	private final LocalSearchAlgorithm<T, U> localSearchAlgorithm;
	
	private List<U> population = new ArrayList<>();
	private List<R> models = new ArrayList<>();
	private int currentGeneration = 0;
	private Instant start;

	public GeneticAlgorithm(long seed, int numberOfThreads, int generations, 
			                Duration timeout, int localSearchRate, int populationSize, int eliteSize, 
			                IndividualGenerator<T, U> individualGenerator, ModelGenerator<T, U, R> modelGenerator, 
							CrossoverFunction<T> crossoverFunction, MutationFunction<T> mutationFunction, 
							SelectionFunction<T, U> selectionFunction, LocalSearchAlgorithm<T, U> localSearchAlgorithm) {
		if (individualGenerator == null) {
			throw new IllegalArgumentException("The individual generator cannot be null.");
		}
		if (modelGenerator == null) {
			throw new IllegalArgumentException("The model generator cannot be null.");
		}
		if (numberOfThreads <= 0) {
			throw new IllegalArgumentException("Number of threads cannot be less or equal to 0.");
		}
		if (generations < 0) {
			throw new IllegalArgumentException("Number of generations cannot be less than 0.");
		}
		if (timeout == null) {
			throw new IllegalArgumentException("Timeout cannot be null.");
		}
		if (timeout.isNegative()) {
			throw new IllegalArgumentException("Timeout cannot be negative.");
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
		
		this.random = new Random(seed);
		this.individualGenerator = individualGenerator;
		this.modelGenerator = modelGenerator;
		this.numberOfThreads = numberOfThreads;
		this.generations = generations;
		this.timeout = timeout; 
		this.localSearchRate = localSearchRate;
		this.populationSize = populationSize;
		this.eliteSize = eliteSize;
		this.crossoverFunction = crossoverFunction;
		this.mutationFunction = mutationFunction;
		this.selectionFunction = selectionFunction;
		this.localSearchAlgorithm = localSearchAlgorithm;		
	}
	
	@SuppressWarnings("unchecked")
	public void evolve()  {
        try {
        	this.start = Instant.now();
    		generateInitialPopulation();
    		logCurrentGeneration();
        	while (!isFinished()) {
        		++this.currentGeneration;
        		possiblyDoLocalSearch();
        		produceNextGeneration();
        		logCurrentGeneration();
        	}
		} catch (FoundWorstIndividualException e) {
			this.population.set(0, (U) e.getIndividual());
		}
	}
	
	void generateInitialPopulation() throws FoundWorstIndividualException {
		final ExecutorService executor = Executors.newFixedThreadPool(this.numberOfThreads);
		final ExecutorCompletionService<U> completionService = new ExecutorCompletionService<>(executor);
		int generated = 0;
		while (generated < this.populationSize) {
			final int toGenerate = this.populationSize - generated;
			for (int i = 0; i < toGenerate; ++i) {
				completionService.submit(() -> this.individualGenerator.generateRandomIndividual(this.random.nextLong()));
			}
			for (int i = 0; i < toGenerate; ++i) {
				try {
					final Future<U> f = completionService.take();
					final U individual = f.get();
					if (individual != null) {
						this.population.add(individual);
						++generated;
					}
				} catch (ExecutionException e) {
					if (e.getCause() instanceof FoundWorstIndividualException) {
						throw (FoundWorstIndividualException) e.getCause();
					} else {
						throw new RuntimeException(e); //TODO report better
					}
				} catch (InterruptedException e) {
					//this should never happen
					throw new AssertionError("Unreachable code reached: thread interrupted.", e);
				}
			}
		}
        executor.shutdown();
		Collections.sort(this.population);
	}
	
	public List<U> getBestIndividuals(int n) {
		if (n < 0 || n > this.population.size()) {
			throw new IllegalArgumentException();
		}
		
		Collections.sort(this.population); //just to stay safe
        return new ArrayList<>(this.population.subList(0, n));        
	}
	
	public List<R> getModels(int n) {
		if (n < 0 || n > this.population.size()) {
			throw new IllegalArgumentException();
		}
		if (n > this.models.size()) {
			//generates the missing models
			for (int i = this.models.size(); i < n; ++i) {
		        logger.debug("Generating model for %d-%s population individual", i + 1, (i == 0 ? "st" : i == 1 ? "nd" : i == 3 ? "rd" : "th"));
				final R model = this.modelGenerator.generateModel(this.population.get(i));
				this.models.add(model);
			}
		}
		return new ArrayList<>(this.models.subList(0, n));
	}
	
	void logCurrentGeneration() {
		logger.debug("Generation %d", this.currentGeneration);
		logger.debug("Population:");
		logIndividuals(this.population);
        logger.info("Generation %d fitness summary: %s", this.currentGeneration, logFitnessStats(this.population));
	}
	
	void logIndividuals(List<U> individuals) {
		int id = 1;
		for (U ind : individuals) {
			final int idFinal = (id++);
			logger.debug("%d. %s", () -> idFinal, ind::toString);
		}
	}

	void possiblyDoLocalSearch() throws FoundWorstIndividualException {
		if (this.localSearchAlgorithm == null) {
			return;
		}
		
        if (this.currentGeneration % this.localSearchRate != 0) {
        	return; //not this time
        }
        
    	logger.debug("Performing local search");
    	
        final U best = this.population.get(0);
        final U optimizedBest = this.localSearchAlgorithm.doLocalSearch(this.random.nextLong(), best);
        
        if (optimizedBest.getFitness() > best.getFitness()) {
        	logger.debug("Local search found a better individual: %s", optimizedBest::toString);
        	
        	this.population.remove(0);
        	this.population.add(0, optimizedBest);
        } else {
        	logger.debug("Local search did not find a better individual");
        }
	}

	boolean isFinished() {
		return (this.generations > 0 && this.currentGeneration == this.generations) ||
		       (!this.timeout.isZero() && Instant.now().isAfter(this.start.plus(this.timeout)));
	}

	void produceNextGeneration() throws FoundWorstIndividualException {
		final List<U> offsprings = breedOffsprings();
        Collections.sort(offsprings);

        logger.debug("Offsprings:");
        logIndividuals(offsprings);
        
        this.population.addAll(offsprings);
        Collections.sort(this.population);

        final List<U> elite = elitism();
        
        logger.debug("Elite:");
        logIndividuals(elite);

        this.population = survivingIndividuals();
        this.population.addAll(elite);
        Collections.sort(this.population);
	}

	List<U> elitism() {
        final int actualEliteSize = (this.population.size() > this.eliteSize ? this.eliteSize : 
        	                         this.population.size() > 2 ? this.populationSize - 2 :
        	                         0);
        final ArrayList<U> elite = new ArrayList<>(this.population.subList(0, actualEliteSize));
        this.population.subList(0, actualEliteSize).clear();
        return elite;
	}
	
	List<U> breedOffsprings() throws FoundWorstIndividualException {
		final ExecutorService executor = Executors.newFixedThreadPool(this.numberOfThreads);
		final ExecutorCompletionService<List<U>> completionService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < this.populationSize / 2; ++i) {
        	completionService.submit(() -> this.generateOffspringsFromTwoParents(this.random.nextLong()));
        }
        executor.shutdown();
        final ArrayList<U> offsprings = new ArrayList<>();
        for (int i = 0; i < this.populationSize / 2; ++i) {
        	try {
            	final Future<List<U>> f = completionService.take();
				offsprings.addAll(f.get());
			} catch (ExecutionException e) {
				if (e.getCause() instanceof FoundWorstIndividualException) {
					throw (FoundWorstIndividualException) e.getCause();
				} else {
					throw new RuntimeException(e); //TODO report better
				}
			} catch (InterruptedException e) {
				//this should never happen
				throw new AssertionError("Unreachable code reached: thread interrupted.", e);
			}
        }        
        return offsprings;
	}
	
	List<U> generateOffspringsFromTwoParents(long seed) throws FoundWorstIndividualException {
        final ArrayList<U> retVal = new ArrayList<>();
        
        final Random random = new Random(seed);
        
        //selection
		final List<Integer> parents = this.selectionFunction.select(random.nextLong(), this.population, 2);
        logger.debug("Selected parents: %d, %d", (parents.get(0) + 1), (parents.get(1) + 1));
        
        //crossover
		final U individual1 = this.population.get(parents.get(0));
		final U individual2 = this.population.get(parents.get(1));
        final Pair<List<T>> chromosomesCrossover = this.crossoverFunction.doCrossover(random.nextLong(), individual1.getChromosome(), individual2.getChromosome());
        if (chromosomesCrossover == null) {
        	//too short chromosomes for the crossover function: no offsprings
        	return retVal;
        }
        
        //mutation
        final List<T> chromosomeCrossoverMutation1 = this.mutationFunction.mutate(random.nextLong(), chromosomesCrossover.first);
        final List<T> chromosomeCrossoverMutation2 = this.mutationFunction.mutate(random.nextLong(), chromosomesCrossover.second);
        
        //generation
        final U offspring1 = this.individualGenerator.generateRandomIndividual(random.nextLong(), chromosomeCrossoverMutation1);
        final U offspring2 = this.individualGenerator.generateRandomIndividual(random.nextLong(), chromosomeCrossoverMutation2);

        if (offspring1 != null) {
        	retVal.add(offspring1);
        }
        if (offspring2 != null) {
        	retVal.add(offspring2);
        }
        return retVal;
	}
	
	List<U> survivingIndividuals() {
        final List<Integer> indices = this.selectionFunction.select(this.random.nextLong(), this.population, this.populationSize - this.eliteSize);
        final List<U> oldPopulation = this.population;
        final List<U> retVal = new ArrayList<>();
        for (int index : indices) {
        	retVal.add(oldPopulation.get(index));
        }
        return retVal;
	}
}
