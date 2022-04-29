package org.openxava.test.actions;

import org.openxava.actions.*;

public class SetEditableOnAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setEditable(true);
	}

}
