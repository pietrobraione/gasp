package complexity.ga.operators.crossover;

import java.util.*;

import complexity.ga.FitnessFunction;
import complexity.ga.Individual;
import complexity.ga.operators.mutation.MutationFunction;
import complexity.se.Constraint;
import complexity.se.Symex;
import complexity.utils.RandomSingleton;

public class SinglePointCrossover extends CrossoverFunction {

	@Override
	public List<Individual> crossover(Individual parent1, Individual parent2) {
		List<Constraint> constraints1 = parent1.getConstraintSet();
		List<Constraint> constraints2 = parent2.getConstraintSet();
		int cp1;
		int cp2;
		
		if(constraints1.size() > 1) {
			cp1 = RandomSingleton.getInstance().nextInt(constraints1.size() - 1) + 1;
		}else {
			cp1 = 0;
		}
		if(constraints2.size() > 1) {
			cp2 = RandomSingleton.getInstance().nextInt(constraints2.size() - 1) + 1;
		}else {
			cp2 = 0;
		}
				
        List<Constraint> childConstraints1 = combine(constraints1.subList(0, cp1), constraints2.subList(cp2, constraints2.size()));
        List<Constraint> childConstraints2 = combine(constraints2.subList(0, cp2), constraints1.subList(cp1, constraints1.size()));
        
        MutationFunction.mutationBis(childConstraints1);
        MutationFunction.mutationBis(childConstraints2);

        Individual child1 = FitnessFunction.evaluate(childConstraints1);
        Individual child2 = FitnessFunction.evaluate(childConstraints2);
        
        ArrayList<Individual> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        
        return children;
	}
	
	private List<Constraint> combine(List<Constraint> constraints1, List<Constraint> constraints2){
		List<Constraint> result = new ArrayList<>(constraints1);
		for (Constraint c2: constraints2) {
			List<Constraint> slice = Symex.makeEngine().formulaSlicing(result, c2);
			if (slice.isEmpty()) {
				result.add(c2);
			} /*else if (!slice.get(0).equals(Constraint.TRUE) && !slice.get(0).equals(Constraint.FALSE)) {
				if (!c2.isInconsistent(slice)) {
					result.add(c2);                			
				}
			}*/
		}
	
		return result;
	}
}