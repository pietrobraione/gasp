package gasp.utils;

import java.util.*;

import gasp.ga.Constraint;
import gasp.ga.Individual;

public abstract class Utils {
	
	public static Constraint mkAnd(List<Constraint> refs) {
		return null; //TODO;
	}
	
	public static Constraint mkImplies(Constraint c1, Constraint c2) {
		return null; //TODO
	}
		
	public static boolean isContradiction(Constraint c) {
		return false; //TODO
	}
	
	public static boolean isTautology(Constraint c) {
		return false; //TODO
	}
	
	public static boolean isImplied(List<Constraint> constraintSet, Constraint c) {
		return isTautology(mkImplies(mkAnd(constraintSet), c));
	}
	
	public static boolean isInconsistent(List<Constraint> constraintSet) {
		return isContradiction(mkAnd(constraintSet));
	}
	
	public static boolean isInconsistent(Constraint c, List<Constraint> cc) {
		if (cc.isEmpty()) {
			return false;
		}
		return false; //TODO
	}

	public static boolean isRedundant(Constraint c, List<Constraint> slice) {
		return false; //TODO
	}
	
	public static String logIndividuals(List<Individual> individuals) {
		String retValue = "";
		
		int id = 1;
		for(Individual ind: individuals) {
			retValue += id++ + ". " + ind + "\n";
		}
		
		return retValue;
	}
	
	public static String logFitnessStats(List<Individual> individuals) {
		ArrayList<Integer> fitnesses = new ArrayList<Integer>();
		int sum = 0;
		
		for(int i = 0; i < individuals.size(); i++){
			fitnesses.add(individuals.get(i).getFitness());
			sum += individuals.get(i).getFitness();
		}
		
        int minFitness = Collections.min(fitnesses);
        int maxFitness = Collections.max(fitnesses);
        int avgFitness = sum / fitnesses.size();
        return "max: " + maxFitness + ", min: " + minFitness + ", avg: " + avgFitness;
	}
	


}
