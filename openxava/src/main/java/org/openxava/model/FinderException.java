package org.openxava.model;

/**
 * Exception thrown when a finder operation on a model object fails. <p>
 *
 * @author Javier Paniza
 */
public class FinderException extends Exception {

	public FinderException() {
		super();
	}

	public FinderException(String message) {
		super(message);
	}

	public FinderException(String message, Throwable cause) {
		super(message, cause);
	}

	public FinderException(Throwable cause) {
		super(cause);
	}

}
