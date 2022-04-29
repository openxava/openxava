package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class ShowListTitleAction extends TabBaseAction {

	public void execute() throws Exception {
		getTab().setTitleVisible(true);
	}

}
