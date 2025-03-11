package org.openxava.web.taglib;

import javax.servlet.jsp.tagext.*;

/**
 * @author Javier Paniza
 */
public interface IActionTag extends IterationTag {

	void setAction(String string);
	void setArgv(String string);	
	
	/**
	 * Sets if the action is always available, regardless of the isAvailable() method result.
	 * @param alwaysAvailable true to make the action always available, false otherwise
	 */
	void setAlwaysAvailable(boolean alwaysAvailable);

}
