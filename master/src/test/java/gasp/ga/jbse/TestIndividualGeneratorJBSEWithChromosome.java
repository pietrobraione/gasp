package gasp.ga.jbse;

import static jbse.bc.Signatures.JAVA_CLONEABLE;
import static jbse.bc.Signatures.JAVA_OBJECT;
import static jbse.bc.Signatures.JAVA_SERIALIZABLE;
import static jbse.common.Type.ARRAYOF;
import static jbse.common.Type.CHAR;
import static jbse.val.HistoryPoint.unknown;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static settings.Settings.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gasp.ga.individualGenerator.GeneJBSE;
import gasp.ga.individualGenerator.IndividualGeneratorJBSE;
import gasp.ga.individualGenerator.IndividualJBSE;
import jbse.bc.ClassFileFactoryJavassist;
import jbse.bc.ClassHierarchy;
import jbse.bc.Classpath;
import jbse.bc.exc.BadClassFileVersionException;
import jbse.bc.exc.ClassFileIllFormedException;
import jbse.bc.exc.ClassFileNotAccessibleException;
import jbse.bc.exc.ClassFileNotFoundException;
import jbse.bc.exc.IncompatibleClassFileException;
import jbse.bc.exc.InvalidClassFileFactoryClassException;
import jbse.bc.exc.RenameUnsupportedException;
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
	private static final long SEED = 0;
	private static final long MAX_FITNESS = 1_000_000;
	private static final String METHOD_CLASS_NAME = "smalldemos/ifx/IfExample";
	private static final String METHOD_DESCRIPTOR = "(I)V";
	private static final String METHOD_NAME = "m";
	
	private IndividualGeneratorJBSE ig;
	private CalculatorRewriting calc;
	private SymbolFactory symbolFactory;
	private State s;
	private ClassHierarchy hier;
	
	@BeforeEach
	private void beforeEach() throws InvalidInputException, InvalidClassFileFactoryClassException, IOException, ClassFileNotFoundException, ClassFileIllFormedException, ClassFileNotAccessibleException, IncompatibleClassFileException, BadClassFileVersionException, WrongClassNameException, RenameUnsupportedException {
		this.ig = new IndividualGeneratorJBSE(MAX_FITNESS, CLASSPATH, JBSE_PATH, Z3_PATH, METHOD_CLASS_NAME, METHOD_DESCRIPTOR, METHOD_NAME);
		this.calc = new CalculatorRewriting();
		this.symbolFactory = new SymbolFactory();
		this.s = new State(true, unknown(), 10, 10, new Classpath(JBSE_PATH, Paths.get(System.getProperty("java.home")), Collections.emptyList(), CLASSPATH), ClassFileFactoryJavassist.class, Collections.emptyMap(), Collections.emptyMap(), this.symbolFactory);
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
		final ReferenceSymbolic R0 = (ReferenceSymbolic) this.symbolFactory.createSymbolLocalVariableReference(unknown(), "L" + METHOD_CLASS_NAME + ";", "L" + METHOD_CLASS_NAME + ";", "foo");
		final PrimitiveSymbolic R0_a = (PrimitiveSymbolic) this.symbolFactory.createSymbolMemberFieldPrimitive("Z", R0, "a", METHOD_CLASS_NAME);
		final PrimitiveSymbolic R0_b = (PrimitiveSymbolic) this.symbolFactory.createSymbolMemberFieldPrimitive("C", R0, "b", METHOD_CLASS_NAME);
		s.assumeExpands(this.calc, R0, this.hier.loadCreateClass(2, METHOD_CLASS_NAME, true));
		s.assume(this.calc.push(R0_a).widen(Type.INT).eq(this.calc.valInt(0)).pop());
		s.assume(this.calc.push(R0_b).widen(Type.INT).ne(this.calc.valInt(0)).pop());
		final List<Clause> pathCondition = s.getPathCondition();
		
		//builds an individual whose chromosome reorders the clauses in the path condition
		final ArrayList<GeneJBSE> chromosomeStart = new ArrayList<>();
		chromosomeStart.add(new GeneJBSE(pathCondition.get(1), this.calc));
		chromosomeStart.add(new GeneJBSE(pathCondition.get(2), this.calc));
		chromosomeStart.add(new GeneJBSE(pathCondition.get(0), this.calc));
		final IndividualJBSE individual = this.ig.generateRandomIndividual(SEED, chromosomeStart);
		
		//the individual's chromosome must have as first clause the same 
		//first clause in the path condition
		final List<GeneJBSE> chromosomeEnd = individual.getChromosome();
		assertEquals(pathCondition.get(0), chromosomeEnd.get(0).getClause());
	}

	@Test
	@DisplayName("IndividualGeneratorJBSE.generateRandomIndividual(chromosome) correctly reorders numeric vs. symbolic reference clauses")
	public void testRandomIndividualWithChromosome2() throws Exception {
		//builds a path condition
		final ReferenceSymbolic R0 = (ReferenceSymbolic) this.symbolFactory.createSymbolLocalVariableReference(unknown(), "L" + METHOD_CLASS_NAME + ";", "L" + METHOD_CLASS_NAME + ";", "foo");
		final PrimitiveSymbolic R0_a = (PrimitiveSymbolic) this.symbolFactory.createSymbolMemberFieldPrimitive("Z", R0, "a", METHOD_CLASS_NAME);
		final PrimitiveSymbolic R0_b = (PrimitiveSymbolic) this.symbolFactory.createSymbolMemberFieldPrimitive("C", R0, "b", METHOD_CLASS_NAME);
		s.assumeExpands(this.calc, R0, this.hier.loadCreateClass(2, METHOD_CLASS_NAME, true));
		s.assume(this.calc.push(R0_a).widen(Type.INT).eq(this.calc.valInt(0)).pop());
		s.assume(this.calc.push(R0_b).widen(Type.INT).ne(this.calc.valInt(0)).pop());
		final List<Clause> pathCondition = s.getPathCondition();
		
		//builds an individual whose chromosome reorders the clauses in the path condition
		final ArrayList<GeneJBSE> chromosomeStart = new ArrayList<>();
		chromosomeStart.add(new GeneJBSE(pathCondition.get(1), this.calc));
		chromosomeStart.add(new GeneJBSE(pathCondition.get(2), this.calc));
		chromosomeStart.add(new GeneJBSE(pathCondition.get(0), this.calc));
		final IndividualJBSE individual = this.ig.generateRandomIndividual(SEED, chromosomeStart);
		
		//the individual's chromosome must have as first clause the same 
		//first clause in the path condition
		final List<GeneJBSE> chromosomeEnd = individual.getChromosome();
		assertEquals(pathCondition.get(0), chromosomeEnd.get(0).getClause());
	}
}
