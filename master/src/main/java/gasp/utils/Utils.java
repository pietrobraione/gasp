package gasp.utils;

import java.util.*;

import gasp.ga.Gene;
import gasp.ga.Individual;

public interface Utils {
	public static String getName() {
		return Utils.class.getPackage().getImplementationTitle();
	}
	
	public static String getVendor() {
		return Utils.class.getPackage().getImplementationVendor();
	}
	
	public static String getVersion() {
		return Utils.class.getPackage().getImplementationVersion();
	}
	
	/*
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
	*/
	
	public static <T extends Gene<T>> String logFitnessStats(List<Individual<T>> individuals) {
		final ArrayList<Integer> fitnesses = new ArrayList<Integer>();
		int sum = 0;
		for (int i = 0; i < individuals.size(); i++) {
			fitnesses.add(individuals.get(i).getFitness());
			sum += individuals.get(i).getFitness();
		}
		
        final int minFitness = Collections.min(fitnesses);
        final int maxFitness = Collections.max(fitnesses);
        final int avgFitness = sum / fitnesses.size();
        return "max: " + maxFitness + ", min: " + minFitness + ", avg: " + avgFitness;
	}
	


}
