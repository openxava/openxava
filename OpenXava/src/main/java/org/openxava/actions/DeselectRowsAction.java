package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public class DeselectRowsAction extends TabBaseAction {

	public void execute() throws Exception {
		getTab().deselectAll();
	}

}
