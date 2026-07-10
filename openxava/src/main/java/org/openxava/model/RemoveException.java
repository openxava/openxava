package org.openxava.model;

/**
 * Exception thrown when a model object cannot be removed. <p>
 *
 * @author Javier Paniza
 */
public class RemoveException extends Exception {

	public RemoveException() {
		super();
	}

	public RemoveException(String message) {
		super(message);
	}

	public RemoveException(String message, Throwable cause) {
		super(message, cause);
	}

	public RemoveException(Throwable cause) {
		super(cause);
	}

}
