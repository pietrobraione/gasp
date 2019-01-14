package gasp.ga;
import java.util.*;

import gasp.se.Constraint;
import gasp.se.Model;
import gasp.se.Symex;

/* Individuals are immutable */

public class Individual implements Cloneable, Comparable<Individual> {
	
	
	private final List<Constraint> constraintSet;
	private int fitness;
	
	public Individual(List<Constraint> constraintSet, int fitness) {
		if(constraintSet == null || constraintSet.isEmpty()){
			throw new IllegalArgumentException("the constraint set can't be empty or null");
		}
		this.constraintSet = new ArrayList<>(constraintSet);
		this.fitness = fitness;
	}

	public static Individual makeRandomIndividual() {
		Symex se = Symex.makeEngine();
		
		List<Constraint> constraintSet = se.randomWalkSymbolicExecution();
		int fitness = se.getInstructionCount();

		return new Individual(constraintSet, fitness);
	}

	public List<Constraint> getConstraintSetClone() {
		return new ArrayList<>(constraintSet);
	}

	public int size() {
		return constraintSet.size();
	}

	public int getFitness() {
		return fitness;
	}

	// Return the solution of the formula represent by this individual  
	public Model getModel() {
		return new Model(constraintSet);
	}
	
	@Override
	public String toString() {
		return "Individual [fitness = " + fitness + ", constraintSet = " + constraintSet + "]";
	}
			
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((constraintSet == null) ? 0 : constraintSet.hashCode());
		result = prime * result + fitness;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Individual other = (Individual) obj;
		if (constraintSet == null) {
			if (other.constraintSet != null)
				return false;
		} else if (!constraintSet.equals(other.constraintSet))
			return false;
		if (fitness != other.fitness)
			return false;
		return true;
	}

	@Override
	public int compareTo(Individual other) {
		if (this.fitness < other.fitness) {
			return -1;
		} else if (this.fitness == other.fitness) {
			return 0;
		} else {
			return 1;
		}
	}

	
}
