package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class GetNameAction extends ViewBaseAction {

	public void execute() throws Exception {
		addMessage("name_is", getView().getValue("name"));	
	}

}
