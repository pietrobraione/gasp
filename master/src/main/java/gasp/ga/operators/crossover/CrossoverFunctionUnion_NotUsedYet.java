package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;

import gasp.ga.Gene;
import gasp.ga.Individual;
import gasp.ga.IndividualGenerator;

public final class CrossoverFunctionUnion_NotUsedYet<T extends Gene<T>> implements CrossoverFunction<T> {
	private final IndividualGenerator<T> individualGenerator;
	
	public CrossoverFunctionUnion_NotUsedYet(IndividualGenerator<T> individualGenerator) {		
		if (individualGenerator == null) {
			throw new IllegalArgumentException("The individual generator cannot be null.");
		}
		
		this.individualGenerator = individualGenerator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Individual<T>[] crossover(Individual<T> parent1, Individual<T> parent2) throws CrossoverException {
		final List<T> chromosome1 = parent1.getChromosome();
		final List<T> chromosome2 = parent2.getChromosome();
		
		final List<T> allGenes = new ArrayList<>();
		allGenes.addAll(chromosome1);
		allGenes.addAll(chromosome2);

		final List<T> childChromosome1 = new ArrayList<>();
		final List<T> childChromosome2 = new ArrayList<>();
        while (!allGenes.isEmpty()) {
            T g = allGenes.remove(allGenes.size() - 1);
            T notG = g.not();
            if (allGenes.contains(notG)) {
                allGenes.remove(notG);
                //TODO genes = [g, not_g];
                //config.random.shuffle(genes);
                //childChromosome1.append(genes[0]);
                //childChromosome2.append(genes[1]);
            } else {
                childChromosome1.add(g);
                childChromosome2.add(g);
            }
		}
		
        final ArrayList<Individual<T>> children = new ArrayList<>();

        final Individual<T> child1 = this.individualGenerator.generateRandomIndividual(childChromosome1);
        if (child1 != null) {
        	children.add(child1);
        }
        final Individual<T> child2 = this.individualGenerator.generateRandomIndividual(childChromosome2);
        if (child2 != null) {
        	children.add(child2);
        }
    
		if (children.isEmpty()) {
			throw new CrossoverException("Crossover produced no children");
		}
		
        return children.toArray(new Individual[children.size()]);
	}
}
