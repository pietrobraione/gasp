package gasp.se;

public abstract class Constraint {
	
/*	public static final Constraint TRUE = new ConstraintStub("TRUE");
	public static final Constraint FALSE = new ConstraintStub("FALSE");*/
	
	@Override
	public abstract String toString(); 
	
	public abstract Constraint mkNot();

}
