package org.openxava.test.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.ViewBaseAction;

/**
 * Create on 19/04/2010 (11:41:47)
 * @author Ana Andres
 */
public class NotEditableViewAction extends ViewBaseAction{
	private static Log log = LogFactory.getLog(NotEditableViewAction.class);
	
	public void execute() throws Exception {
		getView().setEditable(false);
	}

}
