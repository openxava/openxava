package org.openxava.test.actions;

import org.openxava.actions.*;

public class AddActionInReferenceSearchAction extends ReferenceSearchAction{

	public void execute() throws Exception {
		super.execute();
		addActions("Book.doNothing");
	}
	
}
