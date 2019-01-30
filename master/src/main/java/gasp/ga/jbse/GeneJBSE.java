package gasp.ga.jbse;

import gasp.ga.Gene;
import jbse.common.exc.InvalidInputException;
import jbse.mem.Clause;
import jbse.mem.ClauseAssume;
import jbse.mem.ClauseAssumeAliases;
import jbse.mem.ClauseAssumeClassInitialized;
import jbse.mem.ClauseAssumeClassNotInitialized;
import jbse.mem.ClauseAssumeExpands;
import jbse.mem.ClauseAssumeNull;
import jbse.mem.ClauseAssumeReferenceSymbolic;
import jbse.val.Any;
import jbse.val.Expression;
import jbse.val.NarrowingConversion;
import jbse.val.Operator;
import jbse.val.Primitive;
import jbse.val.PrimitiveSymbolicApply;
import jbse.val.PrimitiveSymbolicAtomic;
import jbse.val.PrimitiveVisitor;
import jbse.val.Simplex;
import jbse.val.Term;
import jbse.val.WideningConversion;
import jbse.val.exc.InvalidTypeException;

public class GeneJBSE extends Gene<GeneJBSE> {
	private final Clause clause;
	private final boolean negated;
	private final int hashCode;
	private final String toString;

	public GeneJBSE(Clause clause, boolean negated) {
		if (clause == null) {
			throw new NullPointerException("Cannot build a ConstraintJBSE from a null Clause.");
		}
		this.clause = clause;
		this.negated = negated;
		
		//hashCode
		final int prime = 31;
		int result = 1;
		result = prime * result + this.clause.hashCode();
		result = prime * result + (this.negated ? 1231 : 1237);
		this.hashCode = result;
		
		//toString
		if (this.clause instanceof ClauseAssumeAliases) {
			final ClauseAssumeAliases clauseAliases = (ClauseAssumeAliases) this.clause;
			this.toString = clauseAliases.getReference().asOriginString() + (this.negated ? " does not alias " : " aliases ") + clauseAliases.getObjekt().getOrigin().asOriginString(); 
		} else if (this.clause instanceof ClauseAssumeExpands) {
			final ClauseAssumeExpands clauseExpands = (ClauseAssumeExpands) this.clause;
			this.toString = clauseExpands.getReference().asOriginString() + (this.negated ? " has not class " : " has class ") + clauseExpands.getObjekt().getType().getClassName(); 
		} else if (this.clause instanceof ClauseAssumeNull) {
			final ClauseAssumeNull clauseNull = (ClauseAssumeNull) this.clause;
			this.toString = clauseNull.getReference().asOriginString() + (this.negated ? " not null" : " null"); 
		} else if (this.clause instanceof ClauseAssume) {
			try {
				final Expression condition = (this.negated ? 
											  (Expression) ((ClauseAssume) this.clause).getCondition().not() :
										      (Expression) ((ClauseAssume) this.clause).getCondition());
				final ConditionStringifier cs = new ConditionStringifier();
				condition.accept(cs);
				this.toString = cs.result;
			} catch (Exception e) {
				//this should never happen
				throw new AssertionError("Unreachable invalid expression type while negating a clause.");
			}
		} else {
			this.toString = "-";
		}
	}
	
	public GeneJBSE(Clause clause) {
		this(clause, false);
	}
	
	public Clause getClause() {
		return this.clause;
	}
	
	public boolean isNegated() {
		return this.negated;
	}

	@Override
	public GeneJBSE not() {
		try {
			if (this.clause instanceof ClauseAssume) {
				return new GeneJBSE(new ClauseAssume(((ClauseAssume) this.clause).getCondition().not()));
			} else if (this.clause instanceof ClauseAssumeReferenceSymbolic) {
				return new GeneJBSE(this.clause, !this.negated);
			} else if (this.clause instanceof ClauseAssumeClassInitialized) {
				return new GeneJBSE(new ClauseAssumeClassNotInitialized(((ClauseAssumeClassInitialized) this.clause).getClassFile()));
			} else { //this.clause instanceof ClauseAssumeClassNotInitialized
				return new GeneJBSE(new ClauseAssumeClassInitialized(((ClauseAssumeClassNotInitialized) this.clause).getClassFile(), null));
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
		final GeneJBSE other = (GeneJBSE) obj;
		if (!this.clause.equals(other.clause)) {
			return false;
		}
		if (this.negated != other.negated) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return this.toString;
	}

	private class ConditionStringifier implements PrimitiveVisitor {
		String result;

		@Override
		public void visitAny(Any x) throws Exception {
			this.result = "*";
		}

		@Override
		public void visitExpression(Expression e) throws Exception {
			final Operator operator = e.getOperator();
			if (e.isUnary()) {
				e.getOperand().accept(this);
				this.result =  operator.toString() + " " + this.result;
			} else {
				final Primitive firstOperand = e.getFirstOperand();
				firstOperand.accept(this);
				final String firstOperandString = this.result;
				final Primitive secondOperand = e.getSecondOperand();
				secondOperand.accept(this);
				final String secondOperandString = this.result;
				if (firstOperand instanceof Expression && ((Expression) firstOperand).getOperator().precedence() < operator.precedence()) {
					this.result = "(" + firstOperandString + ")";
				} else {
					this.result = firstOperandString;
				}
				this.result += " " + e.getOperator().toString() + " ";
				if (secondOperand instanceof Expression && ((Expression) secondOperand).getOperator().precedence() < operator.precedence()) {
					this.result += "(" + secondOperandString + ")";
				} else {
					this.result += secondOperandString;
				}
			}
		}
		
		@Override
		public void visitPrimitiveSymbolicApply(PrimitiveSymbolicApply x) throws Exception {
			this.result = x.asOriginString();
		}

		@Override
		public void visitPrimitiveSymbolicAtomic(PrimitiveSymbolicAtomic s) throws Exception {
			this.result = s.asOriginString();
			
		}

		@Override
		public void visitSimplex(Simplex x) throws Exception {
			this.result = x.toString();
			
		}

		@Override
		public void visitTerm(Term x) throws Exception {
			this.result = x.asOriginString();
			
		}

		@Override
		public void visitNarrowingConversion(NarrowingConversion x) throws Exception {
			x.getArg().accept(this);
	        this.result = "NARROW-"+ x.getType() + "(" + this.result + ")";
		}

		@Override
		public void visitWideningConversion(WideningConversion x) throws Exception {
			x.getArg().accept(this);
	        this.result = "WIDEN-"+ x.getType() + "(" + this.result + ")";
		}
	}
}
