package org.openxava.util;

import java.rmi.*;

/**
 * RuntimeException to be thrown by a business method
 * when a system level problem is produced. <p>
 * 
 * A system level exception can be a problem with JDBC,
 * the JPA persistence manager, JNDI, RMI, net communication, 
 * hardware or any other problem not related directly with
 * the business logic associated to the method.<p> 
 * 
 * This exception is a facility, you can use whatever
 * runtime exception you prefer for indicate a system exception.<p>
 * 
 * @author Javier Paniza
 */

public class SystemException extends RuntimeException {

	public SystemException() {
		super();
	}

	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public SystemException(String message) {
		super(message);
	}

	public SystemException(Throwable cause) {
		super(cause);
	}
	
	public SystemException(RemoteException cause) { 
		super(cause.getLocalizedMessage());
	}	
	
}
