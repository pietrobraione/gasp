package gasp.ga;

import java.util.Collections;
import java.util.List;

public interface IndividualGenerator<T extends Gene<T>, U extends Individual<T>> {
	public U generateRandomIndividual(List<T> chromosome);
	
	default U generateRandomIndividual() {
		return generateRandomIndividual(Collections.emptyList());
	}
}
