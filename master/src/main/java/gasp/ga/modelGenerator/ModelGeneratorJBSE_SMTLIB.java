package gasp.ga.modelGenerator;

import static gasp.ga.jbse.Utils.makeCalculator;
import static gasp.ga.jbse.Utils.makeDecisionProcedure;

import java.nio.file.Path;
import java.util.Map;

import gasp.ga.ModelGenerator;
import gasp.ga.individualGenerator.GeneJBSE;
import gasp.ga.individualGenerator.IndividualJBSE;
import jbse.common.exc.InvalidInputException;
import jbse.dec.DecisionProcedureAlgorithms;
import jbse.dec.exc.DecisionException;
import jbse.mem.Clause;
import jbse.mem.exc.ContradictionException;
import jbse.rewr.CalculatorRewriting;
import jbse.val.PrimitiveSymbolic;
import jbse.val.Simplex;

public final class ModelGeneratorJBSE_SMTLIB implements ModelGenerator<GeneJBSE, IndividualJBSE, ModelJBSE_SMTLIB> {
	private final Path z3Path;
	
	public ModelGeneratorJBSE_SMTLIB(Path z3Path) {
		if (z3Path == null) {
			throw new IllegalArgumentException("Z3 path cannot be null");
		}
		
		this.z3Path = z3Path;
	}
	
	@Override
	public ModelJBSE_SMTLIB generateModel(IndividualJBSE individual) {
		final CalculatorRewriting calc = makeCalculator();
		try (DecisionProcedureAlgorithms dec = makeDecisionProcedure(calc, this.z3Path)) {
			dec.addAssumptions(individual.getChromosome().stream().map(x -> x.getClause()).toArray(Clause[]::new));
			final Map<PrimitiveSymbolic, Simplex> model = dec.getModel();
			return new ModelJBSE_SMTLIB(individual, model);
		} catch (DecisionException e) {
			//if this happens, we return null
			return null; //TODO report the issue
		} catch (InvalidInputException | ContradictionException e) {
			//this should never happen
			throw new AssertionError(e);
		}
	}
}
