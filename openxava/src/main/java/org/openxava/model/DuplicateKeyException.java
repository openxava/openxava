package org.openxava.model;

/**
 * Exception thrown when an attempt to create a model object
 * with an already existing key is made. <p>
 *
 * @author Javier Paniza
 */
public class DuplicateKeyException extends CreateException {

	public DuplicateKeyException() {
		super();
	}

	public DuplicateKeyException(String message) {
		super(message);
	}

	public DuplicateKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateKeyException(Throwable cause) {
		super(cause);
	}

}
