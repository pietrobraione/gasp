package gasp.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class IndividualGeneratorStub implements IndividualGenerator<GeneStub, Individual<GeneStub>> {
	private final Random random;
	
	public IndividualGeneratorStub(Random random) {
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}

		this.random = random;
	}
	
	@Override
	public Individual<GeneStub> generateRandomIndividual(List<GeneStub> precondition) {
		final List<GeneStub> chromosome = new ArrayList<>(precondition);
		chromosome.addAll(generateRandomChromosome());
		return new Individual<>(chromosome, chromosome.size());
	}
	
	@Override
	public Individual<GeneStub> generateRandomIndividual() {
		return generateRandomIndividual(Collections.emptyList());
	}

	private List<GeneStub> generateRandomChromosome() {
		final List<GeneStub> retVal = new ArrayList<>();
		final int n = 1 + (int) (this.random.nextDouble() * 30);
		for (int i = 0; i < n; ++i) {
			retVal.add(new GeneStub());
		}
		return retVal;
	}
}
