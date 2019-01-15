package gasp.se;

import java.util.List;

import gasp.ga.Constraint;

public class Model {
	
	private List<Constraint> constraints;

	public Model(List<Constraint> constraintSet) {
		this.constraints = constraintSet;
	}

	@Override
	public String toString() {
		return "Model [constraints=" + constraints + "]";
	}

}
