package gasp.ga.fitness;

public class FitnessEvaluationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1984646626257298286L;

	public FitnessEvaluationException() {
	}

	public FitnessEvaluationException(String message) {
		super(message);
	}

	public FitnessEvaluationException(Throwable cause) {
		super(cause);
	}

	public FitnessEvaluationException(String message, Throwable cause) {
		super(message, cause);
	}

	public FitnessEvaluationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
