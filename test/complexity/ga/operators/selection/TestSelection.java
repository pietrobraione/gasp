package complexity.ga.operators.selection;

import org.junit.jupiter.api.Test;

import complexity.ga.Individual;
import complexity.utils.Config;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Selection test suite")
public class TestSelection {
	
	public ArrayList<Individual> parents() {
		ArrayList<Individual> population = new ArrayList<>();
		for (int i = 0; i < Config.populationSize; i++) {
			population.add(Individual.randomIndividual());
		}
		ArrayList<Individual> parents = Config.selectionFunction.selection(population, (int) Math.round(Config.populationSize / 2));
		return parents;
	}

	
	//TestSuite -> randomIndividual
	@Test
	@DisplayName("selection returns parents different from each other")
	public void testSelection1() {
		for(int i = 0, j = 1; i < parents().size(); i++, j++) {
			assertNotEquals(parents().get(i), parents().get(j));
			i++;
			j++;
		}
	}
	
	@Test
	@DisplayName("selection returns pairs different from each other")
	public void testSelection2() {
		for(int i = 0; i < parents().size(); i++) {
			Individual ind = parents().get(i).cloneIndividual();
			parents().remove(parents().get(i));
			assertFalse(parents().contains(ind));
			parents().add(ind);
		}
	}
	
}
