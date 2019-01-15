package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;

import gasp.ga.Constraint;
import gasp.ga.Individual;
import gasp.ga.fitness.FitnessEvaluationException;
import gasp.ga.operators.mutation.MutationException;
import gasp.se.Symex;
import gasp.utils.Config;
import gasp.utils.RandomNumberSupplier;
import gasp.utils.Utils;

public class CrossoverFunctionSinglePoint extends CrossoverFunction {

	@Override
	public Individual[] crossover(Individual parent1, Individual parent2) throws CrossoverException {
		List<Constraint> constraints1 = parent1.getConstraintSetClone();
		List<Constraint> constraints2 = parent2.getConstraintSetClone();
		int cp1;
		int cp2;
		
		if (constraints1.size() > 0) {
			cp1 = RandomNumberSupplier._I().nextInt(constraints1.size());
		} else {
			throw new CrossoverException("Crossover produced no children: parent1 has no constraints");
		}
		
		if (constraints2.size() > 0) {
			cp2 = RandomNumberSupplier._I().nextInt(constraints2.size());
		} else {
			throw new CrossoverException("Crossover produced no children: parent2 has no constraints");
		}
				
        List<Constraint> childConstraints1 = combine(constraints1.subList(0, cp1), constraints2.subList(cp2, constraints2.size()));
        List<Constraint> childConstraints2 = combine(constraints2.subList(0, cp2), constraints1.subList(cp1, constraints1.size()));
        
        ArrayList<Individual> children = new ArrayList<>();

        Exception e1 = null;
        try {
            Config.mutationFunction.applyMutationToConstraintSetPortion(childConstraints1, Config.mutationSizeRatio);
        	Individual child1 = Config.fitnessFunction.evaluate(childConstraints1);
	        children.add(child1);
		} catch (FitnessEvaluationException | MutationException e) { 
			e1 = e;
		}

        Exception e2 = null;
		try {
	        Config.mutationFunction.applyMutationToConstraintSetPortion(childConstraints2, Config.mutationSizeRatio);
			Individual child2 = Config.fitnessFunction.evaluate(childConstraints2);
	        children.add(child2);
		} catch (FitnessEvaluationException | MutationException e) { 
			e2 = e;
		}

		if (children.isEmpty()) {
			throw new CrossoverException("Crossover produced no children: " + e1, e2);
		}

        return children.toArray(new Individual[children.size()]);
	}
	
	private List<Constraint> combine(List<Constraint> constraints1, List<Constraint> constraints2){
		List<Constraint> result = new ArrayList<>(constraints1);
		
		for (Constraint c: constraints2) {
			List<Constraint> slice = Symex.makeEngine().formulaSlicing(result, c);
		
			if (!Utils.isInconsistent(c, slice) && !Utils.isRedundant(c, slice)) { 
				result.add(c);
			}
		}
	
		return result;
	}
}