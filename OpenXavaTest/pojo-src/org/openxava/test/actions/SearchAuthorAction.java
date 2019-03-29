package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class SearchAuthorAction extends SearchByViewKeyAction {
	
	
	public void execute() throws Exception {
		super.execute();
		addMessage("showing_author", getView().getValue("author"));
	}

}
