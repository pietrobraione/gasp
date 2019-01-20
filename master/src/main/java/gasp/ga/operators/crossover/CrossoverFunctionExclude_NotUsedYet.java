package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Gene;
import gasp.ga.Individual;

public final class CrossoverFunctionExclude_NotUsedYet<T extends Gene<T>> implements CrossoverFunction<T> {
	private final Random random;

	public CrossoverFunctionExclude_NotUsedYet(Random random) {
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		
		this.random = random;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Individual<T>[] crossover(Individual<T> parent1, Individual<T> parent2) {

		final List<T> constraints1 = parent1.getChromosome();
		final List<T> constraints2 = parent2.getChromosome();
		
		final List<T> parent1Split1 = splitConstraints(constraints1).get(0);
		final List<T> parent1Split2 = splitConstraints(constraints1).get(1);
		final List<T> parent1Split3 = splitConstraints(constraints1).get(2);
		final List<T> parent2Split1 = splitConstraints(constraints2).get(0);
		final List<T> parent2Split2 = splitConstraints(constraints2).get(1);
		final List<T> parent2Split3 = splitConstraints(constraints2).get(2);

		List<List<T>> options = new ArrayList<>();
		options.add(parent1Split1);
		options.add(parent2Split1);
		options.add(parent1Split2);
		options.add(parent2Split2);
		options.add(parent1Split3);
		options.add(parent2Split3);

        //List<List<Constraint>> choices = new ArrayList<>();
		
        //TODO choices = rng.sample(options, k=2); //usare (rng.nextBoolean() ? parent2Split1 : parent2Split2)
        //List<Constraint> child1Contraints = combine(choices[0][0], choices[1][1]);
        //List<Constraint> child2Contraints = combine(choices[1][0], choices[0][1]);

		//Individual child1 = FitnessFunction.evaluate(child1Constraints);
        //Individual child2 = FitnessFunction.evaluate(child2Constraints);
        
        final ArrayList<Individual<T>> children = new ArrayList<>();
        //children.add(child1);
        //children.add(child2);
        return children.toArray(new Individual[children.size()]);
	}
	
	private List<List<T>> splitConstraints(List<T> constraints) {
		final List<List<T>> splitset = new ArrayList<>(3);
		final List<T> get0 = new ArrayList<>(1);
		get0.add(constraints.get(0));
		final List<T> get1 = new ArrayList<>(1);
		get1.add(constraints.get(0));
		final List<T> get2 = new ArrayList<>(1);
		get2.add(constraints.get(0));
		if (constraints.size() == 0){
			return splitset;
		} else if (constraints.size() == 1) {
			splitset.add(constraints);
			return splitset;
        } else if (constraints.size() == 2) {
        	splitset.add(get0);
            splitset.add(get1);
            return splitset;
        } else if (constraints.size() == 3) {
        	splitset.add(get0);
            splitset.add(get1);
            splitset.add(get2);
            return splitset;
        } else if (constraints.size() > 2) {
        	final int point1 = this.random.nextInt(constraints.size() - 2) + 1;
        	final int point2 = this.random.nextInt((constraints.size() - point1) + 1) + point1 + 1;
        	final List<T> set1 = new ArrayList<>();
        	final List<T> set12 = new ArrayList<>();
        	final List<T> set2 = new ArrayList<>();
        	for (int i = 0; i < point1; ++i) {
        		set1.add(constraints.get(i));
        	}
        	for (int i = point1; i <= point2; ++i) {
        		set12.add(constraints.get(i));
        	}
        	for (int i = point2; i < constraints.size(); ++i) {
        		set2.add(constraints.get(i));
        	}
        	splitset.add(set1);
            splitset.add(set12);
            splitset.add(set2);
        }
		return splitset;
	}
	
	/*
	private List<Constraint> combine(List<Constraint> constraints1, List<Constraint> constraints2) {
		List<Constraint> result = constraints1;
        for(int i = 0; i < constraints2.size(); i++){
        	result.add(constraints2.get(i));
        	if(Utils.isInconsistent(result)){
        		result.remove(constraints2.size() - 1);
        		}
        	}
        return result;
	}*/
}
