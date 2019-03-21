package gasp.ga.modelGenerator;

import gasp.ga.ModelGenerator;
import gasp.ga.individualGenerator.GeneJBSE;
import gasp.ga.individualGenerator.IndividualJBSE;

public final class ModelGeneratorJBSE implements ModelGenerator<GeneJBSE, IndividualJBSE, ModelJBSE> {
	@Override
	public ModelJBSE generateModel(IndividualJBSE individual) {
		//TODO launch EvoSuite and generate the test
		return new ModelJBSE(individual);
	}
}
