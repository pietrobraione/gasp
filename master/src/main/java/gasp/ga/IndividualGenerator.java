package gasp.ga;

import java.util.Collections;
import java.util.List;

public interface IndividualGenerator<T extends Gene<T>, U extends Individual<T>> {
	public U generateRandomIndividual(List<T> chromosome) throws FoundWorstIndividualException;
	
	default U generateRandomIndividual() throws FoundWorstIndividualException {
		return generateRandomIndividual(Collections.emptyList());
	}
}
