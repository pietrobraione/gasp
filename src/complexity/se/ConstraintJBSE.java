package complexity.se;

import java.util.List;

import jbse.mem.Clause;
import jbse.mem.ClauseAssume;
import jbse.val.exc.InvalidTypeException;

public class ConstraintJBSE extends Constraint {
	
	private Clause clause;

	public ConstraintJBSE(Clause clause) {
		super();
		this.clause = clause;
	}
	
	public Clause getClause() {
		return this.clause;
	}
	
	@Override
	public String toString() {
		return this.clause.toString();
	}

	@Override
	public Constraint mkNot() {
		try {
			//TODO works implicitly only with numeric constraints
			return new ConstraintJBSE(new ClauseAssume(((ClauseAssume) this.clause).getCondition().neg()));
		} catch (InvalidTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
	}

	@Override
	public boolean isInconsistent(List<Constraint> slice) {
		return false;
	}
	

}
