package org.openxava.test.actions;

import org.openxava.actions.*;

public class SetEditableOffAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setEditable(false);
	}

}
