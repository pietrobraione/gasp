package gasp.se;

import java.util.List;

import gasp.ga.Constraint;

public interface Symex {
	public List<Constraint> randomWalkSymbolicExecution(List<Constraint> precondition);
	public List<Constraint> randomWalkSymbolicExecution();
	public List<Constraint> formulaSlicing(List<Constraint> formula, Constraint target);		
	public int getInstructionCount();
	public boolean quickCheck(Constraint c);
	public Constraint boolRef(Constraint c1, Constraint c2);
	
}
