package org.openxava.model;

/**
 * Exception thrown when a model object is not found. <p>
 *
 * @author Javier Paniza
 */
public class ObjectNotFoundException extends FinderException {

	public ObjectNotFoundException() {
		super();
	}

	public ObjectNotFoundException(String message) {
		super(message);
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectNotFoundException(Throwable cause) {
		super(cause);
	}

}
