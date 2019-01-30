package gasp.ga;

import java.util.Collections;
import java.util.List;

public interface IndividualGenerator<T extends Gene<T>> {
	public Individual<T> generateRandomIndividual(List<T> chromosome);
	
	default Individual<T> generateRandomIndividual() {
		return generateRandomIndividual(Collections.emptyList());
	}
}
