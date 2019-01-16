package gasp.ga;

public abstract class Constraint {
	@Override
	public abstract String toString(); 
	
	public abstract Constraint not();

}
