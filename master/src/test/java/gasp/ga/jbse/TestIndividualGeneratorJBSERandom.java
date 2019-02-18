package gasp.ga.jbse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gasp.ga.FoundWorstIndividualException;
import gasp.ga.jbse.IndividualGeneratorJBSE;

@DisplayName("gasp.ga.jbse.IndividualGeneratorJBSE test suite (purely random generation)")
public class TestIndividualGeneratorJBSERandom {
	private static final long MAX_FITNESS = 1_000_000;
	private static final List<Path> CLASSPATH = new ArrayList<Path>(); 
	static {
		CLASSPATH.add(Paths.get("/Users", "pietro", "git", "jbse-examples", "bin"));
	}
	private static final Path JBSE_PATH = Paths.get("/Users", "pietro", "git", "gasp", "jbse", "build", "classes", "java", "main"); 
	private static final Path Z3_PATH = Paths.get("/opt", "local", "bin", "z3");
	private static final String METHOD_CLASS_NAME = "smalldemos/ifx/IfExample";
	private static final String METHOD_DESCRIPTOR = "(I)V";
	private static final String METHOD_NAME = "m";
	
	private IndividualGeneratorJBSE ig;
	
	@BeforeEach
	private void beforeEach() {
		this.ig = new IndividualGeneratorJBSE(MAX_FITNESS, new Random(0), CLASSPATH, JBSE_PATH, Z3_PATH, METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
	}

	@Test
	@DisplayName("IndividualGeneratorJBSE.generateRandomIndividual does not return null")
	public void testRandomIndividual1() throws FoundWorstIndividualException {
		assertNotNull(this.ig.generateRandomIndividual());
	}
	
	@Test
	@DisplayName("IndividualGeneratorJBSE.generateRandomIndividual returns an individual with a nonnull constraint set")
	public void testRandomIndividual2() throws FoundWorstIndividualException {
		assertNotEquals(this.ig.generateRandomIndividual().getChromosome(), null);
	}
	
	@Test
	@DisplayName("IndividualGeneratorJBSE.generateRandomIndividual returns an individual with a nonempty constraint set")
	public void testRandomIndividual3() throws FoundWorstIndividualException {
		assertFalse(this.ig.generateRandomIndividual().getChromosome().isEmpty());
	}
	
	@Test
	@DisplayName("IndividualGeneratorJBSE.generateRandomIndividual returns an individual with a fitness value greater than 0")
	public void testRandomIndividual4() throws FoundWorstIndividualException {
		assertTrue(this.ig.generateRandomIndividual().getFitness() > 0);
	}
}
