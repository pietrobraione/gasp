package complexity.se;

import java.util.ArrayList;
import java.util.List;

public class SymexStub implements Symex {
	
	private int instructionCount = 0;

	@Override
	public List<Constraint> randomWalkSymbolicExecution(List<Constraint> precondition) {
		List<Constraint> fakePathCondition = new ArrayList<>(precondition);
		
		fakePathCondition.addAll(randomWalkSymbolicExecution());
		
		instructionCount = fakePathCondition.size();
		
		return fakePathCondition;
	}

	@Override
	public List<Constraint> randomWalkSymbolicExecution() {
		List<Constraint> fakePathCondition = new ArrayList<>();
		
		int n = 1 + (int) (Math.random() * 30);
		for (int i = 0; i < n; i++) {
			fakePathCondition.add(new ConstraintStub());
		}

		instructionCount = fakePathCondition.size();

		return fakePathCondition;
	}

	@Override
	public List<Constraint> formulaSlicing(List<Constraint> formula, Constraint target) {
		return formula;
	}


	@Override
	public int getInstructionCount() {
		return instructionCount;
	}

	@Override
	public Constraint mkAnd(List<Constraint> refs) {
		return refs.get(0);
	}

	@Override
	public boolean quickCheck(Constraint c) {
		return true;
	}

	@Override
	public Constraint boolRef(Constraint c1, Constraint c2) {
		return c1;
	}
	
}
