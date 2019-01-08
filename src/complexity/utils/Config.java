package complexity.utils;

import complexity.ga.operators.crossover.CrossoverFunction;
import complexity.ga.operators.crossover.SinglePointCrossover;
import complexity.ga.operators.selection.RankSelection;
import complexity.ga.operators.selection.SelectionFunction;
import complexity.se.SymexJBSE;

public class Config {
	
	public static final int generations = 5; //n� di generazioni che l'algoritmo deve eseguire
	public static final int populationSize = 10; //n� di individui presenti nella popolazione
	public static final CrossoverFunction crossoverFunction = new SinglePointCrossover(); //quale funzione di crossover sar� usata (prefix, exclude, singlepoint, union)
	public static final SelectionFunction selectionFunction = new RankSelection(); //quale funzione di crossover sar� usata (prefix, exclude, singlepoint, union)
	public static final double mutationProb = 0.1; //probabilit� di applicare o meno l'operatore di mutazione agli individui generati
	public static final double eliteRatio = 0.1; //n� individui migliori che vengono preservati (elite) in percentuale rispetto alla popolazione
	public static final int seed = 5; //un numero da usare come seed per il generatore di numeri random		   
	public static final String evolutionCsv = null; //nome di un file per scrivere i risultati, per adesso potremmo ignorarlo	
	public static final int timeout = 1;	
	public static final int localSearchRate = 5;
	public static String z3Path="../z3-4.6.0-x64-osx-10.11.6/bin/z3";
	public static String classTarget="../jbse/target/classes";
	public static String dataTarget="../jbse/data/jre/rt.jar";
	public static String programPath=("../example/bin");
	public static String className="example/IfExample";
	public static String descriptor="(I)V";
	public static String methodName="m";

	//int pool_size;
	//random_search;

		
}
