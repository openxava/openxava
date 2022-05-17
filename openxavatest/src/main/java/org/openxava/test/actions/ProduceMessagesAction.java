package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 *
 * @author Javier Paniza
 */

public class ProduceMessagesAction extends BaseAction {
	
	public void execute() throws Exception {
		addMessage("this_is_a_message");
		addError("this_is_an_error");
		addInfo("this_is_an_info");
		addWarning("this_is_a_warning");
	}

}
