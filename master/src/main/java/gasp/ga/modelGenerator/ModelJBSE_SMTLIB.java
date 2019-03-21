package gasp.ga.modelGenerator;

import java.util.Map;

import gasp.ga.Model;
import gasp.ga.individualGenerator.GeneJBSE;
import gasp.ga.individualGenerator.IndividualJBSE;
import jbse.val.PrimitiveSymbolic;
import jbse.val.Simplex;

public final class ModelJBSE_SMTLIB extends Model<GeneJBSE> {
	private final Map<PrimitiveSymbolic, Simplex> model;
	
	public ModelJBSE_SMTLIB(IndividualJBSE individual, Map<PrimitiveSymbolic, Simplex> model) {
		super(individual);
		this.model = model;
	}
	
	public Map<PrimitiveSymbolic, Simplex> getSMTLIBModel() {
		return this.model;
	}
	
	@Override
	public String toString() {
		final StringBuilder retVal = new StringBuilder();
		retVal.append("Model [");
		boolean first = true;
		for (Map.Entry<PrimitiveSymbolic, Simplex> e : this.model.entrySet()) {
			if (first) {
				first = false;
			} else {
				retVal.append(", ");
			}
			retVal.append(e.getKey().asOriginString());
			retVal.append("=");
			retVal.append(e.getValue().toString());
		}
		retVal.append("]");
		return retVal.toString();
	}
}
