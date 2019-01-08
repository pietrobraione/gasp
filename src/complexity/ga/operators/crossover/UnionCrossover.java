package complexity.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;

import complexity.ga.FitnessFunction;
import complexity.ga.Individual;
import complexity.se.Constraint;
import complexity.utils.Utils;

public class UnionCrossover extends CrossoverFunction{

	@Override
	public ArrayList<Individual> crossover(Individual parent1, Individual parent2) {
		
		List<Constraint> constraints1 = parent1.getConstraintSet();
		List<Constraint> constraints2 = parent2.getConstraintSet();
		
		List<Constraint> allConstraints = new ArrayList<>();
		allConstraints.addAll(constraints1);
		allConstraints.addAll(constraints2);

        List<Constraint> childConstraints1 = new ArrayList<>();
        List<Constraint> childConstraints2 = new ArrayList<>();
        while(!allConstraints.isEmpty()) {
            Constraint c = allConstraints.remove(allConstraints.size() - 1);
            Constraint notC = Utils.negate(c);
            if(allConstraints.contains(notC)){
                allConstraints.remove(notC);
                //TODO genes = [c, not_c];
                //config.random.shuffle(genes);
                //child_constraints1.append(genes[0]);
                //child_constraints2.append(genes[1]);
            }else{
                childConstraints1.add(c);
                childConstraints2.add(c);
                }
		}
		
		Individual child1 = FitnessFunction.evaluate(childConstraints1);
        Individual child2 = FitnessFunction.evaluate(childConstraints2);
        
        ArrayList<Individual> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        return children; //return child1, child2
	}
	
}
