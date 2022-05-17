package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class HideButtonsAction extends BaseAction {

	public void execute() throws Exception {
		getManager().hideButtons();
	}

}
