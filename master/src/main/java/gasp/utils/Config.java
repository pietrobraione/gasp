package gasp.utils;

import java.nio.file.Paths;

import gasp.ga.fitness.FitnessFunction;
import gasp.ga.fitness.FitnessFunctionSymbolicExecution;
import gasp.ga.localSearch.LocalSearchAlgorithm;
import gasp.ga.localSearch.LocalSearchAlgorithmHillClimbing;
import gasp.ga.operators.crossover.CrossoverFunction;
import gasp.ga.operators.crossover.CrossoverFunctionSinglePoint;
import gasp.ga.operators.mutation.MutationFunctionDeleteConstraint;
import gasp.ga.operators.mutation.MutationFunction;
import gasp.ga.operators.selection.RankSelectionFunction;
import gasp.ga.operators.selection.SelectionFunction;

public class Config {
	
	public static final int generations = 5; //number of generations to be executed
	public static final int populationSize = 10; //number of individuals in the population
	
	public static final CrossoverFunction crossoverFunction = new CrossoverFunctionSinglePoint(); 
	public static final MutationFunction mutationFunction = new MutationFunctionDeleteConstraint(); 
	public static final SelectionFunction selectionFunction = new RankSelectionFunction(); //which selection function (currently just rank)
	public static final FitnessFunction fitnessFunction = new FitnessFunctionSymbolicExecution();
	
	public static final double mutationProb = 0.1; //probability of applying the mutation operator
	public static final double mutationSizeRatio = 0.1; //probability of applying the mutation operator

	public static final int eliteSize = 5; //num of best individuals that are preserved (elite)
	
	public static final int seed = 5; //random number seed		   
	
	public static final String evolutionCsv = null; //output file	
	
	public static final int timeout = 1;	
	
	public static final LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithmHillClimbing();
	public static final int localSearchRate = 5;
	
	//Giovanni's settings
	/*public static final String z3Path = Paths.get("/Users", "denaro", "Desktop", "RTools", "Z3", "z3-4.3.2.d548c51a984e-x64-osx-10.8.5", "bin", "z3").toString();	
	public static final String classTarget="/Users/denaro/git/gasp/jbse/build/classes/java/main";
	public static final String programPath="/Users/denaro/git/jbse-examples/bin";*/

	//Pietro's settings
	public static final String z3Path = Paths.get("/opt", "local", "bin", "z3").toString();	
	public static final String classTarget="/Users/pietro/git/gasp/jbse/build/classes/java/main";
	public static final String programPath="/Users/pietro/git/jbse-examples/bin";

	public static final String className="smalldemos/ifx/IfExample";
	public static final String descriptor="(I)V";
	public static final String methodName="m";
	
	//Returns an estimation of the number of fitness evaluations when running` with this configuration
	public static int estimateFitnessEvaluations() {
	    int g = generations;
	    int p = populationSize;
	    double mp = mutationProb;
	    return (int) Math.round(p * ((mp + 1) * g + 1));
	}
}
