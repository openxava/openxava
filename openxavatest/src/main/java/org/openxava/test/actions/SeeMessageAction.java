package org.openxava.test.actions;

import org.openxava.actions.ViewBaseAction;

/**
 * Create on 09/05/2008 (11:44:51)
 * @autor Ana Andr�s
 */

public class SeeMessageAction extends ViewBaseAction{

	public void execute() throws Exception {
		addMessage("a_message", "'A.B.C'");
	}

}
