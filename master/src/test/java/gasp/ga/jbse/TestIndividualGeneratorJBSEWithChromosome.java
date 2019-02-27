package gasp.ga.jbse;

import static jbse.bc.Signatures.JAVA_CLONEABLE;
import static jbse.bc.Signatures.JAVA_OBJECT;
import static jbse.bc.Signatures.JAVA_SERIALIZABLE;
import static jbse.common.Type.ARRAYOF;
import static jbse.common.Type.CHAR;
import static jbse.val.HistoryPoint.unknown;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gasp.ga.jbse.IndividualGeneratorJBSE;
import jbse.bc.ClassFileFactoryJavassist;
import jbse.bc.ClassHierarchy;
import jbse.bc.Classpath;
import jbse.bc.exc.BadClassFileVersionException;
import jbse.bc.exc.ClassFileIllFormedException;
import jbse.bc.exc.ClassFileNotAccessibleException;
import jbse.bc.exc.ClassFileNotFoundException;
import jbse.bc.exc.IncompatibleClassFileException;
import jbse.bc.exc.InvalidClassFileFactoryClassException;
import jbse.bc.exc.WrongClassNameException;
import jbse.common.Type;
import jbse.common.exc.InvalidInputException;
import jbse.mem.Clause;
import jbse.mem.State;
import jbse.rewr.CalculatorRewriting;
import jbse.val.PrimitiveSymbolic;
import jbse.val.ReferenceSymbolic;
import jbse.val.SymbolFactory;

@DisplayName("gasp.ga.jbse.IndividualGeneratorJBSE test suite (with chromosome)")
public class TestIndividualGeneratorJBSEWithChromosome {
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
	private CalculatorRewriting calc;
	private SymbolFactory symbolFactory;
	private State s;
	private ClassHierarchy hier;
	
	@BeforeEach
	private void beforeEach() throws InvalidInputException, InvalidClassFileFactoryClassException, IOException, ClassFileNotFoundException, ClassFileIllFormedException, ClassFileNotAccessibleException, IncompatibleClassFileException, BadClassFileVersionException, WrongClassNameException {
		this.ig = new IndividualGeneratorJBSE(MAX_FITNESS, new Random(0), CLASSPATH, JBSE_PATH, Z3_PATH, METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
		this.calc = new CalculatorRewriting();
		this.symbolFactory = new SymbolFactory(calc);
		this.s = new State(true, unknown(), 10, 10, new Classpath(Paths.get(System.getProperty("java.home")), Collections.emptyList(), CLASSPATH), ClassFileFactoryJavassist.class, new HashMap<>(), this.calc, this.symbolFactory);
		this.hier = s.getClassHierarchy();
		this.hier.loadCreateClass(JAVA_OBJECT);
		this.hier.loadCreateClass(JAVA_CLONEABLE);
		this.hier.loadCreateClass(JAVA_SERIALIZABLE);
		this.hier.loadCreateClass("" + ARRAYOF + CHAR);
	}

	@Test
	@DisplayName("IndividualGeneratorJBSE.generateRandomIndividual(chromosome) correctly reorders numeric vs. symbolic reference clauses")
	public void testRandomIndividualWithChromosome1() throws Exception {
		//builds a path condition
		final ReferenceSymbolic R0 = (ReferenceSymbolic) this.symbolFactory.createSymbolLocalVariable(unknown(), "L" + METHOD_CLASS_NAME + ";", "foo");
		final PrimitiveSymbolic R0_a = (PrimitiveSymbolic) this.symbolFactory.createSymbolMemberField("Z", R0, "a", METHOD_CLASS_NAME);
		final PrimitiveSymbolic R0_b = (PrimitiveSymbolic) this.symbolFactory.createSymbolMemberField("C", R0, "b", METHOD_CLASS_NAME);
		s.assumeExpands(R0, this.hier.loadCreateClass(2, METHOD_CLASS_NAME, true));
		s.assume(R0_a.widen(Type.INT).eq(this.calc.valInt(0)));
		s.assume(R0_b.widen(Type.INT).ne(this.calc.valInt(0)));
		final List<Clause> pathCondition = s.getPathCondition();
		
		//builds an individual whose chromosome reorders the clauses in the path condition
		final ArrayList<GeneJBSE> chromosomeStart = new ArrayList<>();
		chromosomeStart.add(new GeneJBSE(pathCondition.get(1)));
		chromosomeStart.add(new GeneJBSE(pathCondition.get(2)));
		chromosomeStart.add(new GeneJBSE(pathCondition.get(0)));
		final IndividualJBSE individual = this.ig.generateRandomIndividual(chromosomeStart);
		
		//the individual's chromosome must have as first clause the same 
		//first clause in the path condition
		final List<GeneJBSE> chromosomeEnd = individual.getChromosome();
		assertEquals(pathCondition.get(0), chromosomeEnd.get(0).getClause());
	}

	@Test
	@DisplayName("IndividualGeneratorJBSE.generateRandomIndividual(chromosome) correctly reorders numeric vs. symbolic reference clauses")
	public void testRandomIndividualWithChromosome2() throws Exception {
		//builds a path condition
		final ReferenceSymbolic R0 = (ReferenceSymbolic) this.symbolFactory.createSymbolLocalVariable(unknown(), "L" + METHOD_CLASS_NAME + ";", "foo");
		final PrimitiveSymbolic R0_a = (PrimitiveSymbolic) this.symbolFactory.createSymbolMemberField("Z", R0, "a", METHOD_CLASS_NAME);
		final PrimitiveSymbolic R0_b = (PrimitiveSymbolic) this.symbolFactory.createSymbolMemberField("C", R0, "b", METHOD_CLASS_NAME);
		s.assumeExpands(R0, this.hier.loadCreateClass(2, METHOD_CLASS_NAME, true));
		s.assume(R0_a.widen(Type.INT).eq(this.calc.valInt(0)));
		s.assume(R0_b.widen(Type.INT).ne(this.calc.valInt(0)));
		final List<Clause> pathCondition = s.getPathCondition();
		
		//builds an individual whose chromosome reorders the clauses in the path condition
		final ArrayList<GeneJBSE> chromosomeStart = new ArrayList<>();
		chromosomeStart.add(new GeneJBSE(pathCondition.get(1)));
		chromosomeStart.add(new GeneJBSE(pathCondition.get(2)));
		chromosomeStart.add(new GeneJBSE(pathCondition.get(0)));
		final IndividualJBSE individual = this.ig.generateRandomIndividual(chromosomeStart);
		
		//the individual's chromosome must have as first clause the same 
		//first clause in the path condition
		final List<GeneJBSE> chromosomeEnd = individual.getChromosome();
		assertEquals(pathCondition.get(0), chromosomeEnd.get(0).getClause());
	}
}
