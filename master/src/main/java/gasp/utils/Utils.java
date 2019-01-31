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
	
	public static <T extends Gene<T>> String logFitnessStats(List<Individual<T>> individuals) {
		final ArrayList<Long> fitnesses = new ArrayList<>();
		int sum = 0;
		for (int i = 0; i < individuals.size(); i++) {
			fitnesses.add(individuals.get(i).getFitness());
			sum += individuals.get(i).getFitness();
		}
		
        final long minFitness = Collections.min(fitnesses);
        final long maxFitness = Collections.max(fitnesses);
        final long avgFitness = sum / fitnesses.size();
        return "max: " + maxFitness + ", min: " + minFitness + ", avg: " + avgFitness;
	}
}
