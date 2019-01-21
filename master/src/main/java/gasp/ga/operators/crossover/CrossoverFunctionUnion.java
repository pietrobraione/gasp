package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.Pair;

public final class CrossoverFunctionUnion<T extends Gene<T>> implements CrossoverFunction<T> {
	private final Random random;

	public CrossoverFunctionUnion(Random random) {		
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		
		this.random = random;
	}

	@Override
	public Pair<List<T>> crossover(List<T> chromosome1, List<T> chromosome2) {
		final List<T> allGenes = new ArrayList<>();
		allGenes.addAll(chromosome1);
		allGenes.addAll(chromosome2);

		final List<T> childChromosome1 = new ArrayList<>();
		final List<T> childChromosome2 = new ArrayList<>();
        for (T g : allGenes) {
            T notG = g.not();
            if (allGenes.contains(notG)) {
                allGenes.remove(notG);
            }
            if (this.random.nextBoolean()) {
                childChromosome1.add(g);
                childChromosome2.add(notG);
            } else {
                childChromosome1.add(notG);
                childChromosome2.add(g);
            }
		}
		
        return new Pair<>(childChromosome1, childChromosome2);
	}
}
