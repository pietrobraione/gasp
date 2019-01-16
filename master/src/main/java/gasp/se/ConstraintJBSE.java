package gasp.se;

import gasp.ga.Constraint;
import jbse.common.exc.InvalidInputException;
import jbse.mem.Clause;
import jbse.mem.ClauseAssume;
import jbse.mem.ClauseAssumeClassInitialized;
import jbse.mem.ClauseAssumeClassNotInitialized;
import jbse.mem.ClauseAssumeReferenceSymbolic;
import jbse.val.exc.InvalidTypeException;

public class ConstraintJBSE extends Constraint {
	private final Clause clause;
	private final boolean negated;
	private final int hashCode;

	public ConstraintJBSE(Clause clause, boolean negated) {
		if (clause == null) {
			throw new NullPointerException("Cannot build a ConstraintJBSE from a null Clause.");
		}
		this.clause = clause;
		this.negated = negated;
		final int prime = 31;
		int result = 1;
		result = prime * result + this.clause.hashCode();
		result = prime * result + (this.negated ? 1231 : 1237);
		this.hashCode = result;
	}
	
	public ConstraintJBSE(Clause clause) {
		this(clause, false);
	}
	
	public Clause getClause() {
		return this.clause;
	}
	
	public boolean isNegated() {
		return this.negated;
	}
	
	@Override
	public String toString() {
		return this.clause.toString();
	}

	@Override
	public Constraint not() {
		try {
			if (this.clause instanceof ClauseAssume) {
				return new ConstraintJBSE(new ClauseAssume(((ClauseAssume) this.clause).getCondition().not()));
			} else if (this.clause instanceof ClauseAssumeReferenceSymbolic) {
				return new ConstraintJBSE(this.clause, !this.negated);
			} else if (this.clause instanceof ClauseAssumeClassInitialized) {
				return new ConstraintJBSE(new ClauseAssumeClassNotInitialized(((ClauseAssumeClassInitialized) this.clause).getClassFile()));
			} else { //this.clause instanceof ClauseAssumeClassNotInitialized
				return new ConstraintJBSE(new ClauseAssumeClassInitialized(((ClauseAssumeClassNotInitialized) this.clause).getClassFile(), null));
			}
		} catch (InvalidTypeException | InvalidInputException e) {
			throw new RuntimeException(e);
		} 
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ConstraintJBSE other = (ConstraintJBSE) obj;
		if (!this.clause.equals(other.clause)) {
			return false;
		}
		if (this.negated != other.negated) {
			return false;
		}
		return true;
	}

}
