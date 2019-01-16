package gasp;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import gasp.ga.GeneticAlgorithm;
import gasp.ga.Individual;
import gasp.utils.Config;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		final Main m = new Main();
		m.run();
	}
	
	public void run() {
		configureLogger();
		final Logger logger = LogManager.getLogger(Main.class);
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
	
	private void configureLogger() {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setStatusLevel(Level.WARN);
		AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE");
		appenderBuilder.addAttribute("target",ConsoleAppender.Target.SYSTEM_OUT);
		appenderBuilder.add(builder.newLayout("PatternLayout")
			                       .addAttribute("pattern", "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"));
		builder.add(appenderBuilder);
		LoggerComponentBuilder loggerBuilder = builder.newLogger("gasp.ga.GeneticAlgorithm", Level.TRACE);
		loggerBuilder.addAttribute("additivity", false);
		loggerBuilder.add(builder.newAppenderRef("Stdout"));
		builder.add(loggerBuilder);
		RootLoggerComponentBuilder rootLoggerBuilder = builder.newRootLogger(Level.DEBUG);
		rootLoggerBuilder.add(builder.newAppenderRef("Stdout"));
		builder.add(rootLoggerBuilder);
		Configurator.initialize(builder.build());
	}
}













