package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.Individual;
import gasp.ga.IndividualGenerator;

public class CrossoverFunctionPrefix_NotUsedYet<T extends Gene<T>> implements CrossoverFunction<T> {
	private final IndividualGenerator<T> individualGenerator;
	private final Random random;
	
	public CrossoverFunctionPrefix_NotUsedYet(IndividualGenerator<T> individualGenerator, Random random) {		
		if (individualGenerator == null) {
			throw new IllegalArgumentException("The individual generator cannot be null.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		
		this.individualGenerator = individualGenerator;
		this.random = random;
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public Individual<T>[] crossover(Individual<T> parent1, Individual<T> parent2) throws CrossoverException {
		final List<T> chromosome1 = parent1.getChromosome();
		final List<T> chromosome2 = parent2.getChromosome();

		final List<T> parent1Prefix = splitConstraints(chromosome1).get(0);
		final List<T> parent1Split1 = splitConstraints(chromosome1).get(1);
		final List<T> parent1Split2 = splitConstraints(chromosome1).get(2);
		final List<T> parent2Prefix = splitConstraints(chromosome2).get(0);
		final List<T> parent2Split1 = splitConstraints(chromosome2).get(1);
		final List<T> parent2Split2 = splitConstraints(chromosome2).get(2);
        
		final List<T> child1Constraints = combineChromosomes(parent1Prefix, (this.random.nextBoolean() ? parent2Split1 : parent2Split2));
		final List<T> child2Constraints = combineChromosomes(parent2Prefix, (this.random.nextBoolean() ? parent1Split1 : parent1Split2));
  
        final ArrayList<Individual<T>> children = new ArrayList<>();

        final Individual<T> child1 = this.individualGenerator.generateRandomIndividual(child1Constraints);
        if (child1 != null) {
        	children.add(child1);
        }
        final Individual<T> child2 = this.individualGenerator.generateRandomIndividual(child2Constraints);
        if (child2 != null) {
        	children.add(child2);
        }
    
		if (children.isEmpty()) {
			throw new CrossoverException("Crossover produced no children.");
		}

        return children.toArray(new Individual[children.size()]);
	}
	
	private List<List<T>> splitConstraints(List<T> chromosome) {
		final List<List<T>> splitset = new ArrayList<>(3);
		final List<T> get0 = new ArrayList<>(1);
		get0.add(chromosome.get(0));
		final List<T> get1 = new ArrayList<>(1);
		get1.add(chromosome.get(0));
		final List<T> get2 = new ArrayList<>(1);
		get2.add(chromosome.get(0));
		if (chromosome.size() == 0) {
			return splitset;
		} else if (chromosome.size() == 1) {
			splitset.add(chromosome);
			return splitset;
        } else if (chromosome.size() == 2) {
        	splitset.add(get0);
            splitset.add(get1);
            return splitset;
        } else if (chromosome.size() == 3) {
        	splitset.add(get0);
            splitset.add(get1);
            splitset.add(get2);
            return splitset;
        } else if (chromosome.size() > 2) {
        	int point1 = this.random.nextInt(chromosome.size() - 2) + 1;
        	int point2 = this.random.nextInt((chromosome.size() + point1) + 1) + point1 + 1;
        	final List<T> set1 = new ArrayList<>();
        	final List<T> set12 = new ArrayList<>();
        	final List<T> set2 = new ArrayList<>();
        	for (int i = 0; i < point1; ++i) {
        		set1.add(chromosome.get(i));
        	}
        	for (int i = point1; i <= point2; ++i){
        		set12.add(chromosome.get(i));
        	}
        	for (int i = point2; i < chromosome.size(); ++i) {
        		set2.add(chromosome.get(i));
        	}
        	splitset.add(set1);
            splitset.add(set12);
            splitset.add(set2);
        }
		return splitset;
	}
	
	private List<T> combineChromosomes(List<T> chromosome1, List<T> chromosome2) {
		final List<T> result = new ArrayList<>(chromosome1);
        for (int i = 0; i < chromosome2.size(); ++i){
        	result.add(chromosome2.get(i));
        	/*if (Utils.isInconsistent(result)){
        		result.remove(constraints2.size() - 1);
        	}*/
        }
        return result;
	}
	
}
