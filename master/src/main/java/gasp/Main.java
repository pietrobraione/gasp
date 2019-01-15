package gasp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gasp.ga.GeneticAlgorithm;
import gasp.ga.Individual;
import gasp.utils.Config;

import java.io.IOException;

public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) throws IOException {
		logger.info("GASP started, going to evolve " + Config.generations  + " generations");
		logger.info("Estimated fitness evaluation: " + Config.estimateFitnessEvaluations());

		final GeneticAlgorithm ga = new GeneticAlgorithm(Config.selectionFunction, Config.crossoverFunction, Config.localSearchAlgorithm);	
		ga.generateSolution();
		final Individual solution = ga.getBestSolutions(1).get(0);
		
		logger.info("Worst case input: " + solution.getConstraintSetClone());
		logger.info("Worst case model: " + solution.getModel());
		logger.info("Worst case cost: " + solution.getFitness());	
		logger.info("GASP ended");
	}
}













