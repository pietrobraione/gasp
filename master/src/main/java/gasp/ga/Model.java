package gasp.ga;

import java.util.List;

public abstract class Model<T extends Gene<T>> {
	private List<T> chromosome;

	public Model(Individual<T> individual) {
		this.chromosome = individual.getChromosome();
	}
	
	public List<T> getChromosome() {
		return this.chromosome;
	}
}
