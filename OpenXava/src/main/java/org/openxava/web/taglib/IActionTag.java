package org.openxava.web.taglib;

import javax.servlet.jsp.tagext.*;

/**
 * @author Javier Paniza
 */
public interface IActionTag extends IterationTag {

	void setAction(String string);
	void setArgv(String string);	

}
