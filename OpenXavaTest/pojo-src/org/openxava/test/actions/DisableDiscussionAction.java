package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class DisableDiscussionAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setEditable("discussion", false);
	}

}
