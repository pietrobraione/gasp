package gasp.ga;

public final class FoundWorstIndividualException extends Exception {
	private final Individual<?> individual;

	public FoundWorstIndividualException(Individual<?> individual) {
		if (individual == null) {
			throw new IllegalArgumentException("The worst individual cannot be null.");
		}
		
		this.individual = individual;
	}
	
	public Individual<?> getIndividual() {
		return this.individual;
	}

	private static final long serialVersionUID = 4625006188543494883L;

}
