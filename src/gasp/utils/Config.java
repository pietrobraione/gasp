package gasp.utils;

import gasp.ga.operators.crossover.CrossoverFunction;
import gasp.ga.operators.crossover.SinglePointCrossover;
import gasp.ga.operators.selection.RankSelection;
import gasp.ga.operators.selection.SelectionFunction;

public class Config {
	
	public static final int generations = 5; //number of generations to be executed
	public static final int populationSize = 10; //number of individuals in the population
	public static final CrossoverFunction crossoverFunction = new SinglePointCrossover(); //which crossover function (prefix, exclude, singlepoint, union)
	public static final SelectionFunction selectionFunction = new RankSelection(); //which selection function (currently just rank)
	public static final double mutationProb = 0.1; //probability of applying the mutation operator
	public static final double eliteRatio = 0.1; //best individuals that are preserved (elite) expressed as percentage of the total population
	public static final int seed = 5; //random number seed		   
	public static final String evolutionCsv = null; //output file	
	public static final int timeout = 1;	
	public static final int localSearchRate = 5;
	public static final String z3Path="/opt/local/bin/z3";
	public static final String classTarget="/Users/pietro/git/sushi/jbse/target/classes";
	public static final String programPath="/Users/pietro/git/jbse-examples/bin";
	public static final String className="smalldemos/ifx/IfExample";
	public static final String descriptor="(I)V";
	public static final String methodName="m";

	//int pool_size;
	//random_search;

		
}
