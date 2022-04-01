package org.openxava.validators.hibernate;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * To use in validators in this package, in order to avoid 
 * excessive objects creation. <p>
 * 
 * This is a Messages object that throws a <code>IllegalStateException</code>
 * each time a message is added.<br> 
 * 
 * @author Javier Paniza
 */

class FailingMessages extends Messages {
	
	private static final long serialVersionUID = -8169129622648189554L;

	final public static String EXCEPTION_MESSAGE="xava.failing.messages.added";
	
	private static FailingMessages instance = new FailingMessages();
	
	public static FailingMessages getInstance() {
		return instance;
	}
	
	private FailingMessages() {		
	}
	
	@Override
	public void add(Messages messages) {		
		throw new IllegalStateException(EXCEPTION_MESSAGE, new ValidationException(messages));
	}
		
	@Override
	public void add(String idMessage, Object ... ids) {		
		if (ids != null && ids.length > 0) idMessage = XavaResources.getString(idMessage, ids);
		throw new IllegalStateException(EXCEPTION_MESSAGE, new ValidationException(idMessage));		
	}
}
