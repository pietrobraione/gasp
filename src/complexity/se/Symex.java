package complexity.se;

import java.util.List;

public interface Symex {
	
	//public RandomWalkSymbolicExecutor rngWalkSymbolExe;
	//public SymbolicExecutor symbolicExe;
	//public PcClause pcClause;
	
	public List<Constraint> randomWalkSymbolicExecution(List<Constraint> precondition);
	public List<Constraint> randomWalkSymbolicExecution();

	public List<Constraint> formulaSlicing(List<Constraint> formula, Constraint target);	
	
	public static Symex makeEngine() {
		//return new SymexStub();
		return new SymexJBSE();
	}
	public int getInstructionCount();
	
	public Constraint mkAnd(List<Constraint> refs);
	
	public boolean quickCheck(Constraint c);
	
	public Constraint boolRef(Constraint c1, Constraint c2);
	
}
