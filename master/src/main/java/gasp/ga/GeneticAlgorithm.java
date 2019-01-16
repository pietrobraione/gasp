package gasp.ga;

import java.nio.file.Path;
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
import gasp.se.Symex;
import gasp.se.SymexJBSE;
import gasp.utils.Utils;

public class GeneticAlgorithm {

	private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);

	private final int generations;
	private final int localSearchRate;
	private final int populationSize;
	private final int eliteSize;
	private final CrossoverFunction crossoverFunction;
	private final SelectionFunction selectionFunction;
	private final LocalSearchAlgorithm localSearchAlgorithm;
	private final List<Path> classpath;
	private final Path jbsePath;
	private final Path z3Path;	
	private final String methodClassName;
	private final String methodDescriptor;	
	private final String methodName;
	
	private List<Individual> population = new ArrayList<Individual>();
	private int currentIteration = 0;

	public GeneticAlgorithm(int generations, int localSearchRate, int populationSize, 
			                int eliteSize, CrossoverFunction crossoverFunction, 
			                SelectionFunction selectionFunction, LocalSearchAlgorithm localSearchAlgorithm, 
			                List<Path> classpath, Path jbsePath, Path z3Path, 
			                String methodClassName, String methodDescriptor, String methodName) {
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
		if (selectionFunction == null) {
			throw new IllegalArgumentException("Selection function cannot be null.");
		}
		if (localSearchAlgorithm == null) {
			throw new IllegalArgumentException("Local search algorithm cannot be null");
		}
		if (classpath == null) {
			throw new IllegalArgumentException("Classpath cannot be null");
		}
		if (jbsePath == null) {
			throw new IllegalArgumentException("JBSE path cannot be null");
		}
		if (z3Path == null) {
			throw new IllegalArgumentException("Z3 path cannot be null");
		}
		if (methodClassName == null) {
			throw new IllegalArgumentException("Method class name cannot be null");
		}
		if (methodDescriptor == null) {
			throw new IllegalArgumentException("Method descriptor cannot be null");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name cannot be null");
		}
		this.generations = generations;
		this.localSearchRate = localSearchRate;
		this.populationSize = populationSize;
		this.eliteSize = eliteSize;
		this.crossoverFunction = crossoverFunction;
		this.selectionFunction = selectionFunction;
		this.localSearchAlgorithm = localSearchAlgorithm;
		this.classpath = classpath;
		this.jbsePath = jbsePath;
		this.z3Path = z3Path;
		this.methodClassName = methodClassName;
		this.methodDescriptor = methodDescriptor;
		this.methodName = methodName;
	}
	
	public List<Individual> getBestSolutions(int n) {
		if (this.population == null || n > this.population.size()) {
			throw new IllegalArgumentException();
		}
		
		Collections.sort(this.population);
        return new ArrayList<>(this.population.subList(0, n));        
	}
	
	protected void generateInitialPopulation() {
		for (int i = 0; i < this.populationSize; ++i) {
			this.population.add(makeRandomIndividual());
		}
	}
	
	public void generateSolution() {
		logger.debug("Generating initial population");

		generateInitialPopulation();
		
		logger.debug(Utils.logIndividuals(this.population));

		while (!isFinished()) {
			this.currentIteration++;

			checkForDoingLocalSearch();
			
			evolve();
        	logger.debug("Generation " + this.currentIteration + " :");
			logger.debug(this.population.subList(0, this.eliteSize));
		}
	}

	protected void checkForDoingLocalSearch() {
		if (this.localSearchAlgorithm == null) {
			return;
		}
        if (this.currentIteration % this.localSearchRate != 0) {
        	return; //not this time
        }
        
    	logger.debug("Starting local search...");

    	Collections.sort(this.population);
        final Individual best = this.population.get(0);
        final Individual optimizedBest = this.localSearchAlgorithm.doLocalSearch(best);
        
        if (optimizedBest.getFitness() > best.getFitness()) {
        	logger.debug("...success with local search: " + optimizedBest);
        	
        	population.remove(0);
        	population.add(0, optimizedBest);
        } else {
        	logger.debug("...no success with local search");
        }
        
	}

	public boolean isFinished() {
		return this.currentIteration > this.generations;
	}

	protected void evolve() {
		final List<Individual> offsprings = breedNextGeneration();
        Collections.sort(offsprings);

        logger.debug("offsprings after crossover and mutation: ");
        logger.debug(Utils.logIndividuals(offsprings));
        
        this.population.addAll(offsprings);
        final List<Individual> elite = elitism();
        
        logger.info("best individual: " + elite.get(0));

        this.population = this.selectionFunction.survivalSelection(this.population, this.populationSize - elite.size());
        this.population.addAll(elite);

        logger.debug("generation summary: " + Utils.logFitnessStats(population));
		
	}

	protected List<Individual> elitism() {
        Collections.sort(this.population);
        final ArrayList<Individual> elite = new ArrayList<>(this.population.subList(0, this.eliteSize));
        this.population.subList(0, this.eliteSize).clear();
        return elite;
	}
	
	
	
	protected List<Individual> breedNextGeneration() {
        Collections.sort(this.population);
        
        final List<Individual> offsprings = new ArrayList<>();
        for (int i = 0; i < this.populationSize / 2; i++) {
        	final Individual[] children = doCrossoverOnPair();
        	if (children != null)  {
        		if (children.length > 0) {
        			offsprings.add(children[0]);
        		}
        		if (children.length > 1) {
        			offsprings.add(children[1]);
        		}
        	}
        }
        
        return offsprings;
	}
	
	protected Individual[] doCrossoverOnPair() {
		final Pair parents = this.selectionFunction.selectPairDistinct(this.population, true);
        logger.debug("selected parents: [" + parents.ind1.getFitness() + "], [" + parents.ind2.getFitness() + "]");
        
        try {
			final Individual[] children = this.crossoverFunction.crossover(parents.ind1, parents.ind2);
			return children;
		} catch (CrossoverException e) {
			return null;
		}
	}

	public Individual makeRandomIndividual() {
		final Symex se = new SymexJBSE(this.classpath, 
				                       this.jbsePath, 
				                       this.z3Path, 
				                       this.methodClassName, 
				                       this.methodDescriptor, 
				                       this.methodName);
		final List<Constraint> constraintSet = se.randomWalkSymbolicExecution();
		final int fitness = se.getInstructionCount();
		return new Individual(constraintSet, fitness);
	}
}
