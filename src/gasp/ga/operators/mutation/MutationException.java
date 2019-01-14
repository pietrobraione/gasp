package gasp.ga.operators.mutation;

public class MutationException extends Exception {

	public MutationException() {
	}

	public MutationException(String message) {
		super(message);
	}

	public MutationException(Throwable cause) {
		super(cause);
	}

	public MutationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MutationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
