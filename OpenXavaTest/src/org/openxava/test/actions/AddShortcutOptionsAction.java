package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class AddShortcutOptionsAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		getView().addValidValue("shortcut", "a", "AA");
		getView().addValidValue("shortcut", "b", "BB");
	}

}
