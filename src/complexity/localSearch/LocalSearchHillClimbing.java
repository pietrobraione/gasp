package complexity.localSearch;

import java.util.*;

import complexity.ga.Individual;
import complexity.se.*;
import complexity.utils.*;

public class LocalSearchHillClimbing extends LocalSearchAlgorithm { 
	
	@Override
	public Individual localSearch(Individual individual) {
		int index = RandomSingleton.getInstance().nextInt(individual.getConstraintSet().size() - 1) + 1;
		int remainingAttempts = Config.populationSize / 2;
		System.out.println("Local search starts at index " + index);		
		while(remainingAttempts > 0) {
			
			remainingAttempts --;
			List<Constraint> currentConstraints = individual.getConstraintSet();
			Constraint mutatedConstraint = currentConstraints.get(index).mkNot();
			currentConstraints.remove(index);
			currentConstraints.add(index, mutatedConstraint);
			Symex sym = Symex.makeEngine();
			
			List<Constraint> pc = sym.randomWalkSymbolicExecution(currentConstraints);
			int newFitness = sym.getInstructionCount();
			
			System.out.println("Local search at index " + index + ": " + individual.getFitness()+ " --> " + newFitness);
			if(newFitness > individual.getFitness()) {
				System.out.println("Successful");
				individual.setConstraintSet(pc);
				individual.setFitness(newFitness);
				if(remainingAttempts < Config.localSearchRate) {
					break; //remainingAttempts = 0;//TODO
				}
			} else if(newFitness == individual.getFitness()) {
				individual.setConstraintSet(pc);
				individual.setFitness(newFitness);
			}

            index = (index + 1) % individual.getConstraintSet().size();
		}
		return individual;
	}
}
