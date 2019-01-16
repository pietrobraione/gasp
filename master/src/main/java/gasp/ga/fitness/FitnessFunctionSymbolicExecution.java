package gasp.ga.fitness;

import java.nio.file.Path;
import java.util.List;

import gasp.ga.Constraint;
import gasp.ga.Individual;
import gasp.se.Symex;
import gasp.se.SymexJBSE;

public class FitnessFunctionSymbolicExecution implements FitnessFunction {
	private final List<Path> classpath;
	private final Path jbsePath;
	private final Path z3Path;	
	private final String methodClassName;
	private final String methodDescriptor;	
	private final String methodName;
	
	public FitnessFunctionSymbolicExecution(List<Path> classpath, Path jbsePath, Path z3Path, String methodClassName, String methodDescriptor, String methodName) {
		if (classpath == null) {
			throw new IllegalArgumentException("Classpath cannot be null");
		}
		if (jbsePath == null) {
			throw new IllegalArgumentException("JBSE path cannot be null");
		}
		if (z3Path == null) {
			throw new IllegalArgumentException("Z3 path cannot be null");
		}
		if (methodClassName == null) {
			throw new IllegalArgumentException("Method class name cannot be null");
		}
		if (methodDescriptor == null) {
			throw new IllegalArgumentException("Method descriptor cannot be null");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name cannot be null");
		}
		
		this.classpath = classpath;
		this.jbsePath = jbsePath;
		this.z3Path = z3Path;
		this.methodClassName = methodClassName;
		this.methodDescriptor = methodDescriptor;
		this.methodName = methodName;
	}
	
	@Override
	public Individual evaluate(List<Constraint> constraintSet) throws FitnessEvaluationException {
		final Symex se = new SymexJBSE(this.classpath, 
                                       this.jbsePath, 
                                       this.z3Path, 
                                       this.methodClassName, 
                                       this.methodDescriptor, 
                                       this.methodName);
		final List<Constraint> pc = se.randomWalkSymbolicExecution(constraintSet);
        return new Individual(pc, se.getInstructionCount());
	}
}
