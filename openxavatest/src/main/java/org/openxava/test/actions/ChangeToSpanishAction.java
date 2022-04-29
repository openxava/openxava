package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class ChangeToSpanishAction extends BaseAction {

	public void execute() throws Exception {
		getRequest().getSession().setAttribute("xava.portal.locale", new Locale("es"));
	}

}
