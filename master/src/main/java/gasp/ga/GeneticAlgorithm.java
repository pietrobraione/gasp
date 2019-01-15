package gasp.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gasp.ga.localSearch.LocalSearchAlgorithm;
import gasp.ga.operators.crossover.CrossoverException;
import gasp.ga.operators.crossover.CrossoverFunction;
import gasp.ga.operators.selection.SelectionFunction;
import gasp.ga.operators.selection.SelectionFunction.Pair;
import gasp.utils.Config;
import gasp.utils.Utils;

public class GeneticAlgorithm {

	private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);

	private final SelectionFunction selectionFunction;
	private final CrossoverFunction crossoverFunction;
	private final LocalSearchAlgorithm localSearchAlgorithm;
	
	private List<Individual> population = new ArrayList<Individual>();
	private int currentIteration = 0;

	public GeneticAlgorithm(SelectionFunction selectionFunction, CrossoverFunction crossoverFunction, LocalSearchAlgorithm localSearchAlgorithm) {
		if (selectionFunction == null) {
			throw new IllegalArgumentException("Selection function cannot be null");
		} else {
			this.selectionFunction = selectionFunction;
		}
		
		if (crossoverFunction == null) {
			throw new IllegalArgumentException("Crossover function cannot be null");
		} else {
			this.crossoverFunction = crossoverFunction;
		}
		
		if (localSearchAlgorithm == null) {
			throw new IllegalArgumentException("Local search algorithm cannot be null");
		} else {
			this.localSearchAlgorithm = localSearchAlgorithm;
		}
	}
	
	/**
	 * Constructor to be used only for testing
	 * 
	 * @param population a {@link List}{@code <}{@link Individual}{@code >}. Must not be null.
	 */
	GeneticAlgorithm(List<Individual> population) {
		this.selectionFunction = null;
		this.crossoverFunction = null;
		this.localSearchAlgorithm = null;
		this.population.addAll(population);
	}

	public List<Individual> getBestSolutions(int n) {
		if (population == null || n > population.size()) throw new IllegalArgumentException();
		
		Collections.sort(population);

        return new ArrayList<>(population.subList(0, n));        
	}
	
	protected void generateInitialPopulation() {
		for (int i = 0; i < Config.populationSize; i++) {
			population.add(Individual.makeRandomIndividual());
		}
	}
	
	public void generateSolution() {
		logger.debug("Generating initial population");

		generateInitialPopulation();
		
		logger.debug(Utils.logIndividuals(population));

		while (!isFinished()) {
			currentIteration++;

			checkForDoingLocalSearch();
			
			evolve();
        	logger.debug("Generation " + currentIteration + " :");
			logger.debug(population.subList(0, Config.eliteSize));
		}
	}

	protected void checkForDoingLocalSearch() {
		if (localSearchAlgorithm == null) {
			return;
		}
		
        if(currentIteration % Config.localSearchRate != 0) {
        	return; //not this time
        }
        
    	logger.debug("Starting local search...");

    	Collections.sort(population);
        Individual best = population.get(0);

        Individual optimizedBest = localSearchAlgorithm.doLocalSearch(best);
        
        if (optimizedBest.getFitness() > best.getFitness()) {
        	logger.debug("... success with local search:");
        	population.remove(0);
        	population.add(0, optimizedBest);
        	logger.debug("... success with local search: " + optimizedBest);
        }
        
	}

	public boolean isFinished() {
		return currentIteration > Config.generations;
	}

	protected void evolve() {
		List<Individual> offspring = breedNextGeneration();
        Collections.sort(offspring);

        logger.debug("offspring after crossover and mutation: ");
        Collections.sort(offspring);
        logger.debug(Utils.logIndividuals(offspring));
        
        population.addAll(offspring);

        List<Individual> elite = elitism();
        logger.info("best: " + elite.get(0));

        population = selectionFunction.survivalSelection(population, Config.populationSize - elite.size());
        population.addAll(elite);


        logger.debug("generation summary: " + Utils.logFitnessStats(population));
		
	}

	protected List<Individual> elitism() {
        Collections.sort(population);

        ArrayList<Individual> elite = new ArrayList<>(population.subList(0, Config.eliteSize));
        
        population.subList(0, Config.eliteSize).clear();
        
        return elite;
	}
	
	
	
	protected List<Individual> breedNextGeneration() {
        Collections.sort(population);
        
        List<Individual> offspring = new ArrayList<>();
        for (int i = 0; i < Config.populationSize / 2; i++) {
        	Individual[] children = doCrossoverOnPair();
        	if (children != null)  {
        		if (children.length > 0) {
        			offspring.add(children[0]);
        		}
        		if (children.length > 1) {
        			offspring.add(children[1]);
        		}
        	}
        }
        
        return offspring;
	}
	
	protected Individual[] doCrossoverOnPair() {
        //SELECTION 
        Pair parents = selectionFunction.selectPairDistinct(population, true);
        logger.debug("* [" + parents.ind1.getFitness() + "] - [" + parents.ind2.getFitness() + "]");
        
        //CROSSOVER
        try {
			Individual[] offspring = crossoverFunction.crossover(parents.ind1, parents.ind2);
			return offspring;
		} catch (CrossoverException e) {
			return null;
		}
	}
	

}
