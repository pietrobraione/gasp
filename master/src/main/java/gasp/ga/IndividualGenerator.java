package gasp.ga;

import java.util.Collections;
import java.util.List;

public interface IndividualGenerator<T extends Gene<T>, U extends Individual<T>> {
	U generateRandomIndividual(long seed, List<T> chromosome) throws FoundWorstIndividualException;
	
	default U generateRandomIndividual(long seed) throws FoundWorstIndividualException {
		return generateRandomIndividual(seed, Collections.emptyList());
	}
}
