package gasp.ga;

public final class ModelGeneratorStub implements ModelGenerator<GeneStub, Individual<GeneStub>, Model<GeneStub>> {

	@Override
	public Model<GeneStub> generateModel(Individual<GeneStub> individual) {
		return new Model<GeneStub>(individual) { };
	}
}
