package gasp.utils;

import java.util.*;
import java.util.stream.Collectors;

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
	
	public static <T extends Gene<T>, U extends Individual<T>> String logFitnessStats(List<U> individuals) {
		final List<Long> fitnesses = individuals.stream().map(i -> i.getFitness()).collect(Collectors.toList());
        final long maxFitness = Collections.max(fitnesses);
        final long avgFitness = fitnesses.stream().reduce(0L, (a, b) -> a + b) / fitnesses.size();
        final long minFitness = Collections.min(fitnesses);
        return "max: " + maxFitness + ", avg: " + avgFitness + ", min: " + minFitness;
	}
}
