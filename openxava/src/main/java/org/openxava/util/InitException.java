package org.openxava.util;

/**
 * Throws when probllems on init (generally calling to <tt>init()</tt> method.>p>
 * 
 * @author Javier Paniza
 */
public class InitException extends Exception {
	
	private Object argv0;
	private Object argv1;	

	public InitException() {
		super("init_error");
	}

	/**
	 * @param idOrMessage Id for look messsage in XavaResources, or text of the
	 * 		message
	 */
	public InitException(String idOrMessage) {
		super(idOrMessage);		
	}
	
	public InitException(String idOrMessage, Object argv0) {
		super(idOrMessage);
		this.argv0 = argv0;
	}
		
	public InitException(String idOrMessage, Object argv0, Object argv1) {
		super(idOrMessage);
		this.argv0 = argv0;
		this.argv1 = argv1;
	}
	
	
	public String getMessage() {
		return getLocalizedMessage();
	}
	
	public String getLocalizedMessage() {
		if (argv1 != null) {
			return XavaResources.getString(super.getMessage(), argv0, argv1);
		}
		if (argv0 != null) return XavaResources.getString(super.getMessage(), argv0);
		return XavaResources.getString(super.getMessage());
	}
	
}
