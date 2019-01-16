package gasp.ga.operators.crossover;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gasp.ga.Constraint;
import gasp.ga.Individual;
import gasp.ga.fitness.FitnessEvaluationException;
import gasp.ga.fitness.FitnessFunction;
import gasp.ga.operators.mutation.MutationException;
import gasp.ga.operators.mutation.MutationFunction;
import gasp.se.Symex;
import gasp.se.SymexJBSE;
import gasp.utils.Utils;

public class CrossoverFunctionSinglePoint implements CrossoverFunction {
	private final MutationFunction mutationFunction;
	private final FitnessFunction fitnessFunction;
	private final double mutationSizeRatio;
	private final Random random;
	private final List<Path> classpath;
	private final Path jbsePath;
	private final Path z3Path;	
	private final String methodClassName;
	private final String methodDescriptor;	
	private final String methodName;
	
	public CrossoverFunctionSinglePoint(MutationFunction mutationFunction, FitnessFunction fitnessFunction, 
			                            double mutationSizeRatio, Random random, List<Path> classpath, Path jbsePath, 
			                            Path z3Path, String methodClassName, String methodDescriptor, String methodName) {
		if (mutationFunction == null) {
			throw new IllegalArgumentException("Mutation function cannot be null.");
		}
		if (fitnessFunction == null) {
			throw new IllegalArgumentException("Fitness function cannot be null.");
		}
		if (mutationSizeRatio < 0 || mutationSizeRatio > 1) {
			throw new IllegalArgumentException("Mutation size ratio cannot be less than 0 or greater than 1.");
		}
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
		if (classpath == null) {
			throw new IllegalArgumentException("Classpath cannot be null.");
		}
		if (jbsePath == null) {
			throw new IllegalArgumentException("JBSE path cannot be null.");
		}
		if (z3Path == null) {
			throw new IllegalArgumentException("Z3 path cannot be null.");
		}
		if (methodClassName == null) {
			throw new IllegalArgumentException("Method class name cannot be null.");
		}
		if (methodDescriptor == null) {
			throw new IllegalArgumentException("Method descriptor cannot be null.");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name cannot be null.");
		}

		this.mutationFunction = mutationFunction;
		this.fitnessFunction = fitnessFunction;
		this.mutationSizeRatio = mutationSizeRatio;
		this.random = random;
		this.classpath = classpath;
		this.jbsePath = jbsePath;
		this.z3Path = z3Path;
		this.methodClassName = methodClassName;
		this.methodDescriptor = methodDescriptor;
		this.methodName = methodName;
	}

	@Override
	public Individual[] crossover(Individual parent1, Individual parent2) throws CrossoverException {
		List<Constraint> constraints1 = parent1.getConstraintSetClone();
		List<Constraint> constraints2 = parent2.getConstraintSetClone();
		int cp1;
		int cp2;
		
		if (constraints1.size() > 0) {
			cp1 = this.random.nextInt(constraints1.size());
		} else {
			throw new CrossoverException("Crossover produced no children: parent1 has no constraints");
		}
		
		if (constraints2.size() > 0) {
			cp2 = this.random.nextInt(constraints2.size());
		} else {
			throw new CrossoverException("Crossover produced no children: parent2 has no constraints");
		}
				
        List<Constraint> childConstraints1 = combine(constraints1.subList(0, cp1), constraints2.subList(cp2, constraints2.size()));
        List<Constraint> childConstraints2 = combine(constraints2.subList(0, cp2), constraints1.subList(cp1, constraints1.size()));
        
        ArrayList<Individual> children = new ArrayList<>();

        Exception e1 = null;
        try {
            this.mutationFunction.applyMutationToConstraintSetPortion(childConstraints1, this.mutationSizeRatio);
        	Individual child1 = this.fitnessFunction.evaluate(childConstraints1);
	        children.add(child1);
		} catch (FitnessEvaluationException | MutationException e) { 
			e1 = e;
		}

        Exception e2 = null;
		try {
			this.mutationFunction.applyMutationToConstraintSetPortion(childConstraints2, this.mutationSizeRatio);
			final Individual child2 = this.fitnessFunction.evaluate(childConstraints2);
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
		
		for (Constraint c : constraints2) {
			final Symex se = new SymexJBSE(this.classpath, 
                                           this.jbsePath,
                                           this.z3Path, 
                                           this.methodClassName, 
                                           this.methodDescriptor, 
                                           this.methodName);
			final List<Constraint> slice = se.formulaSlicing(result, c);
		
			if (!Utils.isInconsistent(c, slice) && !Utils.isRedundant(c, slice)) { 
				result.add(c);
			}
		}
	
		return result;
	}
}