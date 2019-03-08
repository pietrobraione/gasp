package gasp.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class IndividualGeneratorStub implements IndividualGenerator<GeneStub, Individual<GeneStub>> {
	@Override
	public Individual<GeneStub> generateRandomIndividual(long seed, List<GeneStub> precondition) {
		final List<GeneStub> chromosome = new ArrayList<>(precondition);
		chromosome.addAll(generateRandomChromosome(seed));
		return new Individual<>(chromosome, chromosome.size());
	}
	
	@Override
	public Individual<GeneStub> generateRandomIndividual(long seed) {
		return generateRandomIndividual(seed, Collections.emptyList());
	}

	private List<GeneStub> generateRandomChromosome(long seed) {
		final List<GeneStub> retVal = new ArrayList<>();
		final Random random = new Random(seed);
		final int n = 1 + (int) (random.nextDouble() * 30);
		for (int i = 0; i < n; ++i) {
			retVal.add(new GeneStub());
		}
		return retVal;
	}
}
