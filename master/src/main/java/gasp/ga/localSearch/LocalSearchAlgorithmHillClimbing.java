package gasp.ga.localSearch;

import java.nio.file.Path;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gasp.ga.Constraint;
import gasp.ga.Individual;
import gasp.se.Symex;
import gasp.se.SymexJBSE;

public class LocalSearchAlgorithmHillClimbing implements LocalSearchAlgorithm { 
	private static final Logger logger = LogManager.getLogger(LocalSearchAlgorithmHillClimbing.class);

	private final int populationSize;
	private final Random random;
	private final List<Path> classpath;
	private final Path jbsePath;
	private final Path z3Path;	
	private final String methodClassName;
	private final String methodDescriptor;	
	private final String methodName;
	
	public LocalSearchAlgorithmHillClimbing(int populationSize, Random random, List<Path> classpath, Path jbsePath, 
                                            Path z3Path, String methodClassName, String methodDescriptor, 
                                            String methodName) {
		if (populationSize <= 0) {
			throw new IllegalArgumentException("Population size cannot be less or equal to 0.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		if (classpath == null) {
			throw new IllegalArgumentException("Classpath cannot be null.");
		}
		if (jbsePath == null) {
			throw new IllegalArgumentException("JBSE path cannot be null.");
		}
		if (z3Path == null) {
			throw new IllegalArgumentException("Z3 path cannot be null.");
		}
		if (methodClassName == null) {
			throw new IllegalArgumentException("Method class name cannot be null.");
		}
		if (methodDescriptor == null) {
			throw new IllegalArgumentException("Method descriptor cannot be null.");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name cannot be null.");
		}

		this.populationSize = populationSize;
		this.random = random;
		this.classpath = classpath;
		this.jbsePath = jbsePath;
		this.z3Path = z3Path;
		this.methodClassName = methodClassName;
		this.methodDescriptor = methodDescriptor;
		this.methodName = methodName;
	}
	
	@Override
	public Individual doLocalSearch(Individual individual) {
	
		int index = this.random.nextInt(individual.size());
		logger.info("Local search starts at index " + index);		

		Individual retValue = individual;

		for (int remainingAttempts = this.populationSize / 2; remainingAttempts > 0; --remainingAttempts) {			
			final List<Constraint> currentConstraints = individual.getConstraintSetClone();
			final Constraint mutatedConstraint = currentConstraints.get(index).not();
			currentConstraints.remove(index);
			currentConstraints.add(index, mutatedConstraint);
			
			final Symex se = new SymexJBSE(this.classpath, 
                                           this.jbsePath, 
                                           this.z3Path, 
                                           this.methodClassName, 
                                           this.methodDescriptor, 
                                           this.methodName);
			final List<Constraint> pc = se.randomWalkSymbolicExecution(currentConstraints);
			final int newFitness = se.getInstructionCount();
			
			logger.info("Local search at index " + index + ": " + individual.getFitness()+ " --> " + newFitness);

			if (newFitness >= individual.getFitness()) {
				if (newFitness > individual.getFitness()) {
					logger.info(" ** Successful");
				}

				retValue = new Individual(pc, newFitness);
			}

            index = (index + 1) % retValue.size();
		}
		
		return retValue;
	}
}
