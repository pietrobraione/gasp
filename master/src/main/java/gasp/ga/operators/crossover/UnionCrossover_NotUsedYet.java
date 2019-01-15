package gasp.ga.operators.crossover;

import java.util.ArrayList;
import java.util.List;

import gasp.ga.FitnessEvaluationException;
import gasp.ga.FitnessFunction;
import gasp.ga.Individual;
import gasp.se.Constraint;
import gasp.utils.Utils;

public class UnionCrossover_NotUsedYet extends CrossoverFunction{

	@Override
	public Individual[] crossover(Individual parent1, Individual parent2) throws CrossoverException {
		
		List<Constraint> constraints1 = parent1.getConstraintSetClone();
		List<Constraint> constraints2 = parent2.getConstraintSetClone();
		
		List<Constraint> allConstraints = new ArrayList<>();
		allConstraints.addAll(constraints1);
		allConstraints.addAll(constraints2);

        List<Constraint> childConstraints1 = new ArrayList<>();
        List<Constraint> childConstraints2 = new ArrayList<>();
        while(!allConstraints.isEmpty()) {
            Constraint c = allConstraints.remove(allConstraints.size() - 1);
            Constraint notC = c.mkNot();
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
		
        ArrayList<Individual> children = new ArrayList<>();

        try {
        	Individual child1 = FitnessFunction.evaluate(childConstraints1);
	        children.add(child1);
		} catch (FitnessEvaluationException e) { }

		try {
			Individual child2 = FitnessFunction.evaluate(childConstraints2);
	        children.add(child2);
		} catch (FitnessEvaluationException e) { }
    
		if (children.isEmpty()) {
			throw new CrossoverException("Crossover produced no children");
		}
		
        return children.toArray(new Individual[children.size()]);
        
	}
	
}
