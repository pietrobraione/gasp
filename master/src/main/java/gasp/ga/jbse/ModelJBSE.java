package gasp.ga.jbse;

import gasp.ga.Model;

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
