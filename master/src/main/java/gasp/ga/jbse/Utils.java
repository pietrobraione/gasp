package gasp.ga.jbse;

import java.nio.file.Path;
import java.util.ArrayList;

import jbse.common.exc.InvalidInputException;
import jbse.dec.DecisionProcedureAlgorithms;
import jbse.dec.DecisionProcedureAlwSat;
import jbse.dec.DecisionProcedureClassInit;
import jbse.dec.DecisionProcedureLICS;
import jbse.dec.DecisionProcedureSMTLIB2_AUFNIRA;
import jbse.dec.exc.DecisionException;
import jbse.rewr.CalculatorRewriting;
import jbse.rewr.RewriterExpressionOrConversionOnSimplex;
import jbse.rewr.RewriterFunctionApplicationOnSimplex;
import jbse.rewr.RewriterNegationElimination;
import jbse.rewr.RewriterZeroUnit;
import jbse.rules.ClassInitRulesRepo;
import jbse.rules.LICSRulesRepo;

public interface Utils {
	static final String SWITCH_CHAR = System.getProperty("os.name").toLowerCase().contains("windows") ? "/" : "-";
	
	static CalculatorRewriting makeCalculator() {
		final CalculatorRewriting calc = new CalculatorRewriting();
        calc.addRewriter(new RewriterExpressionOrConversionOnSimplex());
        calc.addRewriter(new RewriterFunctionApplicationOnSimplex());
        calc.addRewriter(new RewriterZeroUnit());
        calc.addRewriter(new RewriterNegationElimination());
		return calc;
	}
	
	static DecisionProcedureAlgorithms makeDecisionProcedure(CalculatorRewriting calc, Path z3Path) throws DecisionException {
		final ArrayList<String> z3CommandLine  = new ArrayList<>();
		z3CommandLine.add(z3Path.toString());
		z3CommandLine.add(SWITCH_CHAR + "smt2");
		z3CommandLine.add(SWITCH_CHAR + "in");
		z3CommandLine.add(SWITCH_CHAR + "t:10");
		try {
			return
				new DecisionProcedureAlgorithms(
					new DecisionProcedureClassInit( //useless?
						new DecisionProcedureLICS(  //useless?
							new DecisionProcedureSMTLIB2_AUFNIRA(
								new DecisionProcedureAlwSat(calc), z3CommandLine), 
							new LICSRulesRepo()), 
						new ClassInitRulesRepo()));
		} catch (InvalidInputException e) {
			//this should never happen
			throw new AssertionError("Failed while creating the decision procedure.", e);
		}
	}
}
