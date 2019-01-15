package gasp.ga;

public class FitnessEvaluationException extends Exception {

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
