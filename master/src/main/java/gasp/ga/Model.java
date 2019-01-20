package gasp.ga;

import java.util.List;

public class Model<T extends Gene<T>> {
	private List<T> chromosome;

	public Model(Individual<T> individual) {
		this.chromosome = individual.getChromosome();
	}

	@Override
	public String toString() {
		return "Model [chromosome=" + this.chromosome + "]";
	}
}
