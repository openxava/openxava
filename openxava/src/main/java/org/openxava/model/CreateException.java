package org.openxava.model;

/**
 * Exception thrown when a model object cannot be created. <p>
 *
 * @author Javier Paniza
 */
public class CreateException extends Exception {

	public CreateException() {
		super();
	}

	public CreateException(String message) {
		super(message);
	}

	public CreateException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateException(Throwable cause) {
		super(cause);
	}

}
