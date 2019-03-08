package gasp.ga.jbse;

import gasp.ga.ModelGenerator;

public final class ModelGeneratorJBSE implements ModelGenerator<GeneJBSE, IndividualJBSE, ModelJBSE> {
	@Override
	public ModelJBSE generateModel(IndividualJBSE individual) {
		//TODO launch EvoSuite and generate the test
		return new ModelJBSE(individual);
	}
}
