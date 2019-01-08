package complexity.ga.operators.mutation;

import org.junit.jupiter.api.Test;

import complexity.ga.Individual;
import complexity.se.Constraint;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Mutation Function test suite")
public class TestMutation {

	public List<List<Constraint>> constraints() {
		Individual ind1 = Individual.randomIndividual();
		Individual ind2 = ind1.cloneIndividual();
		List<Constraint> constraints = ind1.getConstraintSet();
		List<Constraint> constraintsM = ind2.getConstraintSet();
		MutationFunction.mutationBis(constraintsM);
		List<List<Constraint>> list = new ArrayList<List<Constraint>>();
		list.add(constraints);
		list.add(constraintsM);
		return list;
	}
	
	@Test
	@DisplayName("test")
	public void testMutationBis1() {
		assertNotEquals(constraints().get(1), null);
	}

	@Test
	@DisplayName("test")
	public void testMutationBis2() {
		assertFalse(constraints().get(1).isEmpty());
	}
	
	@Test
	@DisplayName("test")
	public void testMutationBis3() {
		assertNotEquals(constraints().get(0), constraints().get(1));
	}
	
}
