package org.openxava.chat;

/**
 * Exception thrown by chat service implementations.
 * 
 * @author Javier Paniza
 * @since 7.7
 */
public class ChatException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ChatException(String message) {
		super(message);
	}
	
	public ChatException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
