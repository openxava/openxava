package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class DisableShortcutOptionsAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		getView().disableValidValues("shortcut");
	}

}
