package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Chungyen Tsai
 *
 */
public class ShowTreeContainerAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		showDialog();
		getView().setModelName("TreeContainer");
		getView().setValue("id", 1);
		getView().refresh();
	}

}
