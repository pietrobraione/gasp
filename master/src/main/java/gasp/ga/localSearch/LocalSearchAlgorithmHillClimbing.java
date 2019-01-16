package gasp.ga.localSearch;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gasp.ga.Constraint;
import gasp.ga.Individual;
import gasp.se.*;
import gasp.utils.*;

public class LocalSearchAlgorithmHillClimbing extends LocalSearchAlgorithm { 
	
	private static final Logger logger = LogManager.getLogger(LocalSearchAlgorithmHillClimbing.class);

	@Override
	public Individual doLocalSearch(Individual individual) {
	
		int index = RandomNumberSupplier._I().nextInt(individual.size());
		logger.info("Local search starts at index " + index);		

		Individual retValue = individual;

		for (int remainingAttempts = Config.populationSize / 2; remainingAttempts > 0; remainingAttempts--) {			
			List<Constraint> currentConstraints = individual.getConstraintSetClone();

			Constraint mutatedConstraint = currentConstraints.get(index).not();
			
			currentConstraints.remove(index);
			currentConstraints.add(index, mutatedConstraint);
			
			Symex sym = Symex.makeEngine();
			List<Constraint> pc = sym.randomWalkSymbolicExecution(currentConstraints);
			int newFitness = sym.getInstructionCount();
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
