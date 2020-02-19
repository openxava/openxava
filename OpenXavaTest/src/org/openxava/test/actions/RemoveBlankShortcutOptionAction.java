package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class RemoveBlankShortcutOptionAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		getView().removeBlankValidValue("shortcut");
	}

}
