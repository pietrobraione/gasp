package gasp.ga;

public class ConstraintStub extends Constraint {
	
	private static final String chars = "abcdefghijklmnopkrstuvwxyzabcdefghijklmnopkrstuvwxyzabcdefghijklmnopkrstuvwxyzabcdefghijklmnopkrstuvwxyz";
	private static int nextChar = 0;

	private final String theConstraint; 
	
	public ConstraintStub() {
		super();
		theConstraint = chars.charAt(nextChar++ % chars.length() ) + " > 0";
	}

	public ConstraintStub(String constraint) {
		theConstraint = constraint;
	}

	@Override
	public String toString() {
		return theConstraint;
	}

	@Override
	public Constraint not() {
		return new ConstraintStub("not (" + this.theConstraint + ")");
	}


	//TODO equals, hashCode
}
