package gasp.ga;

public abstract class Gene<T extends Gene<T>> {
	@Override
	public abstract String toString(); 
	
	public abstract T not();

}
