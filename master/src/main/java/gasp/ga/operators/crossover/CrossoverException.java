package gasp.ga.operators.crossover;

public class CrossoverException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5668800634733442518L;

	public CrossoverException() {
	}

	public CrossoverException(String message) {
		super(message);
	}

	public CrossoverException(Throwable cause) {
		super(cause);
	}

	public CrossoverException(String message, Throwable cause) {
		super(message, cause);
	}

	public CrossoverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
