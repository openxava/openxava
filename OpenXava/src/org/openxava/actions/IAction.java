package org.openxava.actions;


import org.openxava.controller.*;
import org.openxava.util.*;


/**
 * OpenXava action.
 * 
 * It's better to extend from {@link BaseAction} instead of implementing directly this
 * interface.
 * 
 * @author Javier Paniza
 */

public interface IAction {
	
	void execute() throws Exception;
	
	void setErrors(Messages errors);
	Messages getErrors();
	
	void setMessages(Messages messages);
	Messages getMessages();
	
	void setEnvironment(Environment environment);
		
}
