package gasp.ga.modelGenerator;

import gasp.ga.Model;
import gasp.ga.individualGenerator.GeneJBSE;
import gasp.ga.individualGenerator.IndividualJBSE;

public final class ModelJBSE extends Model<GeneJBSE> {
	
	private final String pathIdentifier;
	
	public ModelJBSE(IndividualJBSE individual) {
		super(individual);
		this.pathIdentifier = individual.getPathIdentifier();
	}
	
	public String getPathIdentifier() {
		return this.pathIdentifier;
	}
	
	@Override
	public String toString() {
		return "Model [pathIdentifier=" + this.pathIdentifier + "]";
	}
}
